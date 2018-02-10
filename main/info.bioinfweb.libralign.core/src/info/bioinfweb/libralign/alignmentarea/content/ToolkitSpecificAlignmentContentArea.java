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
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.ToolkitSpecificAlignmentRowsArea;



/**
 * Interface for all toolkit specific components displaying the contents of an alignment area (not including
 * the parent scroll container which is part of implementations of {@link ToolkitSpecificAlignmentArea}).
 * 
 * <h3><a id="developer"></a>Notes for developers</h3>
 * This interface should only contain methods that need to be called from the <i>core</i> module and that can
 * be implemented by all components, not matter if they contain subcomponents or not. Methods that are shared
 * by some or all implementations but are not called from the <i>core</i> module should not be added to this
 * interface.
 *  
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface ToolkitSpecificAlignmentContentArea extends ToolkitSpecificAlignmentRowsArea<AlignmentSubArea> {
  /**
	 * Determines whether this component contains nested components for each sequence or data area 
	 * or if all its contents are painted directly.
	 * 
	 * @return {@code true} if subcomponents are used, {@code false} otherwise
	 * @since 0.5.0
   */
  public boolean hasSubcomponents();

	/**
	 * Forces the repaint of all displayed sequences. In implementations that contain subcomponents 
	 * all (visible) {@link SequenceArea}s (not data areas) are repainted.
	 */
	public void repaintSequences();
}
