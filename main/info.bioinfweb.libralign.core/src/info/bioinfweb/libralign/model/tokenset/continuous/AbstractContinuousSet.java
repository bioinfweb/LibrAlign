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


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.KeyStroke;



/**
 * Implements type independent functionality for continuous token sets.
 * <p>
 * This implementation does not support the optional add and remove operations. Because it represents non-discrete
 * values the methods {@link #toArray()}, {@link #toArray(Object[])} and {@link #iterator()} cannot be implemented
 * and return an empty array or iterator. {@link #size()} always returns {@link Integer#MAX_VALUE} in accordance
 * with the definition in {@link Collection#size()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the type of tokens represented by this set
 */
public abstract class AbstractContinuousSet<T extends Number & Comparable<T>> implements TokenSet<ContinuousToken<T>> {
	private boolean spaceForGap = true;
	
	
	protected abstract ContinuousToken<T> parseValue(String value) throws NumberFormatException;
	
	
	@Override
	public ContinuousToken<T> tokenByRepresentation(String representation) {
		if (Character.toString(SequenceUtils.GAP_CHAR).equals(representation)) {
			return getGapToken();
		}
		else if (Character.toString(SequenceUtils.MISSING_DATA_CHAR).equals(representation)) {
			return getMissingInformationToken();
		}
		else {
			try {
				return parseValue(representation);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
	}

	
	@Override
	public ContinuousToken<T> tokenByKeyStroke(KeyStroke key) {
		ContinuousToken<T> result = tokenByRepresentation(Character.toString(key.getKeyChar()));
		if (isSpaceForGap() && (result == null) && (key.getKeyCode() == KeyEvent.VK_SPACE)) {
			result = getGapToken();
		}
		return result;
	}

	
	@Override
	public boolean isGapToken(ContinuousToken<T> token) {
		return (token != null) && (token.getMeaning().equals(CharacterSymbolMeaning.GAP));
	}


	@Override
	public ContinuousToken<T> getGapToken() {
		return ContinuousToken.newGapInstance();
	}


	@Override
	public boolean isMissingInformationToken(ContinuousToken<T> token) {
		return (token != null) && (token.getMeaning().equals(CharacterSymbolMeaning.MISSING));
	}


	@Override
	public ContinuousToken<T> getMissingInformationToken() {
		return ContinuousToken.newMissingInformationInstance();
	}


	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean add(ContinuousToken<T> e) {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean addAll(Collection<? extends ContinuousToken<T>> c) {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * This default implementation always returns {@code true}.
	 * 
	 * @return always {@code true}
	 */
	@Override
	public boolean contains(Object o) {
		return true;
	}

	
	/**
	 * This default implementation always returns {@code true}.
	 * 
	 * @return always {@code true}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return true;
	}

	
	/**
	 * This default implementation always returns {@code false}.
	 * 
	 * @return always {@code false}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	
	/**
	 * Continuous sets cannot be iterated, therefore this method is not supported by this class.
	 * 
	 * @return always an empty iterator
	 */
	@Override
	public Iterator<ContinuousToken<T>> iterator() {
		return Collections.emptyIterator();
	}
	

	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * This method is not supported by this implementation and will always throw an {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("This continuous set does not support adding or removing single elements.");
	}

	
	/**
	 * The number of elements in a continuous set is unlimited.
	 * 
	 * @return always {@link Integer#MAX_VALUE} in accordance with the definition of {@link Collection#size()}
	 */
	@Override
	public int size() {
		return Integer.MAX_VALUE;
	}

	
	/**
	 * Continuous sets cannot be stored in arrays, therefore this method is not supported by this class.
	 * 
	 * @return always an empty array
	 */
	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	
	/**
	 * Continuous sets cannot be stored in arrays, therefore this method is not supported by this class.
	 * 
	 * @param a the array into which the elements of this set would be stored, if it was not continuous
	 * @return {@code a} without any modifications
	 */
	@Override
	public <E> E[] toArray(E[] a) {
		return a;
	}

	
	@Override
	public String representationByToken(ContinuousToken<T> token) {
		if (isGapToken(token)) {
			return Character.toString(SequenceUtils.GAP_CHAR);
		}
		else if (isMissingInformationToken(token)) {
			return Character.toString(SequenceUtils.MISSING_DATA_CHAR);
		}
		else {
			return token.toString();
		}
	}
	

	@Override
	public boolean representationLengthEqual() {
		return false;
	}

	
	@Override
	public String descriptionByToken(ContinuousToken<T> token) {
		return representationByToken(token);
	}

	
	@Override
	public CharacterStateSetType getType() {
		return CharacterStateSetType.CONTINUOUS;
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
	public abstract AbstractContinuousSet<T> clone();
}
