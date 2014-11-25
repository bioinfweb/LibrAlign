/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea;


import java.awt.Rectangle;

import info.bioinfweb.commons.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * Interface implemented by all toolkit specific components displaying the contents of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface ToolkitSpecificAlignmentArea extends ToolkitComponent {
	public int getHeight();
	
  /**
   * Scrolls the contained components so that the specified rectangle is visible.
   *  
   * @param rectangle - the rectangle in the specified alignment that shall become visible
   */
  public void scrollAlignmentRectToVisible(Rectangle rectangle);

  /**
   * Returns the rectangle in pixels of the alignment (with associated data areas) that is visible in the current
   * scroll container. 
   * 
   * @return a rectangle object
   */
  public Rectangle getVisibleAlignmentRect();
  
	/**
	 * Determines whether a horizontal scroll bar should be displayed underneath this element. (In a 
	 * {@link MultipleAlignmentsContainer} only the scroll bar of the bottom most alignment is displayed. If
	 * you use an instance of {@link AlignmentArea} is used outside {@link MultipleAlignmentsContainer} you would
	 * usually display a scroll bar.
	 * 
	 * @param hideHorizontalScrollBar - Specify {@link true} here to display a horizontal scroll bar and {@code false}
	 *        otherwise
	 */
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar);
}
