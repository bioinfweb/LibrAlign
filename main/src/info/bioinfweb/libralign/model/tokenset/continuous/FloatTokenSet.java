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



/**
 * Implementation of a token set containing all possible {@code float} values.
 * <p>
 * For sets containing only a continuous subset of these, overwrite the according methods.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class FloatTokenSet extends AbstractContinuousSet<Float> {
	@Override
	public Float tokenByKeyChar(char key) {
		return tokenByRepresentation(Character.toString(key));
	}

	
	@Override
	public Float tokenByRepresentation(String representation) {
		try {
			return Float.parseFloat(representation);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	
	/**
	 * The lengths of {@code float} values depend on the notation. 
	 * 
	 * @return always {@link Integer#MAX_VALUE}
	 */
	@Override
	public int maxRepresentationLength() {
		return Integer.MAX_VALUE;
	}


	@Override
	public FloatTokenSet clone() {
		return new FloatTokenSet();  // Current implementation has no fields or properties.
	}
}
