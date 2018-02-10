/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.toolkit.ScrollingToolkitComponent;



/**
 * Interface implemented by all toolkit specific components displaying the contents of an {@link AlignmentArea}.
 * 
 * <h3><a id="developer"></a>Notes for developers</h3>
 * This interface should only contain methods that need to be called from the <i>core</i> module. Methods that 
 * are shared by some or all implementations but are not called from there should not be added.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public interface ToolkitSpecificAlignmentArea extends ScrollingToolkitComponent {
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
