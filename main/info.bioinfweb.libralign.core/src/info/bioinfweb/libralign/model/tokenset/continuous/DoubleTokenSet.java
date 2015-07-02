/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.tokenset.continuous;


import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import info.bioinfweb.libralign.model.tokenset.AbstractTokenSet;



/**
 * Implementation of a token set containing all possible {@code double} values. Gaps are represented by {@link Double#NaN}
 * in this set.
 * <p>
 * For sets containing only a continuous subset of these, overwrite the according methods.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class DoubleTokenSet extends AbstractContinuousSet<Double> {
	public static final int DOUBLE_MAX_REPRESENTATION_LENGTH = "-1.7976931348623157E308".length();
	
	
	@Override
	public Double tokenByKeyStroke(KeyStroke key) {
		Double result = tokenByRepresentation(Character.toString(key.getKeyChar()));
		if (isSpaceForGap() && (result == null) && (key.getKeyCode() == KeyEvent.VK_SPACE)) {
			result = getGapToken();
		}
		return result;
	}

	
	@Override
	public Double tokenByRepresentation(String representation) {
		if (Character.toString(AbstractTokenSet.DEFAULT_GAP_REPRESENTATION).equals(representation)) {
			return Double.NaN;
		}
		else {
			try {
				return Double.parseDouble(representation);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
	}

	
	/**
	 * This method returns the length of 17 significant digits in exponential notation which is the notation this token
	 * set uses to generate string representations.  
	 * 
	 * @return the length of {@code -1.7976931348623157E308}
	 */
	@Override
	public int maxRepresentationLength() {
		return DOUBLE_MAX_REPRESENTATION_LENGTH;
	}


	@Override
	public boolean isGapToken(Double token) {
		return token.isNaN();
	}


	@Override
	public Double getGapToken() {
		return Double.NaN;
	}


	@Override
	public DoubleTokenSet clone() {
		return new DoubleTokenSet();  // Current implementation has no fields or properties.
	}
}
