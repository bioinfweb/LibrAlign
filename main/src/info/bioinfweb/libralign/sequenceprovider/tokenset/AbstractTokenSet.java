/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.sequenceprovider.tokenset;


import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;



/**
 * An abstract implementation of {@link TokenSet} that provides a {@link TreeMap} used to map key characters
 * to tokens and inherits from {@link HashSet} so also tokens that do not implement {@link Comparable} are
 * supported.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the type of token to be stored in this set
 */
public abstract class AbstractTokenSet<T> extends HashSet<T> implements TokenSet<T> {
	private Map<Character, T> keyMap = new TreeMap<Character, T>();
	
	
	@Override
	public T tokenByKeyChar(char key) {
		return keyMap.get(key);
	}


	/**
	 * Returns the mapping from key characters to tokens.
	 * 
	 * @return an instance of {@link TreeMap} in the current implementation (Note that this might change in 
	 *         future versions.)
	 */
	protected Map<Character, T> getKeyMap() {
		return keyMap;
	}


	/**
	 * Inherited classes have to overwrite this method and create a deep copy of {@link #getKeyMap()}.
	 */
	@Override
	public abstract AbstractTokenSet<T> clone();
}
