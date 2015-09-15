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
import info.bioinfweb.libralign.model.exception.InvalidTokenException;
import info.bioinfweb.libralign.model.implementations.ArrayListAlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.DefaultTokenSet;



/**
 * Default factory implementation for creating alignment models. If a modified behavior is needed application
 * developers can overwrite methods of this class to catch according cases and delegate the remaining to the
 * super implementation.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class StringAlignmentModelFactory implements AlignmentModelFactory<String> {
	public static final int DEFAULT_AMINO_ACID_CHARACTER_STATE_COUNT = 32;  // 23 amino acids and stop are already more than 16.
	
	
	/**
	 * Creates a new instance of {@link PackedAlignmentModel} if the number of tokens is specified (using 
	 * {@link NewAlignmentModelParameterMap#KEY_CHARACTER_STATE_COUNT}) or {@link ArrayListAlignmentModel} if not.
	 * 
	 * @param parameterMap a map with parameters describing the requirements to be met by the returned instance
	 * @return a new instance of an alignment model
	 */
	@Override
	public AlignmentModel<String> createNewModel(NewAlignmentModelParameterMap parameterMap) {
		// Create token set:
		DefaultTokenSet<String> discreteTokenSet = new DefaultTokenSet<String>(parameterMap.getCharacterStateSetType());
		for (TokenDefinition tokenDefinition : parameterMap.getDefinedTokens()) {
			discreteTokenSet.add(tokenDefinition.getRepresentation());  //TODO Should the meaning be modeled in the token set?
		}
		
		// Create model:
		long charStateCount = parameterMap.getLong(NewAlignmentModelParameterMap.KEY_CHARACTER_STATE_COUNT, -1);
		if ((charStateCount == -1) || (charStateCount > Integer.MAX_VALUE)) {
			return new ArrayListAlignmentModel<String>(discreteTokenSet);  //TODO Only tokens that have explicitly been defined will be loaded that way.
		}
		else {
			return new PackedAlignmentModel<String>(discreteTokenSet, (int)charStateCount);
		}
	}

	
	/**
	 * Creates the token using the token set of the specified alignment model.
	 * <p>
	 * Overwrite this method to support more or different token string representations than the token set of the 
	 * alignment model. 
	 *  
	 * @return the new token
	 * @throws InvalidTokenException if no token for the specified representation could be found in the token set
	 *         of the specified alignment model
	 */
	@Override
	public String createToken(AlignmentModel<String> alignmentModel, String tokenRepresentation) {
		return tokenRepresentation;
	}	
}
