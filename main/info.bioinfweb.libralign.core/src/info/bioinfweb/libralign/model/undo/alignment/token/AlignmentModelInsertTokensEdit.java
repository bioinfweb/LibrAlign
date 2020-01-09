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
package info.bioinfweb.libralign.model.undo.alignment.token;


import java.util.Collection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Edit object that inserts a set of tokens into a sequence managed by an instance of 
 * {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public class AlignmentModelInsertTokensEdit<M extends AlignmentModel<T>, T> extends AlignmentModelInsertRemoveTokensEdit<M, T> {
	public AlignmentModelInsertTokensEdit(M alignmentModel, String sequenceID, int beginIndex, Collection<? extends T> tokens, boolean leftBound) {
		super(alignmentModel, sequenceID, beginIndex, tokens, leftBound);
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
