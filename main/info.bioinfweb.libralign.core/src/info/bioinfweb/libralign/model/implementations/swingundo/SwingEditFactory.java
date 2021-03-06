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
package info.bioinfweb.libralign.model.implementations.swingundo;


import info.bioinfweb.libralign.model.implementations.swingundo.edits.LibrAlignSwingAlignmentEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingAddSequenceEdit;
import info.bioinfweb.libralign.model.implementations.swingundo.edits.sequence.SwingConcreteAddSequenceEdit;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;



/**
 * Classes providing custom edit objects to an instance of {@link SwingUndoAlignmentModel} must
 * implement this interface.
 * <p>
 * The type of edit that shall be created can be obtained by the class type of the passed {@code edit}
 * objects. These objects provide getter methods that allow to access the relevant data. The returned object
 * might also be a wrapper of the passed edit.
 * <p>
 * <b>Important:</b> Edit objects are executed by {@link SwingUndoAlignmentModel} and added to the linked 
 * {@link UndoManager} after it called one of the methods specified here, which means that implementing 
 * classes should not execute or add the the edits they return to the undo manager by themselves within
 * implementations of methods of this interface.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see SwingUndoAlignmentModel
 */
public interface SwingEditFactory<T> {
  /**
   * This method is called to create new edit objects (except if a new sequence shall be added).
   * 
   * @param edit the default <i>LibrAlign</i> edit that would perform the requested operation
   * @return the edit object that shall be used by the calling instance of {@link SwingUndoAlignmentModel}
   */
  public UndoableEdit createEdit(LibrAlignSwingAlignmentEdit<T> edit);	

  /**
   * This method is called if an edit object that inserts a new sequence shall be created.
   * 
   * @param edit the default <i>LibrAlign</i> edit that would perform the requested operation.
   * @return the edit object that shall be used by the calling instance of {@link SwingUndoAlignmentModel}
   */
  public SwingAddSequenceEdit createAddSequenceEdit(SwingConcreteAddSequenceEdit<T> edit);
  //TODO Why is there a separate method for this type of edit? => Either provide more specific methods or use only one.
}
