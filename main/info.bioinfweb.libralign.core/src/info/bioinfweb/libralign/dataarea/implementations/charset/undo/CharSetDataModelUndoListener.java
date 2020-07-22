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
package info.bioinfweb.libralign.dataarea.implementations.charset.undo;


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModelListener;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColorChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColumnChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;
import info.bioinfweb.libralign.model.undo.AlignmentModelEdit;
import info.bioinfweb.libralign.model.undo.EditRecorder;



public class CharSetDataModelUndoListener implements CharSetDataModelListener{
	EditRecorder<?,?> recorder;
	
	
	public CharSetDataModelUndoListener(EditRecorder<?,?> recorder) {
		super();
		this.recorder = recorder;
	}
	
	
	@Override
	public void afterCharSetChange(CharSetChangeEvent e) {
		if (e.getReplacedCharSet() != null) {
			CharSetChangeReplaceEdit<?,?> edit = new CharSetChangeReplaceEdit<>(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getReplacedCharSet(), e.getCharSetID());
			recorder.addSubedit(edit);
		}
		else if (e.getType().equals(ListChangeType.INSERTION)) {
			CharSetChangeAddEdit<?,?> edit = new CharSetChangeAddEdit<>(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getCharSetID());
			recorder.addSubedit(edit);
		}
		else if (e.getType().equals(ListChangeType.DELETION)) {
			CharSetChangeRemoveEdit<?,?> edit = new CharSetChangeRemoveEdit<>(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getCharSetID());
			recorder.addSubedit(edit);
		}
	}

	
	@Override
	public void afterCharSetRenamed(CharSetRenamedEvent e) {
		CharSetRenamedEdit<?,?> edit = new CharSetRenamedEdit<>(e.getSource().getAlignmentModel(), e.getCharSet(), e.getPreviousName(), e.getNewName());
		recorder.addSubedit(edit);
	}

	
	@Override
	public void afterCharSetColumnChange(CharSetColumnChangeEvent e) {
		if (e.getType().equals(ListChangeType.INSERTION)) {
			CharSetColumnAddEdit<?,?> edit = new CharSetColumnAddEdit<>(e.getSource().getAlignmentModel(), e.getCharSet(), e.getFirstPos(), e.getLastPos());
			recorder.addSubedit(edit);
		}
		else if (e.getType().equals(ListChangeType.DELETION)) {
			CharSetColumnRemoveEdit<?,?> edit = new CharSetColumnRemoveEdit<>(e.getSource().getAlignmentModel(), e.getCharSet(), e.getFirstPos(), e.getLastPos());
			recorder.addSubedit(edit);
		}
		
	}

	
	@Override
	public void afterCharSetColorChange(CharSetColorChangeEvent e) {
		CharSetColorChangeEdit<?,?> edit = new CharSetColorChangeEdit<>(e.getSource().getAlignmentModel(), e.getCharSet(), e.getPreviousColor(), e.getNewColor());
		recorder.addSubedit(edit);
		
	}

}
