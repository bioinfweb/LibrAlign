/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.order.SequenceOrder;
import info.bioinfweb.libralign.model.SequenceAccessAlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.tokenset.TokenSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * Implements all methods dealing with the organization of the sequence set using some implementation
 * of {@link Map} to identify the sequence object belonging to a sequence ID. The ordering of the
 * sequences is implemented using an additional list storing the IDs. Inherited classes have the option 
 * to provide a custom map implementation instead of {@link HashMap} which is the default.
 * <p>
 * Sequences are sorted by the order they are added to the model. This ordering in independent to the
 * {@link SequenceOrder} object used in an associated {@link AlignmentArea}.  
 * 
 * @author Ben St&ouml;ver
 * 
 * @see SequenceAccessAlignmentModel
 * @see SequenceOrder
 * @see AlignmentArea
 *
 * @param <S> - the type of the sequence objects (e.g. a {@link List} implementation or a BioJava sequence type)
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractMapBasedAlignmentModel<S, T> extends AbstractUndecoratedAlignmentModel<T> {
  private Map<Integer, S> sequenceMap;
  private List<Integer> sequenceOrder;  // Only necessary to save the order the sequences were added, because they might be sorted like this later on. The actual ordering will be done by a SequenceOrder object of the GUI.

  
	/**
	 * Creates a new instance of this class with a custom map and list implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param sequenceMap - the map instance used to assign sequences to their IDs (must be empty)
	 * @param sequenceOrder - the list object defining the order of the sequences
	 */
	public AbstractMapBasedAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager, 
			Map<Integer, S> sequenceMap, List<Integer> sequenceOrder) {
		
		super(tokenSet, idManager);
		if (sequenceMap.isEmpty()) {
			this.sequenceMap = sequenceMap;
			this.sequenceOrder = sequenceOrder;
		}
		else {
			throw new IllegalArgumentException("The passed sequence map is not empty. "
					+ "(Elements in the map are not allowed because no IDs have been assigned yet.)");
		}
	}


	/**
	 * Creates a new instance of this class with a custom map implementation.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @param sequenceMap - the map instance used to assign sequences to their IDs
	 */
	public AbstractMapBasedAlignmentModel(TokenSet<T> tokenSet,	SequenceIDManager idManager, Map<Integer, S> sequenceMap) {
		this(tokenSet, idManager, sequenceMap, new ArrayList<Integer>());
	}


	/**
	 * Creates a new instance of this class relying on a {@link HashMap}.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 */
	public AbstractMapBasedAlignmentModel(TokenSet<T> tokenSet, SequenceIDManager idManager) {
		this(tokenSet, idManager, new HashMap<Integer, S>());
	}


	/**
	 * Returns the underlying map object used to assign sequences to their IDs. Note that you must fire the 
	 * according events if your implementation modifies this map.
	 * 
	 * @return the instance of a map implementation provided in the constructor
	 */
	protected Map<Integer, S> getSequenceMap() {
		return sequenceMap;
	}


	/**
	 * Returns the list object used to determine the order of the sequences. Note that you must fire the 
	 * according events if your implementation modifies this list.
	 * 
	 * @return an instance of {@link ArrayList} in the current implementation (Note that the implementing class
	 *         might change in future releases of LibrAlign.)
	 */
	protected List<Integer> getSequenceOrder() {
		return sequenceOrder;
	}


	@Override
	public boolean containsSequence(int sequenceID) {
		return getSequenceMap().containsKey(sequenceID);
	}


	/**
	 * Returns an iterator returned the IDs of the stored sequences in the order they were added 
	 * to this model. 
	 *  
	 * @see info.bioinfweb.libralign.model.AlignmentModel#sequenceIDIterator()
	 */
	@Override
	public Iterator<Integer> sequenceIDIterator() {
		return new AbstractSequenceIDIterator<AbstractMapBasedAlignmentModel<S, T>>(
				this,	getSequenceOrder().iterator()) {
			
					@Override
					protected void doRemove() {
						getSequenceMap().remove(getCurrentID());
						fireAfterSequenceChange(SequenceChangeEvent.newRemoveInstance(getProvider(), getCurrentID()));
					}
				};
	}


	@Override
	public int getSequenceCount() {
		return getSequenceMap().size();
	}

	
	/**
	 * Implementing classes must offer a way to create their custom instances of sequence objects 
	 * overwriting this method. This method is called in {@link #doAddSequence(int, String)} before
	 * adding the new sequences to map and the oder list.
	 * 
	 * @param sequenceID - the ID the new sequence must have
	 * @param sequenceName - the name the new sequence will have
	 * @return the new sequence object
	 */
	protected abstract S createNewSequence(int sequenceID, String sequenceName);
	

	@Override
	protected void doAddSequence(int sequenceID, String sequenceName) {
		S sequence = createNewSequence(sequenceID, sequenceName);
		getSequenceMap().put(sequenceID, sequence);
		getSequenceOrder().add(sequenceID);
	}


	@Override
	protected void doRemoveSequence(int sequenceID) {
		getSequenceMap().remove(sequenceID);
		getSequenceOrder().remove(getSequenceOrder().indexOf(sequenceID));
	}
}
