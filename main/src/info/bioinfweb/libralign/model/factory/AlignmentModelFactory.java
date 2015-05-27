/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben StÃ¶ver
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
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Interface to be implemented by classes creating instance of implementations of {@link AlignmentModel}.
 * Alignment model factories are e.g. needed in combination with {@link AlignmentDataReader} to read 
 * alignments from files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the token type of the created alignment model
 */
public interface AlignmentModelFactory<T> {
	/**
	 * Creates a new instance of an alignment model according to the specified parameters.
	 * 
	 * @param parameterMap a map with parameters describing the requirements to be met by the returned instance
	 * @return a new instance of an alignment model or {@code null} if this factory is not able to create an
	 *         object that meets the specified requirements
	 */
	public AlignmentModel<T> createNewModel(NewAlignmentModelParameterMap parameterMap);
	
	/**
	 * Creates a token object associated with the specified string representation. Implementations may use 
	 * {@link TokenSet#tokenByRepresentation(String)} of the token set associated with the specified alignment 
	 * model to determine the return value, but could also implement a different or extended behavior. 
	 * 
	 * @param alignmentModel the alignment model which will hold the returned token
	 * @param tokenRepresentation the string representation of the token to be returned (e.g. read from
	 *        an alignment file)
	 * @return the new token instance or {@code null} if no according token could be generated by this
	 *         factory
	 */
	public T createToken(AlignmentModel<T> alignmentModel, String tokenRepresentation);
}
