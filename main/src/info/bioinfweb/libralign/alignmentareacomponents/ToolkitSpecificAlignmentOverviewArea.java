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
import info.bioinfweb.libralign.dataarea.DataAreaListType;



/**
 * Common interface for toolkit specific components displaying a whole alignment area. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface ToolkitSpecificAlignmentOverviewArea extends ToolkitComponent {
	/**
	 * Returns the contained toolkit specific component displaying the part of the alignment area
	 * at the specified position.
	 * 
	 * @param position - the position of the alignment part area
	 * @return an instance of {@link SwingAlignmentPartArea} or {@link SWTAlignmentPartArea}
	 */
	public ToolkitSpecificAlignmentPartArea getPartArea(DataAreaListType position);
	
  /**
   * Recreates the components displaying sequences and data areas in the alignment according to
   * the current model information. 
   */
  public void reinsertSubelements();	
  
	/**
	 * Distributes the available height to the head, content, and bottom area. The previous distribution is not
	 * considered.
	 */
  public void redistributeHeight();
  
  /**
   * Returns the {@link SequenceArea} inside this area that displays the sequence with the specified ID.
   * 
   * @param sequenceID - the ID of the sequence displayed in the returned area
   * @return the GUI component or {@code null} if no sequence with the specified ID is displayed in this area
   */
  public SequenceArea getSequenceAreaByID(int sequenceID);
  
  /**
   * Scrolls the contained components so that the specified rectangle is visible. 
   */
  public void scrollAlignmentRectToVisible(Rectangle rectangle);
  
  /**
   * Returns the rectangle in pixels of the alignment (with associated data areas) that is visible in the current
   * scroll container. 
   * 
   * @return a rectangle object
   */
  public Rectangle getVisibleAlignmentRect();
 }
