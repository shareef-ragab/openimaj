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
package org.openimaj.image.contour;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.contour.SuzukiContourProcessor.Border;
import org.openimaj.util.function.Function;
import org.openimaj.util.function.Predicate;

/**
 * 
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 */
public class FindAestheticode implements Function<Border, List<Aestheticode>>, Predicate<Border> {
	private static final int MAX_CHILDLESS_CHILDREN = 0;
	private static final int MAX_CHILDREN = 5;
	private static final int MIN_CHILDREN = 5;

	@Override
	public List<Aestheticode> apply(Border in) {
		final List<Aestheticode> found = new ArrayList<Aestheticode>();
		detectCode(in, found);
		return found;
	}

	private void detectCode(Border root, List<Aestheticode> found) {
		if (test(root)) {
			found.add(new Aestheticode(root));
		}
		else {
			for (final Border child : root.children) {
				detectCode(child, found);
			}
		}
	}

	@Override
	public boolean test(Border in) {
		// has at least one child
		if (in.children.size() < MIN_CHILDREN || in.children.size() > MAX_CHILDREN) {
			return false;
		}
		int childlessChild = 0;
		// all grandchildren have no children
		for (final Border child : in.children) {
			if (child.children.size() == 0)
				childlessChild++;

			if (childlessChild > MAX_CHILDLESS_CHILDREN)
				return false;
			for (final Border grandchildren : child.children) {
				if (grandchildren.children.size() != 0)
					return false;
			}
		}
		return true;
	}

}
