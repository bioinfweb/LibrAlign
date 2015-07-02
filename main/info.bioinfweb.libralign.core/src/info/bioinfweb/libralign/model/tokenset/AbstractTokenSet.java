/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateType;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.KeyStroke;



/**
 * An abstract implementation of {@link TokenSet} that provides a {@link TreeMap} used to map key characters
 * to tokens and inherits from {@link HashSet} so also tokens that do not implement {@link Comparable} are
 * supported.
 * <p>
 * Since the implemented functionality of this class assumes discrete token values, {@link #isContinuous()} 
 * always returns {@code false}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the type of token to be stored in this set
 */
public abstract class AbstractTokenSet<T> extends HashSet<T> implements TokenSet<T> {
	public static final char DEFAULT_GAP_REPRESENTATION = '-';
	
	
	private CharacterStateType type;
	private Map<KeyStroke, T> keyMap = new TreeMap<KeyStroke, T>();
	
	
	/**
	 * Creates a new empty instance of this class.
	 */
	public AbstractTokenSet(CharacterStateType type) {
		super();
		this.type = type;
	}


	/**
	 * Copy constructor that copies all elements the contents of the key map from source to the new instance.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 * @param source the source instance to be cloned
	 */
	public AbstractTokenSet(CharacterStateType type, AbstractTokenSet<T> source) {
		this(type);
		addAll(source);
		getKeyMap().putAll(source.getKeyMap());
	}


	@Override
	public CharacterStateType getType() {
		return type;
	}


	@Override
	public T tokenByKeyStroke(KeyStroke key) {
		return keyMap.get(key);
	}


	/**
	 * Returns the mapping from key characters to tokens.
	 * 
	 * @return an instance of {@link TreeMap} in the current implementation (Note that this might change in 
	 *         future versions.)
	 */
	protected Map<KeyStroke, T> getKeyMap() {
		return keyMap;
	}


	/**
	 * Default implementation that tests of the string representation of the specified token is equal to
	 * "{@value #DEFAULT_GAP_REPRESENTATION}".
	 * 
	 * @see info.bioinfweb.libralign.model.tokenset.TokenSet#isGapToken(java.lang.Object)
	 */
	@Override	
	public boolean isGapToken(T token) {
		return TokenSetTools.isGapToken(this, token);
	}


	@Override
	public T getGapToken() {
		return tokenByRepresentation(Character.toString(DEFAULT_GAP_REPRESENTATION));
	}


	/**
	 * Inherited classes have to overwrite this method and create a deep copy of {@link #getKeyMap()}.
	 */
	@Override
	public abstract AbstractTokenSet<T> clone();
	
	
	protected void addSpaceKeyForGaps() {
		getKeyMap().put(KeyStroke.getKeyStroke(' '), getGapToken());
	}
}
