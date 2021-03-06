/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.tokenset.continuous;




/**
 * Implementation of a token set containing all possible {@code float} values.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class FloatTokenSet extends AbstractContinuousSet<Float> {
	public static final int FLOAT_MAX_REPRESENTATION_LENGTH = "-3.4028235E38".length();
	
	
	@Override
	protected ContinuousToken<Float> parseValue(String value)	throws NumberFormatException {
		return new ContinuousToken<Float>(Float.parseFloat(value));
	}


	/**
	 * This method returns the length of 8 significant digits in exponential notation which is the notation this token
	 * set uses to generate string representations.  
	 * 
	 * @return the length of {@code -3.4028235E38}
	 */
	@Override
	public int maxRepresentationLength() {
		return FLOAT_MAX_REPRESENTATION_LENGTH;
	}


	@Override
	public FloatTokenSet clone() {
		return new FloatTokenSet();  // Current implementation has no fields or properties.
	}
}
