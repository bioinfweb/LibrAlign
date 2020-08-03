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
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModelListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.undo.AlignmentModelEdit;
import info.bioinfweb.libralign.model.undo.AbstractDataModelEdit;



public abstract class CharSetModelEdit <M extends AlignmentModel<T>, T, L> extends AbstractDataModelEdit<M, T, CharSetDataModelListener>{
	private CharSet charSet;

	
	public CharSetModelEdit(M alignmentModel, CharSet charSet, DataModel<CharSetDataModelListener> dataModel) {
		super(alignmentModel, dataModel);
		if (charSet == null) {
			throw new IllegalArgumentException("CharSet must not be null.");
		}
		this.charSet = charSet;
	}


	public CharSet getCharSet() {
		return charSet;
	}
}

