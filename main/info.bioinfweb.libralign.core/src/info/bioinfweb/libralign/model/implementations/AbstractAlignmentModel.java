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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;

import java.util.HashSet;
import java.util.Set;



/**
 * This is the base class for all alignment models and alignment model decorators. It implements the common
 * change listener functionality.
 *
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractAlignmentModel<T> implements AlignmentModel<T> {
	private String id = null;
	private String label = null;
	private Set<AlignmentModelListener> modelListeners = new HashSet<AlignmentModelListener>();


	@Override
	public String getID() {
		return id;
	}


	@Override
	public void setID(String id) {
		this.id = id;
	}


	@Override
	public String getLabel() {
		return label;
	}


	/**
	 * Allows to specify a new label for this alignment. This implementation allows to edit the label and
	 * will never throw an {@link UnsupportedOperationException} in this method.
	 *
	 * @param label the new label
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
	}


	@Override
	public boolean addModelListener(AlignmentModelListener listener) {
		return modelListeners.add(listener);
	}


	@Override
	public boolean removeModelListener(AlignmentModelListener listener) {
		return modelListeners.remove(listener);
	}


	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterSequenceChange(SequenceChangeEvent<T> e) {
		for (AlignmentModelListener listener : modelListeners.toArray(new AlignmentModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterSequenceChange(e);
		}
	}


	/**
	 * Informs all listeners that a sequence has been renamed.
	 */
	protected void fireAfterSequenceRenamed(SequenceRenamedEvent<T> e) {
		for (AlignmentModelListener listener : modelListeners.toArray(new AlignmentModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterSequenceRenamed(e);
		}
	}


	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterTokenChange(TokenChangeEvent<T> e) {
		for (AlignmentModelListener listener : modelListeners.toArray(new AlignmentModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterTokenChange(e);
		}
	}
}
