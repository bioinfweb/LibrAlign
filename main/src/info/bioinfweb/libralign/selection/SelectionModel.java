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
 * The selection model of {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see AlignmentArea
 * @see OneDimensionalSelection
 */
public class SelectionModel {
  private AlignmentArea owner;
  private SelectionType type = SelectionType.CELLS;
  private OneDimensionalSelection columnSelection = new OneDimensionalSelection(this, SelectionDimension.COLUMN);
  private OneDimensionalSelection rowSelection = new OneDimensionalSelection(this, SelectionDimension.ROW);
	private AlignmentCursor cursor = new AlignmentCursor();
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>(16);
  
  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will be using this selection object
	 */
	public SelectionModel(AlignmentArea owner) {
		super();
		this.owner = owner;
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
					if (getColumnSelection().isEmpty()) {
						getRowSelection().clear();
					}
					else {
						getRowSelection().selectAll();
					}
					break;
				case ROW_ONLY:
					if (getRowSelection().isEmpty()) {
						getColumnSelection().clear();
					}
					else {
						getColumnSelection().selectAll();
					}
					break;
				case CELLS:  // Nothing to do.
					break;
			}
		}
		this.type = type;
		fireSelectionChanged();
	}
	
	
	/**
	 * Returns the column selection.
	 */
	public OneDimensionalSelection getColumnSelection() {
		return columnSelection;
	}


	/**
	 * Returns the row selection.
	 */
	public OneDimensionalSelection getRowSelection() {
		return rowSelection;
	}

	
	/**
	 * Returns the alignment cursor.
	 */
	public AlignmentCursor getCursor() {
		return cursor;
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
		boolean columnSelected = getColumnSelection().isSelected(column);
		boolean rowSelected = getRowSelection().isSelected(row);
		return (getType().equals(SelectionType.CELLS) && columnSelected && rowSelected) ||
				(getType().equals(SelectionType.ROW_ONLY) && rowSelected) ||
				(getType().equals(SelectionType.COLUMN_ONLY) && columnSelected);
	}
	
	
	public void clear() {
		getColumnSelection().clear();
		getRowSelection().clear();
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
		//repaint();  // Called here directly because this class does not register itself as a listener.
		//Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();
	}
}
