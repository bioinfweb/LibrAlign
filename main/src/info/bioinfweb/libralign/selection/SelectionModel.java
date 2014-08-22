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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.libralign.AlignmentArea;



/**
 * The selection model of {@link AlignmentArea}. Stores the cursor position and height as well as the selected area.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see AlignmentArea
 */
public class SelectionModel {
  private AlignmentArea owner;
  private SelectionType type = SelectionType.CELLS;
  private OneDimensionalSelection columnSelection = new OneDimensionalSelection(this, SelectionDimension.COLUMN);
  private OneDimensionalSelection rowSelection = new OneDimensionalSelection(this, SelectionDimension.ROW);
  private AlignmentCursor cursor;
	private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>(16);
  
  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will be using this selection object
	 */
	public SelectionModel(AlignmentArea owner) {
		super();
		this.owner = owner;
		cursor = new AlignmentCursor(owner);
	}


	/**
	 * Returns the alignment area that uses this selection object.
	 */
	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns the currently used selection pattern.
	 */
	public SelectionType getType() {
		return type;
	}


	/**
	 * Switches the selection type.
	 * <p>
	 * If a change from {@link SelectionType#CELLS} to {@link SelectionType#COLUMN_ONLY} is done,
	 * all rows become automatically selected, if the column selection is not empty.   
   * If a change from {@link SelectionType#CELLS} to {@link SelectionType#ROW_ONLY} is done,
	 * all columns become automatically selected, if the row selection is not empty.
	 * </p>
	 * 
	 * @param type -  the new selection type
	 */
	public void setType(SelectionType type) {
		if (type.equals(SelectionType.CELLS)) {
			switch (this.type) {
				case COLUMN_ONLY:
					if (columnSelection.isEmpty()) {
						rowSelection.clear();
					}
					else {
						rowSelection.selectAll();
					}
					break;
				case ROW_ONLY:
					if (rowSelection.isEmpty()) {
						columnSelection.clear();
					}
					else {
						columnSelection.selectAll();
					}
					break;
				case CELLS:  // Nothing to do.
					break;
			}
		}
		this.type = type;
		fireSelectionChanged();
	}
	
	
	public int getCursorColumn() {
		return cursor.getColumn();
	}
	
	
	public int getCursorRow() {
		return cursor.getRow();
	}
	
	
	public int getCursorHeight() {
		return cursor.getHeight();
	}
	
	
	public void setNewCursorColumn(int column) {
    setNewCursorPosition(column, cursor.getRow(), cursor.getHeight());
	}
	
	
	public void setNewCursorRow(int row) {
    setNewCursorPosition(cursor.getColumn(), row, cursor.getHeight());
	}
	
	
	public void setNewCursorPosition(int column, int row) {
    setNewCursorPosition(column, row, cursor.getHeight());
	}
	
	
	public void setNewCursorPosition(int column, int row, int height) {
		clear();
		cursor.setColumnRowHeight(column, row, height);
    fireSelectionChanged();
	}
	
	
	private int bringCursorColumnInRange(int column) {
		 return Math.max(0, Math.min(column, getOwner().getSequenceProvider().getMaxSequenceLength()));
	}
	
	
	private int bringRowInRange(int row) {
		 return Math.max(0, Math.min(row, getOwner().getSequenceProvider().getSequenceCount() - 1));
	}
	
	
	/**
	 * @param column - the new cursor position
	 * @param row - the new cursor position
	 */
	public void setSelectionEnd(int column, int row) {
		column = bringCursorColumnInRange(column);
		row = bringRowInRange(row);
		
		if (((column == getStartColumn()) && (cursor.getColumn() > column)) ||  // Selection shrunk to zero from right
				((column == getStartColumn() + 1) && (cursor.getColumn() < column))) {  // Selection shrunk to zero from left
			
			clear();  // Selection needs to be cleared because the new start point might be different depending on the direction of the next cursor move.
			cursor.setColumn(column);
		}
		else {
			boolean wasEmpty = isEmpty();
			if (isEmpty()) {  // Define selection start
				if (column < cursor.getColumn()) {
					columnSelection.setNewSelection(cursor.getColumn() - 1);
				}
				else {
					columnSelection.setNewSelection(cursor.getColumn());
				}
				rowSelection.setNewSelection(cursor.getRow());
			}
			
			if (column <= getStartColumn()) {
				columnSelection.setSelectionEnd(column);
			}
			else {
				columnSelection.setSelectionEnd(column - 1);
			}
			rowSelection.setSelectionEnd(row);

			boolean movedSidewards = column != cursor.getColumn();
			cursor.setColumn(column);
			cursor.setRow(rowSelection.getFirstPos());
			cursor.setHeight(rowSelection.getLength());
			
			if (!movedSidewards && wasEmpty && (getStartColumn() == cursor.getColumn())) {
				clear();  // Avoid one column wide selection when cursor height is changed.
			}
		}
    fireSelectionChanged();
	}

	
	/**
	 * Checks if the specified cell is contained in this selection.
	 * <p>
	 * If the selection type is {@link SelectionType#COLUMN_ONLY} (or {@link SelectionType#ROW_ONLY})
	 * and the selection is not empty all row (or columns) are considered as selected.
	 * 
	 * @param column - the column index of the cell to be checked
	 * @param row - the row index of the cell to be checked
	 * @return {@code true} if the specified cell is contained in the selection, {@code false} otherwise
	 */
	public boolean isSelected(int column, int row) {
		boolean columnSelected = columnSelection.isSelected(column);
		boolean rowSelected = rowSelection.isSelected(row);
		return (getType().equals(SelectionType.CELLS) && columnSelected && rowSelected) ||
				(getType().equals(SelectionType.ROW_ONLY) && rowSelected) ||
				(getType().equals(SelectionType.COLUMN_ONLY) && columnSelected);
	}
	
	
	public int getStartColumn() {
		return columnSelection.getStartPos();
	}
	
	
	public int getStartRow() {
		return rowSelection.getStartPos();
	}
	
	
	public void clear() {
		columnSelection.clear();
		rowSelection.clear();
	}
	
	
	public boolean isEmpty() {
		return columnSelection.isEmpty() && rowSelection.isEmpty();
	}
	
	
	/**
	 * Adds a lister to this object that will be informed about future changes of the selection.
	 * 
	 * @param listener - the listener object to be notified in the future
	 * @return {@code true} (as specified by {@link Collection#add(Object)}) 
	 */
	public boolean addSelectionListener(SelectionListener listener) {
		return selectionListeners.add(listener);
	}
	

	/**
	 * Removes the specified listener from this objects list.
	 * 
	 * @param listener - the listener to be removed 
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean removeSelectionListener(SelectionListener listener) {
		return selectionListeners.remove(listener);
	}


	/**
	 * Informs all listeners that the selection changed.
	 */
	protected void fireSelectionChanged() {
		Iterator<SelectionListener> iterator = selectionListeners.iterator();
		SelectionChangeEvent e = new SelectionChangeEvent(this);
		while (iterator.hasNext()) {
			iterator.next().selectionChanged(e);
		}

		//TODO Check if the following has to be implemented here:
		//Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();
	}
}
