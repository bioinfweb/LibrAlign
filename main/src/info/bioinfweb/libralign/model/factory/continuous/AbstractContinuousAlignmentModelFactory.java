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
package info.bioinfweb.libralign.model.factory.continuous;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.InvalidTokenException;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;



/**
 * Implements basic functionality for factories working with continuous values for tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type of the created alignment model
 */
public abstract class AbstractContinuousAlignmentModelFactory<T extends Number> implements AlignmentModelFactory<T> {
	protected abstract T parseValue(String tokenRepresentation);
	
	
	@Override
	public T createToken(AlignmentModel<T> alignmentModel, String tokenRepresentation) {
		try {
			return parseValue(tokenRepresentation);
		}
		catch (NumberFormatException e) {
			throw new InvalidTokenException(alignmentModel, tokenRepresentation);
		}
	}
}
