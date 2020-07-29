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


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



public class CharSetColumnAddEdit <M extends AlignmentModel<T>, T, D extends DataModel<?>> extends CharSetColumnAddRemoveEdit<M, T, D>{

	
	public CharSetColumnAddEdit(M alignmentModel, CharSet charSet, int firstPos, int lastPos, D dataModel) {
		super(alignmentModel, charSet, firstPos, lastPos, dataModel);
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		columnAdd();
		super.redo();
	}

	
	@Override
	public void undo() throws CannotUndoException {
		columnRemove();
		super.undo();
	}
	
	
	@Override
	public String getPresentationName() {
		return getFirstPosition() + " to " + getLastPosition() + " colums were added to the CharSet.";
	}
	

}
