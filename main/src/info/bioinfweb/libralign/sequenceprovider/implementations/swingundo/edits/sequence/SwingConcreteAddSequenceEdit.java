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
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.sequence;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.SwingUndoSequenceDataProvider;



/**
 * Edit object that inserts a new empty sequence into an instance of {@link SequenceDataProvider}.
 * 
 * @author Ben St&oml;ver
 * @since 0.0.1
 * @see SwingUndoSequenceDataProvider
 */
public class SwingConcreteAddSequenceEdit extends SwingSequenceEdit implements SwingAddSequenceEdit {
	private String name;

	
	public SwingConcreteAddSequenceEdit(SwingUndoSequenceDataProvider provider, String name) {
		super(provider, -1);
		this.name = name;
	}


	@Override
	public void redo() throws CannotRedoException {
		sequenceID = getProvider().addSequence(name);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getProvider().removeSequence(getProvider().sequenceIDByName(name));
		super.undo();
	} 

	
	@Override
	public String getPresentationName() {
		return "Add sequence \"" + name + "\"";
	}


	@Override
	public int getNewSequenceID() {
		return sequenceID;
	}
}
