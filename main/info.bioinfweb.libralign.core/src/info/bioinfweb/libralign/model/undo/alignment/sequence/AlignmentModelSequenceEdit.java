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
package info.bioinfweb.libralign.model.undo.alignment.sequence;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.AbstractAlignmentModelEdit;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Offers basic functionality used by edits that manipulate sequences. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public abstract class AlignmentModelSequenceEdit<M extends AlignmentModel<T>, T> extends AbstractAlignmentModelEdit<M, T> {
	private String sequenceID;

	
	public AlignmentModelSequenceEdit(M alignmentModel, String sequenceID) {
		super(alignmentModel);
		if (sequenceID == null) {
			throw new IllegalArgumentException("sequenceID must not be null.");
		}
		else {
			this.sequenceID = sequenceID;
		}
	}


	/**
	 * Returns the sequence ID this edit deals with.
	 * 
	 * @return the sequence ID or {@code null} if the ID is not yet known.
	 */
	public String getSequenceID() {
		return sequenceID;
	}
}
