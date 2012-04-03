/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.experiment.evaluation.retrieval;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openimaj.experiment.dataset.Identifiable;
import org.openimaj.experiment.evaluation.AnalysisResult;
import org.openimaj.experiment.evaluation.Evaluator;

/**
 * An implementation of an {@link Evaluator} for the evaluation of
 * retrieval experiments using the Cranfield methodology.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 * @param <R> Type of analysed data
 * @param <D> Type of documents
 * @param <Q> Type of query
 */
public class RetrievalEvaluator<R extends AnalysisResult, D extends Identifiable, Q> implements Evaluator<Map<Q, List<D>>, R> {
	protected RetrievalEngine<D, Q> engine;
	protected Collection<Q> queries;
	protected Map<Q, Set<D>> relevant; //in the future we might want a model more like trec qrels with relevance levels
	protected RetrievalAnalyser<R, Q, D> analyser;
	
	/**
	 * Construct a new {@link RetrievalEvaluator} with a search engine,
	 * a set of queries to perform, relevant documents for each query,
	 * and a {@link RetrievalAnalyser} to analyse the results.
	 * 
	 * @param engine the query engine
	 * @param queries the queries
	 * @param relevant the relevant documents for each query
	 * @param analyser the analyser
	 */
	public RetrievalEvaluator(RetrievalEngine<D, Q> engine, Collection<Q> queries, Map<Q, Set<D>> relevant, RetrievalAnalyser<R, Q, D> analyser) {
		this.engine = engine;
		this.queries = queries;
		this.relevant = relevant;
		this.analyser = analyser;
	}
	
	/**
	 * Construct a new {@link RetrievalEvaluator} with a search engine,
	 * relevant documents for each query, and a {@link RetrievalAnalyser} 
	 * to analyse the results. The queries are determined automatically
	 * from the keys of the map of relevant documents.
	 * 
	 * @param engine the query engine
	 * @param relevant the relevant documents for each query
	 * @param analyser the analyser
	 */
	public RetrievalEvaluator(RetrievalEngine<D, Q> engine, Map<Q, Set<D>> relevant, RetrievalAnalyser<R, Q, D> analyser) {
		this.engine = engine;
		this.queries = relevant.keySet();
		this.relevant = relevant;
		this.analyser = analyser;
	}
	
	/**
	 * Construct a new {@link RetrievalEvaluator} with the given ranked
	 * results lists and sets of relevant documents for each query, and 
	 * a {@link RetrievalAnalyser} to analyse the results.
	 * <p>
	 * Internally, this constructor wraps a simple {@link RetrievalEngine}
	 * implementation around the results, and determines the set of
	 * queries from the keys of the relevant document map.
	 * 
	 * @param results the ranked results per query
	 * @param relevant the relevant results per query
	 * @param analyser the analyser
	 */
	public RetrievalEvaluator(final Map<Q, List<D>> results, Map<Q, Set<D>> relevant, RetrievalAnalyser<R, Q, D> analyser) {
		this.engine = new RetrievalEngine<D, Q>() {
			@Override
			public List<D> search(Q query) {
				return results.get(query);
			}
		};
		
		this.queries = relevant.keySet();
		this.relevant = relevant;
		this.analyser = analyser;
	}
	
	@Override
	public Map<Q, List<D>> evaluate() {
		Map<Q, List<D>> results = new HashMap<Q, List<D>>();
		
		for (Q query : queries) {
			results.put(query, engine.search(query));
		}
		
		return results;
	}
	
	@Override
	public R analyse(Map<Q, List<D>> results) {
		return analyser.analyse(results, relevant);
	}
}
