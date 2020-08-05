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
package info.bioinfweb.libralign.model.data;


import java.util.HashSet;
import java.util.Set;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;
import info.bioinfweb.libralign.model.undo.alignment.AlignmentModelUndoListener;



public class AbstractDataModel<L> implements DataModel<L> {
	private AlignmentModel<?> alignmentModel;
	protected Set<L> modelListeners = new HashSet<>();
	private boolean undoListenerExists;

	
	public AbstractDataModel(AlignmentModel<?> alignmentModel) {
		super();
		
		if (alignmentModel == null) {
			throw new IllegalArgumentException("The associated AlignmentModel must not be null.");
		}
		else {
			this.alignmentModel = alignmentModel;
		}
	}


	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}


	@Override
	public boolean addModelListener(L listener) {
		return modelListeners.add(listener);
	}


	@Override
	public boolean removeModelListener(L listener) {
		return modelListeners.remove(listener);
	}


	@Override
	public L createUndoListener(EditRecorder<?, ?> recorder) {
		return null; //TODO: implement not here
		// TODO Auto-generated method stub
	}


	@Override
	public void removeUndoListener() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean hasUndoListener() {
		// TODO Auto-generated method stub
		return false;
	}	
}
