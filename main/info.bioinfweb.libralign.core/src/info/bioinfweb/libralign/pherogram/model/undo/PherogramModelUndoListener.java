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
package info.bioinfweb.libralign.pherogram.model.undo;


import info.bioinfweb.libralign.model.undo.EditRecorder;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.PherogramCutPositionChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramFirstSeqPosChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;
import info.bioinfweb.libralign.pherogram.model.PherogramProviderChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramShiftChangeUpdateEvent;



public class PherogramModelUndoListener implements PherogramModelListener {
	private EditRecorder<?, ?> recorder; //TODO: maybe add generic parameters to PherogramModelUndoListener
	
	
	public PherogramModelUndoListener(EditRecorder<?, ?> recorder) {
		super();
		this.recorder = recorder;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void pherogramProviderChange(PherogramProviderChangeEvent event) {
		PherogramAreaModel model = (PherogramAreaModel) event.getSource();
		recorder.addSubedit(new PherogramModelProviderChangeEdit(model.getAlignmentModel(), event.getSource(), event.getOldProvider(), event.getNewProvider(), event.isReverseComplemented(), event.getSequenceIDs()));
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void leftCutPositionChange(PherogramCutPositionChangeEvent event) {
		if (event.getSource() instanceof PherogramAreaModel) {
			PherogramAreaModel model = (PherogramAreaModel) event.getSource();
			recorder.addSubedit(new LeftCutPositionEdit(model.getAlignmentModel(), model, event.getOldBaseCallIndex(), event.getNewBaseCallIndex()));
		}
		
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void rightCutPositionChange(PherogramCutPositionChangeEvent event) {
		if (event.getSource() instanceof PherogramAreaModel) {
			PherogramAreaModel model = (PherogramAreaModel) event.getSource();
			recorder.addSubedit(new RightCutPositionEdit(model.getAlignmentModel(), event.getSource(), event.getOldBaseCallIndex(), event.getNewBaseCallIndex()));
		}
		
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void firstSequencePositionChange(PherogramFirstSeqPosChangeEvent event) {
		if (event.getSource() instanceof PherogramAreaModel) {
			PherogramAreaModel model = (PherogramAreaModel) event.getSource();
			recorder.addSubedit(new FirstSequencePositionEdit(model.getAlignmentModel(), model, event.getOldPosition(), event.getNewPosition()));
		}
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void shiftChangeEdited(PherogramShiftChangeUpdateEvent event) {
		if (event.getSource() instanceof PherogramAreaModel) {
			PherogramAreaModel model = (PherogramAreaModel) event.getSource();
			recorder.addSubedit(new PherogramShiftChangeEdit(model.getAlignmentModel(), model, event.getBaseCallIndex(), event.getRelativeShiftChange()));
		}
		
	}
}
