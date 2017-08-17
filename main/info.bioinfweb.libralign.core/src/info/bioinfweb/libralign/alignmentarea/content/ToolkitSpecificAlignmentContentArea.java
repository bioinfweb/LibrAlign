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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.ToolkitSpecificAlignmentRowsArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.tic.TICComponent;



/**
 * Interface for all toolkit specific components displaying the contents of an alignment area (not including
 * the parent scroll container which is part of implementations of {@link ToolkitSpecificAlignmentArea}).
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface ToolkitSpecificAlignmentContentArea extends ToolkitSpecificAlignmentRowsArea<TICComponent> {
  /**
   * Returns the height of this component.
   * 
   * @return the height in pixels
   */
  public int getHeight();
  
  /**
	 * Determines whether this component contains nested components for each sequence or data area 
	 * or if all its contents are painted directly.
	 * 
	 * @return {@code true} if subcomponents are used, {@code false} otherwise
	 * @since 0.5.0
   */
  public boolean hasSubcomponents();

  /**
   * Adds a list of data areas as children of this component.
   * 
   * @param list the data areas to be added
   */
  public void addDataAreaList(DataAreaList list);
  
	/**
	 * Forces all contained {@link SequenceArea}s (not data areas) to be repainted.
	 */
	public void repaintSequenceAreas();
}
