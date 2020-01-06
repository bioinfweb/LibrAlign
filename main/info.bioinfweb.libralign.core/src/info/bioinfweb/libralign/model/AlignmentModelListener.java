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
package info.bioinfweb.libralign.model;


import info.bioinfweb.libralign.model.events.DataModelChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * This interface should be implemented by classes that want to track changes in the stored data 
 * of an implementation of {@link AlignmentModel}.
 * <p>
 * Calls of the inherited list change event methods indicate that data models nested within the alignment model were added,
 * removed or replaced. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface AlignmentModelListener<T> {
	/**
	 * Called after a sequence has been inserted, removed or replaced.
	 * 
	 * @param event the event object containing information on the change
	 */
	public void afterSequenceChange(SequenceChangeEvent<T> event);

	/**
	 * Called after a sequence was renamed.
	 * 
	 * @param event the event object containing information on the change
	 */
	public void afterSequenceRenamed(SequenceRenamedEvent<T> event);

	/**
	 * Called after a single token or a set of tokens has been inserted, removed or replaced.
	 * 
	 * @param event the event object containing information on the change
	 */
	public void afterTokenChange(TokenChangeEvent<T> event);
	
	/**
	 * Called after a data model has been added or removed from the alignment model.
	 * 
	 * @param event the event object containing information on the change
	 * @since 0.10.0
	 */
	public void afterDataModelChange(DataModelChangeEvent<T> event);
}
