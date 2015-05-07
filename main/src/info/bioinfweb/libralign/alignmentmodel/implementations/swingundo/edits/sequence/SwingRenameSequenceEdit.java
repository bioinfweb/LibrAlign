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
package info.bioinfweb.libralign.alignmentmodel.implementations.swingundo.edits.sequence;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.alignmentmodel.AlignmentModel;
import info.bioinfweb.libralign.alignmentmodel.implementations.swingundo.SwingUndoSequenceDataProvider;



/**
 * Edit object that renames a sequence managed by an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.1.0
 * @see SwingUndoSequenceDataProvider
 */
public class SwingRenameSequenceEdit<T> extends SwingSequenceEdit<T> {
  private String newName;
	private String oldName;
  
  
  public SwingRenameSequenceEdit(SwingUndoSequenceDataProvider<T> provider, int sequenceID, String newName) {
		super(provider, sequenceID);
		this.newName = newName;
	}


	@Override
	public void redo() throws CannotRedoException {
		getProvider().getUnderlyingProvider().renameSequence(sequenceID, newName);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getProvider().getUnderlyingProvider().renameSequence(sequenceID, oldName);
		super.undo();
	}


	@Override
	public String getPresentationName() {
		return "Rename sequence \"" + oldName + "\" to \"" + newName + "\"";
	}
}
