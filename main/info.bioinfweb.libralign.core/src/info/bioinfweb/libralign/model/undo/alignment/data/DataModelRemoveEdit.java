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
package info.bioinfweb.libralign.model.undo.alignment.data;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.undo.DataModelEdit;



public class DataModelRemoveEdit<M extends AlignmentModel<T>, T, D extends DataModel<?>> extends DataModelAddRemoveEdit<M, T, D>{

	
	public DataModelRemoveEdit(M alignmentModel, D dataModel) {
		super(alignmentModel, dataModel);
	}

	
	@Override
	public void redo() throws CannotRedoException {
		removeDataModel();
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		addDataModel();
		super.undo();
	} 
	
	
	@Override
	public String getPresentationName() {
		return getDataModel() + " was removed from the DataModelList.";
	} 

}
