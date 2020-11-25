/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.undo.alignment;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.model.AlignmentModelListener;
import info.bioinfweb.libralign.model.events.DataModelChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.undo.EditRecorder;
import info.bioinfweb.libralign.model.undo.alignment.data.DataModelAddEdit;
import info.bioinfweb.libralign.model.undo.alignment.data.DataModelRemoveEdit;
import info.bioinfweb.libralign.model.undo.alignment.sequence.AlignmentModelAddSequenceEdit;
import info.bioinfweb.libralign.model.undo.alignment.sequence.AlignmentModelRemoveSequenceEdit;
import info.bioinfweb.libralign.model.undo.alignment.sequence.AlignmentModelRenameSequenceEdit;
import info.bioinfweb.libralign.model.undo.alignment.token.AlignmentModelInsertTokensEdit;
import info.bioinfweb.libralign.model.undo.alignment.token.AlignmentModelRemoveTokensEdit;
import info.bioinfweb.libralign.model.undo.alignment.token.AlignmentModelSetTokensEdit;



public class AlignmentModelUndoListener<T> implements AlignmentModelListener<T> {
	private EditRecorder<?,?> recorder;
	
	
	public AlignmentModelUndoListener(EditRecorder<?, ?> recorder) {
		super();
		this.recorder = recorder;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterSequenceChange(SequenceChangeEvent<T> event) {
		if (event.getType().equals(ListChangeType.INSERTION)) {
			recorder.addSubedit(new AlignmentModelAddSequenceEdit(event.getSource(), event.getSequenceID(), event.getSequenceName(), event.getIndex()));
		}
		else if (event.getType().equals(ListChangeType.DELETION)) {
			recorder.addSubedit(new AlignmentModelRemoveSequenceEdit(event.getSource(), event.getSequenceID(), event.getDeletedContent(), event.getIndex()));
		}
		
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterSequenceRenamed(SequenceRenamedEvent<T> event) {
		recorder.addSubedit(new AlignmentModelRenameSequenceEdit(event.getSource(), event.getSequenceID(), event.getNewName(), event.getPreviousName()));
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterTokenChange(TokenChangeEvent<T> event) {
		if (event.getType().equals(ListChangeType.INSERTION)) {
			recorder.addSubedit(new AlignmentModelInsertTokensEdit(event.getSource(), event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens(), event.isLeftBound()));
		}
		else if (event.getType().equals(ListChangeType.DELETION)) {
			recorder.addSubedit(new AlignmentModelRemoveTokensEdit(event.getSource(), event.getSequenceID(), event.getStartIndex(), event.getAffectedTokens()));
		}
		else if (event.getType().equals(ListChangeType.REPLACEMENT)) {
			recorder.addSubedit(new AlignmentModelSetTokensEdit(event.getSource(), event.getSequenceID(), event.getStartIndex(), event.getNewTokens(), event.getAffectedTokens()));
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterDataModelChange(DataModelChangeEvent<T> event) {
		if (event.getType().equals(ListChangeType.INSERTION)) {
			recorder.addSubedit(new DataModelAddEdit(event.getSource(), event.getDataModel()));
			event.getDataModel().ensureUndoListener(recorder);
		}
		else if (event.getType().equals(ListChangeType.DELETION)) {
			recorder.addSubedit(new DataModelRemoveEdit(event.getSource(), event.getDataModel()));
		}
		
	}
}
