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
package info.bioinfweb.libralign.alignmentmodel;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentmodel.events.PartModelChangeEvent;
import info.bioinfweb.libralign.alignmentmodel.events.SequenceChangeEvent;
import info.bioinfweb.libralign.alignmentmodel.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.alignmentmodel.events.TokenChangeEvent;



/**
 * This interface should be implemented by classes that want to track changes of an instance of 
 * {@link ConcatenatedAlignmentModel} and the {@link AlignmentPartModel} implementations it contains.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface AlignmentModelChangeListener {
	/**
	 * Called after a part model has been inserted or removed from an instance of {@link ConcatenatedAlignmentModel}.
	 * <p>
	 * A part model is an instance of {@link AlignmentPartModel} which provides a subsequent set of columns of the
	 * whole alignment (described by {@link ConcatenatedAlignmentModel}) which share a common token set.
	 * 
	 * @param e - the event object containing information on the change
	 * @since 0.4.0
	 */
	public <T> void afterPartModelChange(PartModelChangeEvent<T> e);
	
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
	public <T> void afterTokenChange(TokenChangeEvent<T> e);

	/**
	 * Called if this listener was moved to another instance of {@link ConcatenatedAlignmentModel}.
	 * <p>
	 * This might e.g. happen, if the model of an {@link AlignmentArea} was changed.
	 * 
	 * @param previous - the model this listener was attached to before the event happened
	 * @param current - the new model this listener is attached to now
	 */
	public void afterModelChanged(ConcatenatedAlignmentModel previous, 
			ConcatenatedAlignmentModel current);
}
