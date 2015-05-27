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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetType;



/**
 * Parameter map that stores information used to create new instances of alignment models by implementations
 * of {@link AlignmentModelFactory}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class NewAlignmentModelParameterMap extends ParameterMap {
	public static final String KEY_PREFIX = "info.bioinfweb.libralign.alignmentModelFactory.";
	public static final String KEY_CHARACTER_STATE_SET_TYPE = KEY_PREFIX + "charStateSetType";
	public static final String KEY_DEFINED_TOKENS = KEY_PREFIX + "tokens";
	public static final String KEY_CHARACTER_STATE_COUNT = KEY_PREFIX + "isPartModel";
	public static final String KEY_IS_PART_MODEL = KEY_PREFIX + "isPartModel";
	public static final String KEY_START_INDEX = KEY_PREFIX + "startIndex";
	public static final String KEY_END_INDEX = KEY_PREFIX + "endIndex";
	
	
	/**
	 * Returns the character set type the alignment model to be created shall have.
	 * 
	 * @return the character set type of {@code null} if no value is stored for the key 
	 *         {@link #KEY_CHARACTER_SET_TYPE} in this instance
	 */
	public TokenSetType getCharacterStateSetType() {
		return (TokenSetType)get(KEY_CHARACTER_STATE_SET_TYPE);  // Null is also returned correctly by this code.
	}
	
	
	/**
	 * Sets or replaces the character set type in this map.
	 * 
	 * @param type the character set type the alignment model to be created shall have
	 */
	public void setCharacterStateSetType(TokenSetType type) {
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
	public List<SingleTokenDefinitionEvent> getDefinedTokens() {
		List<SingleTokenDefinitionEvent> result = (List<SingleTokenDefinitionEvent>)get(KEY_DEFINED_TOKENS);
		if (result == null) {
			result = new ArrayList<SingleTokenDefinitionEvent>();
			put(KEY_DEFINED_TOKENS, result);
		}
		return result;
	}
}
