/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations.decorate;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Abstract base class for implementations of {@link AlignmentModelView} that simply replace a set
 * of tokens by other tokens of the same type. It delegates all methods to the underlying model
 * except {@link #getTokenAt(int, int)} which calls the abstract method {@link #convertToken(Object)}
 * to perform the actual translation.
 * <p>
 * It is still possible to write tokens that are translated to the underlying data source. Note that
 * in that case {@link #getTokenAt(int, int)} would return a different token than the one passed to a 
 * previously call of {@link #setTokenAt(int, int, Object)} or {@link #insertTokenAt(int, int, Object)}
 * or similar methods called with the same index value.
 * <p>
 * Note that classes inherited from this class are not meant to apply length changes. Most methods will
 * not work correctly or throw exceptions, if index conversion is used.
 * <p>
 * Inherited classes that add or hide sequences from or to the underlying model, will need to overwrite
 * {@link #getMaxSequenceLength()}. Classes that hide sequences will have to overwrite no additional methods,
 * classes adding sequences, will need to overwrite all methods with a sequence ID as parameter to handle 
 * additional sequence not present in the underlying model.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the type of sequence elements (tokens) the implementing decorator works with
 * @param <U> the type of sequence elements (tokens) the underlying model works with
 */
public abstract class AbstractTokenReplacementAlignmentModelDecorator<T, U> extends AbstractAlignmentModelDecorator<T, U> {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tokenSet the token set to be used by the decorator
	 * @param underlyingModel the underlying model to be decorated
	 */
	public AbstractTokenReplacementAlignmentModelDecorator(TokenSet<T> tokenSet, AlignmentModel<U> underlyingModel) {
		super(tokenSet, underlyingModel);
	}


	/**
	 * Inherited classes should perform the token translation from an underlying to a viewed token
	 * by implementing this method.
	 * 
	 * @param underlyingSequenceID the ID of the sequence in the underlying model containing the token to be converted 
	 * @param underlyingIndex the column index of the token to be converted in the underlying model
	 * @param underlyingToken the token stored in (or shown by) the underlying provider
	 * @return the token that shall be shown instead
	 */
	protected abstract T convertUnderlyingToken(String underlyingSequenceID, int underlyingIndex, U underlyingToken);

	
	/**
	 * Inherited classes should perform the token translation from a viewed to an underlying token
	 * by implementing this method.
	 * 
	 * @param viewedSequenceID the ID of the sequence in this decorator containing the token to be converted 
	 * @param viewedIndex the column index of the token to be converted in this view (Note that this index may be behind
	 *        current end of the sequence, of this method is used to elongate a sequence.)
	 * @param decoratedToken the token shown in the view instance 
	 * @return the token to be stored in (or shown by) the underlying provider
	 */
	protected abstract U convertDecoratedToken(String viewedSequenceID, int viewedIndex, T decoratedToken);
	
	
	/**
	 * Converts a {@link SequenceChangeEvent} event from the decorated instance to an event object compatible
	 * with this instance.
	 * <p>
	 * This default implementation returns a new event object with this instance as its owner and a sequence ID 
	 * converted using {@link #convertUnderlyingSequenceID(int)}. If the ID conversion returns -1, {@code null} is returned
	 * by this method.
	 * <p>
	 * Inherited classes may overwrite this method to implement a different behavior, but in most cases it should
	 * be sufficient to overwrite {@link #convertUnderlyingSequenceID(int)} to change the behavior of this method.
	 * 
	 * @param event the event from the underlying (decorated) model
	 * @return a new converted event object or {@code null} if the specified underlying sequence is hidden by this decorator
	 * @throws IllegalArgumentException if {@link SequenceChangeEvent#getType()} return anything else than
	 *         {@link ListChangeType#INSERTION} or {@link ListChangeType#DELETION}. (Events fired by models
	 *         of LibrAlign never have any other type.)
	 */
	protected SequenceChangeEvent<T> convertSequenceChangeEvent(SequenceChangeEvent<U> event) {
		String decoratedID = convertUnderlyingSequenceID(event.getSequenceID());
		if (decoratedID != null) {
			switch (event.getType()) {
				case INSERTION:
					return SequenceChangeEvent.newInsertInstance(event.getIndex(), this, decoratedID, event.getSequenceName());
				case DELETION:
					return SequenceChangeEvent.newRemoveInstance(event.getIndex(), this, decoratedID, convertUnderlyingTokens(event.getSequenceID(), 0, event.getDeletedContent()));
				default:  // Just in case more valid types are added in the future.
					throw new IllegalArgumentException("The change type \"" + event.getType() + " is not supported.");
			}
		}
		else {
			return null;
		}
	}

	
	protected Iterable<TokenChangeEvent<T>> convertTokenChangeEvent(TokenChangeEvent<U> event) {
		List<TokenChangeEvent<T>> result = new ArrayList<TokenChangeEvent<T>>(1);
		String decoratedID = convertUnderlyingSequenceID(event.getSequenceID());
		if (decoratedID != null) {
			TokenChangeEvent<T> newEvent;
			switch (event.getType()) {
				case INSERTION:
					newEvent = TokenChangeEvent.newInsertInstance(this, convertUnderlyingSequenceID(event.getSequenceID()), 
							convertUnderlyingTokenIndex(event.getSequenceID(), event.getStartIndex()), event.isLeftBound(), 
							convertUnderlyingTokens(event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens()));
					break;
				case DELETION:
					newEvent = TokenChangeEvent.newRemoveInstance(this, convertUnderlyingSequenceID(event.getSequenceID()), 
							convertUnderlyingTokenIndex(event.getSequenceID(), event.getStartIndex()), 
							convertUnderlyingTokens(event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens())); 
					break;
				case REPLACEMENT:
					newEvent = TokenChangeEvent.newReplaceInstance(this, convertUnderlyingSequenceID(event.getSequenceID()), 
							convertUnderlyingTokenIndex(event.getSequenceID(), event.getStartIndex()), 
							convertUnderlyingTokens(event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens()), convertUnderlyingTokens(event.getSequenceID(), event.getStartIndex(), event.getNewTokens())); 
					break;
				default:  // Just in case more types are added in the future.
					throw new IllegalArgumentException("The change type \"" + event.getType() + " is not supported.");
			}
			result.add(newEvent);
		}
		return result;
	}


	protected Collection<T> convertUnderlyingTokens(String underlyingSequenceID, int underlyingIndex, 
			Collection<? extends U> underlyingTokens) {
		
		Collection<T> result;
		if (getTokenSet().getType().equals(CharacterStateSetType.CONTINUOUS) || 
				getTokenSet().getType().equals(CharacterStateSetType.UNKNOWN)) {
			
			result = new ArrayList<T>(underlyingTokens.size());
		}
		else {
			result = new PackedObjectArrayList<T>(getTokenSet().size(), underlyingTokens.size());
		}
		
		for (U token : underlyingTokens) {
			result.add(convertUnderlyingToken(underlyingSequenceID, underlyingIndex, token));
		}
		return result;
	}
	
	
	protected Collection<U> convertDecoratedTokens(String viewedSequenceID, int viewedIndex, 
			Collection<? extends T> viewedTokens) {
		
		Collection<U> result;
		if (getUnderlyingModel().getTokenSet().getType().equals(CharacterStateSetType.CONTINUOUS) || 
				getUnderlyingModel().getTokenSet().getType().equals(CharacterStateSetType.UNKNOWN)) {
			
			result = new ArrayList<U>(viewedTokens.size());
		}
		else {
			result = new PackedObjectArrayList<U>(getTokenSet().size(), viewedTokens.size());
		}
		
		for (T token : viewedTokens) {
			result.add(convertDecoratedToken(viewedSequenceID, viewedIndex, token));
		}
		return result;
	}
	
	
	@Override
	public T getTokenAt(String sequenceID, int index) {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, index);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				return convertUnderlyingToken(underlyingID, underlyingIndex, 
						getUnderlyingModel().getTokenAt(underlyingID, underlyingIndex));
			}
			else {
				throw new InternalError("An index shift between the decorated (" + index + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void setTokenAt(String sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, index);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().setTokenAt(underlyingID, underlyingIndex, 
						convertDecoratedToken(sequenceID, index, token));
			}
			else {
				throw new InternalError("An index shift between the decorated (" + index + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void setTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, beginIndex);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().setTokensAt(underlyingID, underlyingIndex, 
						convertDecoratedTokens(sequenceID, beginIndex, tokens));
			}
			else {
				throw new InternalError("An index shift between the decorated (" + beginIndex + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void appendToken(String sequenceID, T token, boolean leftBound) throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			getUnderlyingModel().appendToken(underlyingID, convertDecoratedToken(sequenceID, 
					getUnderlyingModel().getSequenceLength(underlyingID), token), leftBound);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void appendTokens(String sequenceID, Collection<? extends T> tokens, boolean leftBound) throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			getUnderlyingModel().appendTokens(underlyingID, convertDecoratedTokens(sequenceID, 
					getUnderlyingModel().getSequenceLength(underlyingID), tokens), leftBound);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void insertTokenAt(String sequenceID, int index, T token, boolean leftBound)	throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, index);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().insertTokenAt(underlyingID, underlyingIndex, 
						convertDecoratedToken(sequenceID, index, token), leftBound);
			}
			else {
				throw new InternalError("An index shift between the decorated (" + index + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void insertTokensAt(String sequenceID, int beginIndex, Collection<? extends T> tokens, boolean leftBound)
			throws AlignmentSourceNotWritableException {
		
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, beginIndex);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().insertTokensAt(underlyingID, underlyingIndex, 
						convertDecoratedTokens(sequenceID, beginIndex, tokens), leftBound);
			}
			else {
				throw new InternalError("An index shift between the decorated (" + beginIndex + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void removeTokenAt(String sequenceID, int index) throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, index);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().removeTokenAt(underlyingID, underlyingIndex);
			}
			else {
				throw new InternalError("An index shift between the decorated (" + index + ") and the underlying index (" + 
						underlyingIndex + ") happened, although index shifts are not allowed in this class.");
			}
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void removeTokensAt(String sequenceID, int beginIndex, int endIndex) throws AlignmentSourceNotWritableException {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			getUnderlyingModel().removeTokensAt(underlyingID, beginIndex, endIndex);  // Indices are not converted here, because this model does not perform index conversions.
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public int getSequenceLength(String sequenceID) {
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			return getUnderlyingModel().getSequenceLength(underlyingID);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	/**
	 * This default implementation delegates to the underlying model.
	 * <p>
	 * Inherited classes that change the sequence set (add or hide sequences) must overwrite this method
	 * in order to consider only relevant sequences when calculating the maximum.
	 */
	@Override
	public int getMaxSequenceLength() {
		return getUnderlyingModel().getMaxSequenceLength();
	}


	@Override
	public AlignmentModelWriteType getWriteType() {
		return getUnderlyingModel().getWriteType();
	}


	@Override
	public boolean isTokensReadOnly() {
		return getUnderlyingModel().isTokensReadOnly();
	}


	@Override
	public String renameSequence(String sequenceID, String newSequenceName) throws AlignmentSourceNotWritableException,
			SequenceNotFoundException {
		
		String underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID != null) {
			return getUnderlyingModel().renameSequence(underlyingID, newSequenceName);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}
}
