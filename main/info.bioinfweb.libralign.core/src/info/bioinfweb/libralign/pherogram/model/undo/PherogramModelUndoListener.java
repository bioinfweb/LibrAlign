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


import info.bioinfweb.libralign.pherogram.model.PherogramCutPositionChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramFirstSeqPosChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;
import info.bioinfweb.libralign.pherogram.model.PherogramProviderChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramShiftChangeUpdateEvent;



public class PherogramModelUndoListener implements PherogramModelListener {
	
	
	@Override
	public void pherogramProviderChange(PherogramProviderChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void leftCutPositionChange(PherogramCutPositionChangeEvent event) {
		//LeftCutPositionEdit<?,?,?> edit = new LeftCutPositionEdit<>(event.getSource().getAlignmentModel(), event.getSource(), event.getOldBaseCallIndex(), event.getNewBaseCallIndex());
		
	}

	
	@Override
	public void rightCutPositionChange(PherogramCutPositionChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void firstSequencePositionChange(PherogramFirstSeqPosChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void shiftChangeEdited(PherogramShiftChangeUpdateEvent event) {
		// TODO Auto-generated method stub
		
	}
}
