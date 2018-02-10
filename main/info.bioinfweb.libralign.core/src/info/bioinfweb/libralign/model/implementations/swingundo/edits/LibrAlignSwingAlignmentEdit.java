/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.implementations.swingundo.edits;


import info.bioinfweb.commons.swing.AbstractDocumentEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;



/**
 * All edit objects created by {@link SwingUndoAlignmentModel} are inherited from this class which
 * implements basic functionality.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public abstract class LibrAlignSwingAlignmentEdit<T> extends AbstractDocumentEdit {
  private SwingUndoAlignmentModel<T> model;

  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param model the alignment model creating this instance 
	 */
	public LibrAlignSwingAlignmentEdit(SwingUndoAlignmentModel<T> model) {
		super();
		this.model = model;
	}


	/**
	 * Returns the data provider instance that created and uses this object.
	 * 
	 * @return an instance of {@link SwingUndoAlignmentModel}
	 */
	public SwingUndoAlignmentModel<T> getModel() {
		return model;
	}


	/**
	 * Delegates to {@link SwingUndoAlignmentModel#registerDocumentChange()} of the associated
	 * data provider.
	 */
	@Override
	protected void registerDocumentChange() {
		getModel().registerDocumentChange();
	}	
}
