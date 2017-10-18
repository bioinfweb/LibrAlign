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
package info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;



/**
 * Edit object that inserts a new empty sequence into an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public class SwingConcreteAddSequenceEdit<T> extends SwingSequenceEdit<T> implements SwingAddSequenceEdit {
	private String name;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param model the alignment model creating this edit
	 * @param name the name of the new sequence
	 * @param sequenceID the ID of the new sequence (Maybe {@code null}, of the underlying model shall create an ID for it.)
	 */
	public SwingConcreteAddSequenceEdit(SwingUndoAlignmentModel<T> model, String name, String sequenceID) {
		super(model, null);
		this.name = name;
		this.sequenceID = sequenceID;
	}


	@Override
	public void redo() throws CannotRedoException {
		if (sequenceID == null) {  // Possibly create an ID on the first call of redo(), if none was specified.
			sequenceID = getModel().getUnderlyingModel().addSequence(name);
		}
		else {
			getModel().getUnderlyingModel().addSequence(name, sequenceID);
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getModel().getUnderlyingModel().removeSequence(sequenceID);
		super.undo();
	} 

	
	@Override
	public String getPresentationName() {
		return "Add sequence \"" + name + "\".";
	}


	@Override
	public String getNewSequenceID() {
		return sequenceID;
	}
}
