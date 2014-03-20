/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.alignmentprovider;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.alignmentprovider.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.alignmentprovider.events.SequenceChangeEvent;
import info.bioinfweb.libralign.alignmentprovider.events.TokenChangeEvent;



/**
 * This interface should be implemented by classes that want to track changes in the stored data 
 * of an implementation of {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public interface SequenceDataChangeListener {
	/**
	 * Called after a sequence has been inserted, removed or replaced.
	 * 
	 * @param e - the event object containing information on the change
	 */
	public void afterSequenceChange(SequenceChangeEvent e);

	/**
	 * Called after a sequence was renamed.
	 * 
	 * @param e - the event object containing information on the change
	 */
	public void afterSequenceRenamed(SequenceRenamedEvent e);

	/**
	 * Called after a single token or a set of tokens has been inserted, removed or replaced.
	 * 
	 * @param e - the event object containing information on the change
	 */
	public void afterTokenChange(TokenChangeEvent e);

	/**
	 * Called if this listener was moved to another instance of {@link SequenceDataProvider}.
	 * <p>
	 * This might e.g. happen, if the data provider of an {@link AlignmentArea} was changed.
	 * 
	 * @param previous - the data provider this listener was attached to before the event happened
	 * @param current - the new data provider this listener is attached to now
	 */
	public void afterProviderChanged(SequenceDataProvider previous, SequenceDataProvider current);
}
