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
package info.bioinfweb.libralign.model.implementations.decorate;


import info.bioinfweb.commons.bio.CharacterStateType;
import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelWriteType;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



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
	protected abstract T convertUnderlyingToken(int underlyingSequenceID, int underlyingIndex, U underlyingToken);

	
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
	protected abstract U convertDecoratedToken(int viewedSequenceID, int viewedIndex, T decoratedToken);
	
	
	protected Iterable<TokenChangeEvent<T>> convertTokenChangeEvent(TokenChangeEvent<U> event) {
		List<TokenChangeEvent<T>> result = new ArrayList<TokenChangeEvent<T>>(1);
		int decoratedID = convertUnderlyingSequenceID(event.getSequenceID());
		if (decoratedID > -1) {
			TokenChangeEvent<T> newEvent;
			switch (event.getType()) {
				case INSERTION:
					newEvent = TokenChangeEvent.newInsertInstance(this, convertUnderlyingSequenceID(event.getSequenceID()), 
							convertUnderlyingTokenIndex(event.getSequenceID(), event.getStartIndex()), 
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
							convertUnderlyingTokens(event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens())); 
					break;
				default:  // Just in case more types are added in the future.
					throw new IllegalArgumentException("The change type \"" + event.getType() + " is not supported.");
			}
			result.add(newEvent);
		}
		return result;
	}


	protected Collection<? extends T> convertUnderlyingTokens(int underlyingSequenceID, int underlyingIndex, 
			Collection<? extends U> underlyingTokens) {
		
		Collection<T> result;
		if (getTokenSet().getType().equals(CharacterStateType.CONTINUOUS) || 
				getTokenSet().getType().equals(CharacterStateType.UNKNOWN)) {
			
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
	
	
	protected Collection<? extends U> convertDecoratedTokens(int viewedSequenceID, int viewedIndex, 
			Collection<? extends T> viewedTokens) {
		
		Collection<U> result;
		if (getUnderlyingModel().getTokenSet().getType().equals(CharacterStateType.CONTINUOUS) || 
				getUnderlyingModel().getTokenSet().getType().equals(CharacterStateType.UNKNOWN)) {
			
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
	public T getTokenAt(int sequenceID, int index) {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
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
	public void setTokenAt(int sequenceID, int index, T token) throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
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
	public void setTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
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
	public void appendToken(int sequenceID, T token) throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			getUnderlyingModel().appendToken(underlyingID, convertDecoratedToken(sequenceID, 
					getUnderlyingModel().getSequenceLength(underlyingID), token));
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void appendTokens(int sequenceID, Collection<? extends T> tokens) throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			getUnderlyingModel().appendTokens(underlyingID, convertDecoratedTokens(sequenceID, 
					getUnderlyingModel().getSequenceLength(underlyingID), tokens));
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public void insertTokenAt(int sequenceID, int index, T token)	throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, index);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().insertTokenAt(underlyingID, underlyingIndex, 
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
	public void insertTokensAt(int sequenceID, int beginIndex, Collection<? extends T> tokens)
			throws AlignmentSourceNotWritableException {
		
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			int underlyingIndex = convertUnderlyingTokenIndex(sequenceID, beginIndex);  // Should in this case always be equal to the decorated index.
			if (underlyingIndex > -1) {
				getUnderlyingModel().insertTokensAt(underlyingID, underlyingIndex, 
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
	public void removeTokenAt(int sequenceID, int index) throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
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
	public void removeTokensAt(int sequenceID, int beginIndex, int endIndex) throws AlignmentSourceNotWritableException {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			getUnderlyingModel().removeTokensAt(underlyingID, beginIndex, endIndex);  // Indices are not converted here, because this model does not perform index conversions.
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public int getSequenceLength(int sequenceID) {
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
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
	public String renameSequence(int sequenceID, String newSequenceName) throws AlignmentSourceNotWritableException,
			DuplicateSequenceNameException, SequenceNotFoundException {
		
		int underlyingID = convertDecoratedSequenceID(sequenceID);
		if (underlyingID > -1) {
			return getUnderlyingModel().renameSequence(underlyingID, newSequenceName);
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}


	@Override
	public Set<AlignmentModelChangeListener> getChangeListeners() {
		return getUnderlyingModel().getChangeListeners();  //TODO Does in make sense to forward these directly? Could indices have changed? Should new listeners really be registered in the underlying model?
	}	
}
