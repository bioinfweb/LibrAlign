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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.toolkit.ToolkitComponent;



/**
 * Common interface for toolkit specific components displaying the contents of a {@link MultipleAlignmentsContainer}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface ToolkitSpecificMultipleAlignmentsContainer extends ToolkitComponent {
	/**
	 * This method is used internally by {@link MultipleAlignmentsContainer} if the order or set of contained
	 * alignment areas changes and these changes shall be reflected by the underlying GUI component.
	 */
	public void adoptChildAreas();
	
  /**
   * Returns the height that is currently available to be distributed among all contained alignment areas.
   * 
   * @return the sum of the heights of all visible rectangles of the contained scrolled components
   */
  public int getAvailableHeight();
    
  /**
   * Return the height needed for the specified alignment to be displayed without scroll bars.
   * 
   * @param alignmentIndex - the current index of the alignment in the container
   * @return the height in pixels
   */
  public int getNeededHeight(int alignmentIndex);
  
  /**
   * Sets the divider locations to distribute the available height among the contained alignment areas.
   * 
   * @param heights - an array containing the heights for all alignment areas
   */
  public void setDividerLocations(int[] heights);
  
  /**
   * Returns the alignment area contained in this component, that currently has the focus. (It is also possible,
   * that a subcomponent if the returned area (e.g. a sequence or data area) has the focus.)
   * <p>
   * Note that application developers should use {@link MultipleAlignmentsContainer#getFocusedAlignmentArea()}
   * instead of calling this method directly, because that method also checks, if a toolkit-specific component
   * has already been created.
   * 
   * @return the focused alignment area in this component or {@code null} of the focus is not inside this container
   * @see MultipleAlignmentsContainer#getFocusedAlignmentArea()
   */
  public AlignmentArea getFocusedAlignmentArea();
}
