/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * This interface should be implemented by classes that want to track changes in the stored data 
 * of an implementation of {@link AlignmentModel}.
 * <p>
 * This class does not specify a global generic token type parameter since the {@link AlignmentModel}
 * is listens to may change during runtime if the provider that is attached to the {@link AlignmentArea}
 * is substituted.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface AlignmentModelChangeListener {
	/**
	 * Called after a sequence has been inserted, removed or replaced.
	 * 
	 * @param e the event object containing information on the change
	 */
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e);

	/**
	 * Called after a sequence was renamed.
	 * 
	 * @param e the event object containing information on the change
	 */
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e);

	/**
	 * Called after a single token or a set of tokens has been inserted, removed or replaced.
	 * 
	 * @param e the event object containing information on the change
	 */
	public <T> void afterTokenChange(TokenChangeEvent<T> e);

	/**
	 * Called if this listener was moved to another instance of {@link AlignmentModel}.
	 * <p>
	 * This happens if the alignment model of an {@link AlignmentArea} was changed.
	 * 
	 * @param previous the alignment model this listener was attached to before the event happened
	 * @param current the new alignment model this listener is attached to now
	 */
	public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current);
}
