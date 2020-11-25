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


import java.util.ArrayList;
import java.util.Collection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;



/**
 * Edit object that removes a sequence from an instance of {@link AlignmentModel}.
 * 
 * @author Ben St&oml;ver
 * @since 0.10.0
 * @see EditRecorder
 */
public class AlignmentModelRemoveSequenceEdit<M extends AlignmentModel<T>, T> extends AlignmentModelSequenceEdit<M, T> {
	private String name;
	private Collection<T> deletedContent;
	private int index;
	

	public AlignmentModelRemoveSequenceEdit(M alignmentModel, String sequenceID, Collection<T> deletedContent, int index) {
		super(alignmentModel, sequenceID);
		this.deletedContent = deletedContent;
		this.index = index;
		if (deletedContent == null) {
			name = getAlignmentModel().sequenceNameByID(sequenceID);
			int length = getAlignmentModel().getSequenceLength(sequenceID);
			deletedContent = new ArrayList<T>(length);
			for (int i = 0; i < length; i++) {
				deletedContent.add(getAlignmentModel().getTokenAt(sequenceID, i));
			}
		}
		else {
			this.deletedContent = deletedContent;
		}
	}


	@Override
	public void redo() throws CannotRedoException {
		getAlignmentModel().removeSequence(getSequenceID());
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getAlignmentModel().addSequence(index, getName(), getSequenceID());
		getAlignmentModel().insertTokensAt(getSequenceID(), 0, deletedContent, true);  // The left bound property is set arbitrarily here, since it is undefined. In redo operations, data models should be restored from their own edit objects instead of reacting to events triggered by this call.
		super.undo();
	}


	public String getName() {
		return name;
	}


	@Override
	public String getPresentationName() {
		return "Removed sequence \"" + name + "\"";
	}
}
