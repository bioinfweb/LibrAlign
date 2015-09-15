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
package info.bioinfweb.libralign.model.factory;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Implements shared functionality for alignment model factories.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type of the alignment models to be created by this instance
 */
public abstract class AbstractAlignmentModelFactory<T> implements AlignmentModelFactory<T> {
	/**
	 * Creates a token using {@link TokenSet#tokenByRepresentation(String)}.
	 * 
	 * @return the token or {@code null} if no according representation was found in the set
	 * @see info.bioinfweb.libralign.model.factory.AlignmentModelFactory#createToken(info.bioinfweb.libralign.model.AlignmentModel, java.lang.String)
	 */
	@Override
	public T createToken(AlignmentModel<T> alignmentModel, String tokenRepresentation) {
		return alignmentModel.getTokenSet().tokenByRepresentation(tokenRepresentation);
	}
}
