/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.rowsarea;


import java.util.Iterator;

import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.toolkit.ToolkitComponent;



/**
 * Interface to be implemented by all classes displaying alignment associated components in rows. (One row for
 * each sequence and data area.)
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface ToolkitSpecificAlignmentRowsArea<C extends TICComponent> extends ToolkitComponent {
  /**
   * Recreates the components displaying sequences and data areas in the alignment according to
   * the current model information. 
   */
  public void reinsertSubelements();
  
	public Iterator<C> subAreaIterator();
}
