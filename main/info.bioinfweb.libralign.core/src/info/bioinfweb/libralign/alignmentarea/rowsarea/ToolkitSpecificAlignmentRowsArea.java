/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.ToolkitComponent;



/**
 * Interface to be implemented by all classes displaying alignment associated components in rows. (One row for
 * each sequence or data area.)
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface ToolkitSpecificAlignmentRowsArea<C extends TICComponent> extends ToolkitComponent {
  /**
   * Recreates the subcomponents displaying sequences and data areas in the alignment according to
   * the current model information. For implementations that do not use subcomponents, calling this
   * method may have no effect. 
   */
  public void reinsertSubelements();
}
