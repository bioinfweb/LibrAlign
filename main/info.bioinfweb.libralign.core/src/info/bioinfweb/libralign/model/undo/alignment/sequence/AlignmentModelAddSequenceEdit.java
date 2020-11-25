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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Edit object that inserts a new empty sequence into an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public class AlignmentModelAddSequenceEdit<M extends AlignmentModel<T>, T> extends AlignmentModelSequenceEdit<M, T> {
	private String sequenceName;
	private int index;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param model the alignment model to be edited
	 * @param sequenceID the ID of the new sequence
	 * @param sequenceName the name of the new sequence
	 */
	public AlignmentModelAddSequenceEdit(M alignmentModel, String sequenceID, String sequenceName, int index) {
		super(alignmentModel, sequenceID);
		if (alignmentModel == null) {
			throw new IllegalArgumentException("sequenceName must not be null.");
		}
		else {
			this.sequenceName = sequenceName;
			this.index = index;
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		getAlignmentModel().addSequence(index, sequenceName, getSequenceID());
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getAlignmentModel().removeSequence(getSequenceID());
		super.undo();
	} 

	
	@Override
	public String getPresentationName() {
		return "Add sequence \"" + sequenceName + "\".";
	}
}
