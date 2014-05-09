/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits.token;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.SwingUndoSequenceDataProvider;

import java.util.Collection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;



/**
 * Edit object that inserts a set of tokens into a sequence managed by an instance of 
 * {@link SequenceDataProvider}.
 * 
 * @author Ben St&oml;ver
 * @since 0.0.1
 * @see SwingUndoSequenceDataProvider
 */
public class SwingInsertTokensEdit extends SwingInsertRemoveTokensEdit {
	public SwingInsertTokensEdit(SwingUndoSequenceDataProvider provider, int sequenceID, int beginIndex, 
			Collection<? extends Object> tokens) {
	
		super(provider, sequenceID, beginIndex, tokens);
	}

	
	@Override
	public void redo() throws CannotRedoException {
		insert();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		remove();
		super.undo();
	}


	@Override
	protected String getOperationName() {
		return "Insert";
	}
}
