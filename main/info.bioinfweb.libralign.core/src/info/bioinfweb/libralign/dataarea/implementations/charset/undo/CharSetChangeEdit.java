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
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;
import info.bioinfweb.libralign.model.AlignmentModel;



public abstract class CharSetChangeEdit <M extends AlignmentModel<T>, T> extends CharSetModelEdit<M, T>{
	private CharSet newCharSet;
	private CharSet oldCharSet;
	private CharSetDataModel source;
	private String key;
	
	
	public CharSetChangeEdit(CharSetDataModel source, M alignmentModel, CharSet newCharSet, CharSet oldCharSet, String key) {
		super(alignmentModel, newCharSet);
		this.newCharSet = newCharSet;
		this.oldCharSet = oldCharSet;
		this.source = source;
		this.key = key;
	}


	public void addCharSet() {
		getSource().put(getKey(), getNewCharSet());
	}
	
	
	public void removeCharSet() {
		getSource().remove(getKey());
	}


	public CharSet getNewCharSet() {
		return newCharSet;
	}


	public CharSet getOldCharSet() {
		return oldCharSet;
	}


	public CharSetDataModel getSource() {
		return source;
	}


	public String getKey() {
		return key;
	}

	
	
}
