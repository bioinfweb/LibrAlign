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
package info.bioinfweb.libralign.model.undo;


import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.UndoManager;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



/**
 * Records all changes made to an {@link AlignmentModel} and its contained {@link DataModel}s and allows to undo and redo them.
 * 
 * @author Ben St&ouml;ver
 * @since 0.10.0
 *
 * @param <M> the alignment model this instance is used for
 */
public class EditRecorder<M extends AlignmentModel<T>, T> {
	private M alignmentModel;
	private UndoManager undoManager;
	private List<AlignmentModelEdit<M, T>> currentSubedits;
	private boolean definedEditRecording;
	
	
	public EditRecorder(M alignmentModel) {
		super();
		this.alignmentModel = alignmentModel;
		createNewSubeditList();
		definedEditRecording = false;
	}

	
	private void createNewSubeditList() {
		currentSubedits = new ArrayList<AlignmentModelEdit<M, T>>();
	}
	
	//TODO: figure out how to do it without the raw type
	public void addSubedit(AlignmentModelEdit<M, T> edit) {
		currentSubedits.add(edit);
	}
	

	public M getAlignmentModel() {
		return alignmentModel;
	}


	public boolean isDefinedEditRecording() {
		return definedEditRecording;
	}


	public void startEdit() {
		endEdit(null);
		definedEditRecording = true;
	}
	
	
	public void endEdit(String presentationName) {
		if (!currentSubedits.isEmpty()) {
			if (presentationName == null) {
				//TODO Determine presentation name by subedits.
			}
			undoManager.addEdit(new CombinedEdit<M, T>(alignmentModel, presentationName, currentSubedits));
			createNewSubeditList();
		}
		definedEditRecording = false;
	}
}
