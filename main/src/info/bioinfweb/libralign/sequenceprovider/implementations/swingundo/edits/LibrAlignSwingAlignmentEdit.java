/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.edits;


import info.bioinfweb.commons.swing.AbstractDocumentEdit;
import info.bioinfweb.libralign.sequenceprovider.implementations.swingundo.SwingUndoSequenceDataProvider;



/**
 * All edit objects created by {@link SwingUndoSequenceDataProvider} are inherited from this class which
 * implements basic functionality.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoSequenceDataProvider
 */
public abstract class LibrAlignSwingAlignmentEdit<T> extends AbstractDocumentEdit {
  private SwingUndoSequenceDataProvider<T> provider;

  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param provider - the data provider creating this instance 
	 */
	public LibrAlignSwingAlignmentEdit(SwingUndoSequenceDataProvider<T> provider) {
		super();
		this.provider = provider;
	}


	/**
	 * Returns the data provider instance that created and uses this object.
	 * 
	 * @return an instance of {@link SwingUndoSequenceDataProvider}
	 */
	public SwingUndoSequenceDataProvider<T> getProvider() {
		return provider;
	}


	/**
	 * Delegates to {@link SwingUndoSequenceDataProvider#registerDocumentChange()} of the associated
	 * data provider.
	 */
	@Override
	protected void registerDocumentChange() {
		getProvider().registerDocumentChange();
	}	
}
