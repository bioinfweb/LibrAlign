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
package info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence;


import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.LibrAlignSwingAlignmentEdit;



/**
 * Offers basic functionality used by edits that manipulate sequences. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public abstract class SwingSequenceEdit<T> extends LibrAlignSwingAlignmentEdit<T> {
	protected int sequenceID;

	
	public SwingSequenceEdit(SwingUndoAlignmentModel<T> provider, int sequenceID) {
		super(provider);
		this.sequenceID = sequenceID;
	}

	
	/**
	 * Returns the sequence ID this edit deals with.
	 * 
	 * @return the sequence ID or {@code -1} if the ID is not yet known.
	 */
	public int getSequenceID() {
		return sequenceID;
	}
}
