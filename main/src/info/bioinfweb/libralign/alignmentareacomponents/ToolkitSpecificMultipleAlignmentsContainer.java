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
package info.bioinfweb.libralign.alignmentareacomponents;


import java.awt.Rectangle;

import info.bioinfweb.commons.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.MultipleAlignmentsContainer;



/**
 * Common interface for toolkit specific components displaying the contents of a {@link MultipleAlignmentsContainer}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface ToolkitSpecificMultipleAlignmentsContainer extends ToolkitComponent {
	/**
	 * Returns the contained toolkit specific component displaying the part of the alignment area
	 * at the specified position.
	 * 
   * @param alignmentIndex - the index of the alignment in this component
	 * @return an instance of {@link SwingAlignmentArea} or {@link SWTAlignmentArea}
	 */
	public ToolkitSpecificAlignmentArea getPartArea(int alignmentIndex);
	
	/**
	 * This method is used internally by {@link MultipleAlignmentsContainer} of the order or set of contained
	 * alignment areas changes and these changes shall be reflected by the underlying GUI component.
	 */
	public void adoptChildAreas();
	
	/**
	 * Distributes the available height to the head, content, and bottom area. The previous distribution is not
	 * considered.
	 */
  public void redistributeHeight();
}
