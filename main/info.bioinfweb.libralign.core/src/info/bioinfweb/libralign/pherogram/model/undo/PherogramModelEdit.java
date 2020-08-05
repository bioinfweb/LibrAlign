/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben StÃ¶ver
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
import info.bioinfweb.libralign.model.undo.AbstractDataModelEdit;
import info.bioinfweb.libralign.model.undo.AbstractAlignmentModelEdit;
import info.bioinfweb.libralign.model.undo.DataModelEdit;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;



public abstract class PherogramModelEdit<M extends AlignmentModel<T>, T, D extends PherogramComponentModel> extends AbstractDataModelEdit<M,T,PherogramModelListener>{
	private D pherogramModel;

	
	public PherogramModelEdit(M alignmentModel, D pherogramModel) {
		super(alignmentModel, (PherogramAreaModel) pherogramModel);
		if (pherogramModel == null) {
			throw new IllegalArgumentException("pherogramModel must not be null.");
		}
		this.pherogramModel = pherogramModel;
	}


	public PherogramAreaModel getPherogramModel() {
		return (PherogramAreaModel) pherogramModel;
	}
	
}
