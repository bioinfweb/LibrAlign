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
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.commons.tic.toolkit.ToolkitComponent;
import info.bioinfweb.libralign.AlignmentSubArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;



/**
 * Interface for all toolkit specific components displaying the contents of an alignment area (not including
 * the parent scroll container which is part of implementations of {@link ToolkitSpecificAlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public interface ToolkitSpecificAlignmentContentArea extends ToolkitComponent {
  /**
   * Recreates the components displaying sequences and data areas in the alignment according to
   * the current model information. 
   */
  public void reinsertSubelements();
  
	public void addDataAreaList(DataAreaList list);
	
	public AlignmentSubArea getAreaByY(int y);
	
  /**
   * Returns the {@link SequenceArea} inside this area that displays the sequence with the specified ID.
   * 
   * @param sequenceID - the ID of the sequence displayed in the returned area
   * @return the GUI component or {@code null} if no sequence with the specified ID is displayed in this area
   */
  public SequenceArea getSequenceAreaByID(int sequenceID);
  
  /**
   * Returns the height of this component.
   * 
   * @return the height in pixels
   */
  public int getHeight();
  
  /**
   * Delegates to the {@link SequenceArea} contained in this component to assign its current size.
   * 
   * @param sequenceID - the ID of the sequence displayed in the sequence area to be resized
   * @throws IllegalArgumentException if no sequence area for the specified sequence ID was found
   */
  public void assignSequenceAreaSize(int sequenceID) throws IllegalArgumentException;
}
