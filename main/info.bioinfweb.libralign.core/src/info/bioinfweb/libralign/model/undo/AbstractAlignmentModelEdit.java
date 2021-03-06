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
package info.bioinfweb.libralign.model.undo;


import info.bioinfweb.commons.swing.AbstractDocumentEdit;
import info.bioinfweb.libralign.model.AlignmentModel;



public abstract class AbstractAlignmentModelEdit<M extends AlignmentModel<T>, T> extends AbstractDocumentEdit {
	private M alignmentModel;


	public AbstractAlignmentModelEdit(M alignmentModel) {
		super();
		if (alignmentModel == null) {
			throw new IllegalArgumentException("alignmentModel must not be null.");
		}
		else {
			this.alignmentModel = alignmentModel;
			setIsSubedit(true);
		}
	}


	public M getAlignmentModel() {
		return alignmentModel;
	}


	@Override
	protected void registerDocumentChange() {}  //TODO Call method of EventRecorder or alert listener?
}
