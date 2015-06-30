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
package info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoSequenceDataProvider;



/**
 * Edit object that inserts a new empty sequence into an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoSequenceDataProvider
 */
public class SwingConcreteAddSequenceEdit<T> extends SwingSequenceEdit<T> implements SwingAddSequenceEdit {
	private String name;

	
	public SwingConcreteAddSequenceEdit(SwingUndoSequenceDataProvider<T> provider, String name) {
		super(provider, -1);
		this.name = name;
	}


	@Override
	public void redo() throws CannotRedoException {
		sequenceID = getProvider().getUnderlyingProvider().addSequence(name);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getProvider().getUnderlyingProvider().removeSequence(getProvider().sequenceIDByName(name));
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