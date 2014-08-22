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


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.cursor.CursorChangeEvent;
import info.bioinfweb.libralign.cursor.CursorListener;



/**
 * Represents the position and height of the cursor in an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentCursor {
	private AlignmentArea owner;
  private int column = 0;
  private int row = 0;
  private int height = 1;
  private List<CursorListener> listeners = new ArrayList<CursorListener>();
  
  
	public AlignmentCursor(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the column index in front of which the cursor is located. (The initial value is 0.)
	 */
	public int getColumn() {
		return column;
	}
	
	
	private int bringColumnInRange(int column) {
		 return Math.max(0, Math.min(column, getOwner().getSequenceProvider().getMaxSequenceLength()));
	}
	
	
	public void setColumn(int column) {
		column = bringColumnInRange(column);
		if (this.column != column) {
			this.column = column;
			//fireCursorMovedResized();
		}
	}
	
	
	/**
	 * Returns the row index in front of which the cursor is located. (The initial value is 0.)
	 */
	public int getRow() {
		return row;
	}
	
	
	private int bringRowInRange(int row) {
		 return Math.max(0, Math.min(row, getOwner().getSequenceProvider().getSequenceCount() - getHeight()));
	}
	
	
	public void setRow(int row) {
		row = bringRowInRange(row);
		if (this.row != row) {
			this.row = row;
			//fireCursorMovedResized();
		}
	}
	
	
	/**
	 * Returns the height in rows if the cursor. (The initial value is 1.)
	 */
	public int getHeight() {
		return height;
	}
	
	
	private int bringHeightInRange(int height) {
		 return Math.max(1, Math.min(height, getOwner().getSequenceProvider().getSequenceCount() - getRow()));
	}
	
	
	public void setHeight(int height) {
		height = bringHeightInRange(height);
		if (this.height != height) {
			this.height = height;
			//fireCursorMovedResized();
		}
	}
	
	
	/**
	 * Sets a new column, row and height in one step. Use this method to make sure that only one event is fired for 
	 * the change of several properties
	 * 
	 * @param column - the new column index
	 * @param row - the new row index
	 * @param height - the new height (in rows)
	 */
	public void setColumnRowHeight(int column, int row, int height) {
		boolean change = this.column != column;
		this.column = bringColumnInRange(column);
		
		row = bringRowInRange(row);
		change = change || this.row != row;
		this.row = row;  // Needs to be done first, to calculate the correct range for the new height.
		
		height = bringHeightInRange(height);
		if (change || (this.height != height)) {
			this.height = height;
			//fireCursorMovedResized();
		}
	}
	
	
//	/**
//	 * Informs all listeners that this cursor moved.
//	 */
//	protected void fireCursorMovedResized() {
//		CursorChangeEvent event = new CursorChangeEvent(this);
//		for (CursorListener listener : listeners) {
//			listener.cursorMovedResized(event);
//		}
//	}
	
	
//	/**
//	 * Adds a listener to this cursor.
//	 * 
//	 * @param listener - the listener that will track changes
//	 */
//	public void addCursorListener(CursorListener listener) {
//		listeners.add(listener);
//	}
//	
//	
//	/**
//	 * Removes a listener from this cursor.
//	 * 
//	 * @param listener - the listener to be removed.
//	 * @return {@code true} if the listener was removed, {@code false} if the specified listener was not contained in the list
//	 */
//	public boolean removeCursorListener(CursorListener listener) {
//		return listeners.remove(listener);
//	}
}
