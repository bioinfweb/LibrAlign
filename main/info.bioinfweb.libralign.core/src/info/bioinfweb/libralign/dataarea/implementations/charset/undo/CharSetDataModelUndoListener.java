/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben St�ver
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
import info.bioinfweb.libralign.model.undo.AbstractAlignmentModelEdit;
import info.bioinfweb.libralign.model.undo.EditRecorder;



public class CharSetDataModelUndoListener implements CharSetDataModelListener{
	EditRecorder<?,?> recorder;
	
	
	public CharSetDataModelUndoListener(EditRecorder<?,?> recorder) {
		super();
		this.recorder = recorder;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterCharSetChange(CharSetChangeEvent e) {
		if (e.getReplacedCharSet() != null) {
			recorder.addSubedit(new CharSetChangeReplaceEdit(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getReplacedCharSet(), e.getCharSetID(), e.getSource()));
		}
		else if (e.getType().equals(ListChangeType.INSERTION)) {
			recorder.addSubedit(new CharSetChangeAddEdit(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getCharSetID(), e.getSource()));
		}
		else if (e.getType().equals(ListChangeType.DELETION)) {
			recorder.addSubedit(new CharSetChangeRemoveEdit(e.getSource(), e.getSource().getAlignmentModel(), e.getNewCharSet(), e.getCharSetID(), e.getSource()));
		}
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void afterCharSetRenamed(CharSetRenamedEvent e) {
		recorder.addSubedit(new CharSetRenamedEdit(e.getSource().getAlignmentModel(), e.getCharSet(), e.getPreviousName(), e.getNewName(), e.getSource()));
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterCharSetColumnChange(CharSetColumnChangeEvent e) {
		if (e.getType().equals(ListChangeType.INSERTION)) {
			recorder.addSubedit(new CharSetColumnAddEdit(e.getSource().getAlignmentModel(), e.getCharSet(), e.getFirstPos(), e.getLastPos(), e.getSource()));
		}
		else if (e.getType().equals(ListChangeType.DELETION)) {
			recorder.addSubedit(new CharSetColumnRemoveEdit(e.getSource().getAlignmentModel(), e.getCharSet(), e.getFirstPos(), e.getLastPos(), e.getSource()));
		}
		
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterCharSetColorChange(CharSetColorChangeEvent e) {
		recorder.addSubedit(new CharSetColorChangeEdit(e.getSource().getAlignmentModel(), e.getCharSet(), e.getPreviousColor(), e.getNewColor(), e.getSource()));
	}

}
