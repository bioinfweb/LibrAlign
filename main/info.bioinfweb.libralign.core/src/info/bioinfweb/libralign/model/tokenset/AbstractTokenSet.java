/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.tokenset;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.commons.bio.SequenceUtils;

import java.awt.event.KeyEvent;
import java.util.HashMap;
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
	private static final long serialVersionUID = 1L;
	

	private CharacterStateSetType type;
	private Map<KeyStroke, T> keyMap = new HashMap<KeyStroke, T>();
	private boolean spaceForGap = true;
	
	
	/**
	 * Creates a new empty instance of this class.
	 */
	public AbstractTokenSet(CharacterStateSetType type) {
		super();
		this.type = type;
	}


	/**
	 * Copy constructor that copies all elements the contents of the key map from source to the new instance.
	 * 
	 * @param type the token type of the new instance (Only a discrete type would make sense for this class.)
	 * @param source the source instance to be cloned
	 */
	public AbstractTokenSet(CharacterStateSetType type, AbstractTokenSet<T> source) {
		this(type);
		addAll(source);
		getKeyMap().putAll(source.getKeyMap());
	}


	@Override
	public CharacterStateSetType getType() {
		return type;
	}


	@Override
	public T tokenByKeyStroke(KeyStroke key) {
		T result = keyMap.get(key);
		if (isSpaceForGap() && (result == null) && (key.getKeyCode() == KeyEvent.VK_SPACE)) {
			result = getGapToken();
		}
		return result;
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
		return tokenByRepresentation(Character.toString(SequenceUtils.GAP_CHAR));
	}


	@Override
	public boolean isSpaceForGap() {
		return spaceForGap;
	}


	@Override
	public void setSpaceForGap(boolean spaceForGap) {
		this.spaceForGap = spaceForGap;
	}


	@Override
	public boolean isMissingInformationToken(T token) {
		return TokenSetTools.isMissingInformationToken(this, token);
	}


	@Override
	public T getMissingInformationToken() {
		return tokenByRepresentation(Character.toString(SequenceUtils.MISSING_DATA_CHAR));
	}


	/**
	 * This default implementation returns {@link CharacterSymbolType#UNCERTAIN} for all missing information tokens.
	 * If {@link #getType()} specifies a nucleotide or amino acid token set, {@link CharacterSymbolType#UNCERTAIN} is returned
	 * for all tokens which have a IUPAC ambiguity code string representation (as returned by 
	 * {@link #representationByToken(Object)}). In all other cases {@link CharacterSymbolType#ATOMIC_STATE} is returned.
	 * <p>
	 * Inherited classes should overwrite this method, if other uncertain or any polymorphic tokens are present.
	 */
	@Override
	public CharacterSymbolType getSymbolType(T token) {
		return TokenSetTools.getSymbolType(this, token);
	}


	@Override
	public CharacterSymbolMeaning getMeaning(T token) {
		return TokenSetTools.getMeaning(this, token);
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
