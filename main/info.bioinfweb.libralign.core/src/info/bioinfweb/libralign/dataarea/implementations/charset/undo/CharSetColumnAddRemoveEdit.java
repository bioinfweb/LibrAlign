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


import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;



public abstract class CharSetColumnAddRemoveEdit <M extends AlignmentModel<T>, T, D extends DataModel<?>> extends CharSetModelEdit<M, T, D>{
	private int firstPos;
	private int lastPos;
	
	
	public CharSetColumnAddRemoveEdit(M alignmentModel, CharSet charSet, int firstPos, int lastPos, D dataModel) {
		super(alignmentModel, charSet, dataModel);
		this.firstPos = firstPos;
		this.lastPos = lastPos;
	}
	
	
	public int getFirstPosition() {
		return firstPos;
	}


	public int getLastPosition() {
		return lastPos;
	}

	
	public void columnAdd() {
		getCharSet().add(getFirstPosition(), getLastPosition());
	}
	
	
	public void columnRemove() {
		getCharSet().remove(getFirstPosition(), getLastPosition());
	}
}
