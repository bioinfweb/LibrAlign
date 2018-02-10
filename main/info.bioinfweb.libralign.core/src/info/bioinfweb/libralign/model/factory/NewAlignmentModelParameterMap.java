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
package info.bioinfweb.libralign.model.factory;


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.collections.ParameterMap;



/**
 * Parameter map that stores information used to create new instances of alignment models by implementations
 * of {@link AlignmentModelFactory}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class NewAlignmentModelParameterMap extends ParameterMap {
	/** The prefix of predefined keys used in this map. */
	public static final String KEY_PREFIX = "info.bioinfweb.libralign.alignmentModelFactory.";
	
	/** Key used to store the ID of the alignment model to be created. */
	public static final String KEY_ALIGNMENT_ID = KEY_PREFIX + "alignmentID";
	
	/** Key used to store the label of the alignment model to be created. */
	public static final String KEY_ALIGNMENT_LABEL = KEY_PREFIX + "alignmentLabel";
	
	/** Key used to store the return value of {@link #getCharacterStateSetType()}. */
	public static final String KEY_CHARACTER_STATE_SET_TYPE = KEY_PREFIX + "charStateSetType";
	
	/** Key used to store the return value of {@link #getDefinedTokens()}. */
	public static final String KEY_DEFINED_TOKENS = KEY_PREFIX + "tokens";
	
	/** 
	 * Key used to store an {@link Integer} or {@link Long} value defining the maximum number of states 
	 * to be supported by the new model.  
	 */
	public static final String KEY_CHARACTER_STATE_COUNT = KEY_PREFIX + "charStateCount";
	
	/** 
	 * Key used to store a {@link Boolean} value defining whether the model to be created will be part 
	 * of a concatenated alignment model.
	 * <p>
	 * This key is currently not in use, but is already defined for future support of concatenated alignment models.
	 */
	public static final String KEY_IS_PART_MODEL = KEY_PREFIX + "isPartModel";
	
	/** 
	 * Key used to store the first column index of the model to be created in its parent concatenated 
	 * alignment model. 
	 * <p>
	 * This key is currently not in use, but is already defined for future support of concatenated alignment models.
	 */
	public static final String KEY_START_INDEX = KEY_PREFIX + "startIndex";
	
	/** 
	 * Key used to store the first column index after the end of the model to be created in its parent 
	 * concatenated alignment model. 
	 * <p>
	 * This key is currently not in use, but is already defined for future support of concatenated alignment models.
	 */
	public static final String KEY_END_INDEX = KEY_PREFIX + "endIndex";
	
	
	/**
	 * Returns the character set type the alignment model to be created shall have.
	 * 
	 * @return the character set type ({@link TokenSetType#UNKNOWN} is also returned if no value is stored for the key 
	 *         {@link #KEY_CHARACTER_SET_TYPE} in this instance.)
	 */
	public CharacterStateSetType getCharacterStateSetType() {
		CharacterStateSetType result = (CharacterStateSetType)get(KEY_CHARACTER_STATE_SET_TYPE);  // Null is also cast correctly by this code.
		if (result == null) {
			result = CharacterStateSetType.UNKNOWN;
		}
		return result;
	}
	
	
	/**
	 * Sets or replaces the character set type in this map.
	 * 
	 * @param type the character set type the alignment model to be created shall have
	 */
	public void setCharacterStateSetType(CharacterStateSetType type) {
		put(KEY_CHARACTER_STATE_SET_TYPE, type);
	}
	
	
	/**
	 * Returns a list of defined Tokens (e.g. for gaps or unknown characters). 
	 * <p>
	 * After calling this method, it is guaranteed that this maps contains an list to {@link #KEY_DEFINED_TOKENS}
	 * which is returned. The returned list may be modified. 
	 * 
	 * @return the list of according single token definition events (Maybe empty but is never {@code null}.)
	 */
	@SuppressWarnings("unchecked")
	public List<TokenDefinition> getDefinedTokens() {
		List<TokenDefinition> result = (List<TokenDefinition>)get(KEY_DEFINED_TOKENS);
		if (result == null) {
			result = new ArrayList<TokenDefinition>();
			put(KEY_DEFINED_TOKENS, result);
		}
		return result;
	}
}
