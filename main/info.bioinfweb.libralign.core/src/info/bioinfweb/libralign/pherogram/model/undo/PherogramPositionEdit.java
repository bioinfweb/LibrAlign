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


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;



public abstract class PherogramPositionEdit<M extends AlignmentModel<T>, T, D extends PherogramComponentModel> extends PherogramModelEdit<M, T, D> {
	private int oldPosition;
	private int newPosition;
	
	
	public PherogramPositionEdit(M alignmentModel, D pherogramModel, int oldPosition, int newPosition) {
		super(alignmentModel, pherogramModel);
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}


	public int getOldPosition() {
		return oldPosition;
	}


	public int getNewPosition() {
		return newPosition;
	}
}
