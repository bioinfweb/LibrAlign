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
package info.bioinfweb.libralign.selection;


import info.bioinfweb.libralign.AlignmentArea;



/**
 * Represents the position and height of the cursor in an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class AlignmentCursor {
  private int column = 0;
  private int row = 0;
  private int height = 1;
  
  
	/**
	 * Returns the column index in front of which the cursor is located. (The initial value is 0.)
	 */
	public int getColumn() {
		return column;
	}
	
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	
	/**
	 * Returns the row index in front of which the cursor is located. (The initial value is 0.)
	 */
	public int getRow() {
		return row;
	}
	
	
	public void setRow(int row) {
		this.row = row;
	}
	
	
	/**
	 * Returns the height in rows if the cursor. (The initial value is 1.)
	 */
	public int getHeight() {
		return height;
	}
	
	
	public void setHeight(int height) {
		this.height = height;
	}
}
