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
package info.bioinfweb.libralign.model.implementations;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * This is the base class for all alignment models and alignment model decorators. It implements the common
 * change listener functionality.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing provider object works with
 */
public abstract class AbstractAlignmentModel<T> implements AlignmentModel<T> {
	private Set<AlignmentModelChangeListener> changeListeners = new HashSet<AlignmentModelChangeListener>();

	
	@Override
	public Set<AlignmentModelChangeListener> getChangeListeners() {
		return changeListeners;
	}


	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterSequenceChange(SequenceChangeEvent<T> e) {
		Iterator<AlignmentModelChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceChange(e);
		}
	}


	/**
	 * Informs all listeners that a sequence has been renamed.
	 */
	protected void fireAfterSequenceRenamed(SequenceRenamedEvent<T> e) {
		Iterator<AlignmentModelChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterSequenceRenamed(e);
		}
	}
	

	/**
	 * Informs all listeners that a sequence has been inserted, removed or replaced.
	 */
	protected void fireAfterTokenChange(TokenChangeEvent<T> e) {
		Iterator<AlignmentModelChangeListener> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().afterTokenChange(e);
		}
	}
}
