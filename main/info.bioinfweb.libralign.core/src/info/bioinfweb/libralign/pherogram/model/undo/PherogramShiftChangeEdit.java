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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;



public class PherogramShiftChangeEdit<M extends AlignmentModel<T>, T> extends PherogramModelEdit<M, T, PherogramAreaModel> {
	private int baseCallIndex;
	private int relativeShiftChange;
	
	
	public PherogramShiftChangeEdit(M alignmentModel, PherogramAreaModel pherogramModel, int baseCallIndex, int relativeShiftChange) {
		super(alignmentModel, pherogramModel);
		this.baseCallIndex = baseCallIndex;
		this.relativeShiftChange = relativeShiftChange;
	}


	@Override
	public void redo() throws CannotRedoException {
		getPherogramModel().addShiftChange(baseCallIndex, relativeShiftChange);
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		getPherogramModel().addShiftChange(baseCallIndex, -relativeShiftChange);  //TODO Test of this operation (including all automatic shift combinations) is fully symmetric. (It should be.)
		super.undo();
	}

	
	@Override
	public String getPresentationName() {
		return "Edit pherogram distorsion";
	}
}
