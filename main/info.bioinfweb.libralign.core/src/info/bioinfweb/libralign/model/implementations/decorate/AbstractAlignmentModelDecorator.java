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


import java.util.Iterator;
import java.util.Set;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.commons.collections.observable.ListAddEvent;
import info.bioinfweb.commons.collections.observable.ListRemoveEvent;
import info.bioinfweb.commons.collections.observable.ListReplaceEvent;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.AlignmentModelView;
import info.bioinfweb.libralign.model.DataModelLists;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.implementations.AbstractAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
 * Basic implementation of {@link AlignmentModelView} which delegates sequence-specific methods 
 * (which are independent of the token type) to the underlying model. Inherited classes may 
 * provide modified views of the underlying sequences, possibly using a different token set.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the type of sequence elements (tokens) the implementing decorator works with
 * @param <U> the type of sequence elements (tokens) the underlying model works with
 */
public abstract class AbstractAlignmentModelDecorator<T, U> extends AbstractAlignmentModel<T> implements AlignmentModelView<T, U> {
	//TODO Since only the listeners from AbstractAlignmentModel are used and all other fields are hidden, inheriting from this class should be reconsidered.
	
  private AlignmentModel<U> underlyingModel;
  private TokenSet<T> tokenSet;

  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingModel the underlying model to be viewed
	 */
	public AbstractAlignmentModelDecorator(TokenSet<T> tokenSet, AlignmentModel<U> underlyingModel) {
		super();
		this.tokenSet = tokenSet;
		this.underlyingModel = underlyingModel;
		underlyingModel.addModelListener(new AlignmentModelListener<U>() {
			@Override
			public void afterTokenChange(TokenChangeEvent<U> e) {
				for (TokenChangeEvent<T> event: convertTokenChangeEvent((TokenChangeEvent<U>)e)) {
					fireAfterTokenChange(event);
				}
			}
			
			@Override
			public void afterSequenceRenamed(SequenceRenamedEvent<U> e) {
				SequenceRenamedEvent<T> event = convertSequenceRenamedEvent((SequenceRenamedEvent<U>)e); 
				if (event != null) {
					fireAfterSequenceRenamed(event);
				}
			}
			
			@Override
			public void afterSequenceChange(SequenceChangeEvent<U> e) {
				SequenceChangeEvent<T> event = convertSequenceChangeEvent((SequenceChangeEvent<U>)e); 
				if (event != null) {
					fireAfterSequenceChange(event);
				}
			}

			@Override
			public void beforeElementsAdded(ListAddEvent<DataModel<?>> event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterElementsAdded(ListAddEvent<DataModel<?>> event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeElementReplaced(ListReplaceEvent<DataModel<?>> event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterElementReplaced(ListReplaceEvent<DataModel<?>> event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeElementsRemoved(ListRemoveEvent<DataModel<?>, Object> event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterElementsRemoved(ListRemoveEvent<DataModel<?>, DataModel<?>> event) {
				// TODO Auto-generated method stub
			}
		});
	}

	
	/**
	 * Method used to convert a sequence ID from the underlying model to the according sequence ID used by the decorator.
	 * This default implementation just returns the specified ID unchanged.
	 * <p>
	 * Inherited classes that use different IDs than their underlying model must overwrite this method. Otherwise
	 * change events from the underlying model will not be converted correctly. The same applies if inherited classes
	 * hide any of the underlying sequences. In this case, they must return {@code null} for calls with the ID of a hidden 
	 * sequence. 
	 * 
	 * @param underlyingSequenceID the sequence ID used in the underlying model
	 * @return the according sequence ID used by this view or {@code null} if the specified underlying sequence is hidden by
	 *         this decorator 
	 */
	protected String convertUnderlyingSequenceID(String underlyingSequenceID) {
		return underlyingSequenceID;
	}
	
	
	/**
	 * Method used to convert a sequence ID used by the decorator to the according sequence ID used by the underlying model.
	 * This default implementation just returns the specified ID unchanged.
	 * <p>
	 * Inherited classes that use different IDs than their underlying model must overwrite this method because numerous 
	 * decorator methods rely on this conversion. The same applies if inherited classes add sequences to those contained 
	 * in the underlying model. In this case, they must return -1 for calls with the ID of an additional sequence. 
	 * 
	 * @param decoratedSequenceID the sequence ID used in the underlying model
	 * @return the according sequence ID used by this view or {@code null} if the specified decorated sequence is not 
	 *         present in the underlying model
	 */
	protected String convertDecoratedSequenceID(String decoratedSequenceID) {
		return decoratedSequenceID;
	}
	
	
	protected int convertUnderlyingTokenIndex(String sequenceID, int underlyingIndex) {
		return underlyingIndex;
	}
	
	
	protected int convertDecoratedTokenIndex(String sequenceID, int decoratedIndex) {
		return decoratedIndex;
	}
	
	
	/**
	 * Converts a token change event from the underlying (decorated) model to a collection of according change 
	 * events to be used with this decorator.
	 * <p>
	 * Depending on which columns are added or hidden by the decorator implementation, an empty collection or
	 * a collection containing more than one event may be returned. ({@code null} is not a valid return value.)  
	 * 
	 * @param event the event fired by the underlying model
	 * @return a list (with zero or more elements) that describe the according changes in this decorator
	 */
	protected abstract Iterable<TokenChangeEvent<T>> convertTokenChangeEvent(TokenChangeEvent<U> event);
	
	
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
					return SequenceChangeEvent.newInsertInstance(this, decoratedID);
				case DELETION:
					return SequenceChangeEvent.newRemoveInstance(this, decoratedID);
				default:  // Just in case more valid types are added in the future.
					throw new IllegalArgumentException("The change type \"" + event.getType() + " is not supported.");
			}
		}
		else {
			return null;
		}
	}
	

	/**
	 * Converts a {@link SequenceRenamedEvent} event from the decorated instance to an event object compatible
	 * with this instance.
	 * <p>
	 * This default implementation returns a new event object with this instance as its owner, the same previous and
	 * current name and a sequence ID converted using {@link #convertUnderlyingSequenceID(int)}. If the ID conversion returns -1, {@code null} is returned
	 * by this method.
	 * <p>
	 * Inherited classes may overwrite this method to implement a different behavior, but in most cases it should
	 * be sufficient to overwrite {@link #convertUnderlyingSequenceID(int)} to change the behavior of this method.
	 * 
	 * @param event the event from the underlying (decorated) model
	 * @return a new converted event object or {@code null} if the specified underlying sequence is hidden by this decorator
	 */
	protected SequenceRenamedEvent<T> convertSequenceRenamedEvent(SequenceRenamedEvent<U> event) {
		String decoratedID = convertUnderlyingSequenceID(event.getSequenceID());
		if (decoratedID != null) {
			return new SequenceRenamedEvent<T>(this, decoratedID, event.getPreviousName(), event.getNewName());
		}
		else {
			return null;
		}
	}
	

	@Override
	public AlignmentModel<U> getUnderlyingModel() {
		return underlyingModel;
	}


	@Override
	public TokenSet<T> getTokenSet() {
		return tokenSet;
	}


	@Override
	public void setTokenSet(TokenSet<T> tokenSet) {
		this.tokenSet = tokenSet;
	}


	/**
	 * Replaces the decorated (underlying) model.
	 * 
	 * @param underlyingModel - the new underlying model
	 */
	protected void setUnderlyingModel(AlignmentModel<U> underlyingModel) {
		this.underlyingModel = underlyingModel;
	}


	@Override
	public String getID() {
		return underlyingModel.getID();
	}


	@Override
	public void setID(String id) {
		underlyingModel.setID(id);
	}


	@Override
	public String getLabel() {
		return underlyingModel.getLabel();
	}


	@Override
	public void setLabel(String label) throws UnsupportedOperationException {
		underlyingModel.setLabel(label);
	}


	/**
	 * Returns the {@link DataModelLists} instance of the decorated alignment model.
	 * <p>
	 * Note that the returned instance will reference the decorated alignment model as its owner and not this instance. 
	 * 
	 * @return the {@link DataModelLists} object
	 */
	@Override
	public DataModelLists getDataModels() {
		return underlyingModel.getDataModels();
	}


	@Override
	public boolean isSequencesReadOnly() {
		return underlyingModel.isSequencesReadOnly();
	}


	@Override
	public boolean containsSequence(String sequenceID) {
		return underlyingModel.containsSequence(sequenceID);
	}


	@Override
	public Set<String> sequenceIDsByName(String sequenceName) {
		return underlyingModel.sequenceIDsByName(sequenceName);
	}


	@Override
	public String sequenceNameByID(String sequenceID) {
		return underlyingModel.sequenceNameByID(sequenceID);
	}


	@Override
	public String addSequence(String sequenceName) throws AlignmentSourceNotWritableException {
		return underlyingModel.addSequence(sequenceName);
	}


	@Override
	public String addSequence(String sequenceName, String sequenceID) throws AlignmentSourceNotWritableException {
		return underlyingModel.addSequence(sequenceName, sequenceID);
	}


	@Override
	public boolean removeSequence(String sequenceID)	throws AlignmentSourceNotWritableException {
		return underlyingModel.removeSequence(sequenceID);
	}


	@Override
	public Iterator<String> sequenceIDIterator() {
		return underlyingModel.sequenceIDIterator();
	}


	@Override
	public int getSequenceCount() {
		return underlyingModel.getSequenceCount();
	}
}
