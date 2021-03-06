/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.selection;


import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



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
  private boolean cursorOnly = true;
  private int cursorStartRow = 0;
  private int cursorStartColumn = 0;
	private List<SelectionListener<GenericEventObject<SelectionModel>>> selectionListeners = 
			new ArrayList<SelectionListener<GenericEventObject<SelectionModel>>>(16);


	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner - the alignment content area that will be using this selection object
	 */
	public SelectionModel(AlignmentArea owner) {
		super();
		this.owner = owner;
		cursor = new AlignmentCursor(owner);
	}


	/**
	 * Returns the alignment content area that uses this selection object.
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


	/**
	 * Returns the current column position of the alignment cursor.
	 *
	 * @return the index of the column where the cursor is currently located
	 */
	public int getCursorColumn() {
		return cursor.getColumn();
	}


	/**
	 * Returns the topmost row included in the current alignment cursor.
	 *
	 * @return the index of topmost row where the cursor is currently located
	 */
	public int getCursorRow() {
		return cursor.getRow();
	}


	/**
	 * Returns the current height of the alignment cursor (which is equal to the height of a possible selection).
	 *
	 * @return a value greater 0
	 * @see #getWidth()
	 */
	public int getCursorHeight() {
		return cursor.getHeight();
	}


	/**
	 * Returns the column where the current selection was started.
	 *
	 * @return the column which was first added to the current selection
	 */
	public int getStartColumn() {
		return cursorStartColumn;
	}


	/**
	 * Returns the row where the current selection was started.
	 *
	 * @return the row which was first added to the current selection
	 */
	public int getStartRow() {
		return cursorStartRow;
	}


	/**
	 * Moves the cursor to a new column, leaving its row and height unchanged.
	 * <p>
	 * The selection will always be empty after calling this method.
	 *
	 * @param column - the new column where the cursor shall be located
	 */
	public void setNewCursorColumn(int column) {
    setNewCursorPosition(column, cursor.getRow(), cursor.getHeight());
	}


	/**
	 * Specifies a new row position of the cursor. Column and height remain unchanged.
	 * <p>
	 * The selection will always be empty after calling this method.
	 *
	 * @param row - the new top most row of the cursor
	 */
	public void setNewCursorRow(int row) {
    setNewCursorPosition(cursor.getColumn(), row, cursor.getHeight());
	}


	/**
	 * Specifies a new location of the cursor. The height will remain unchanged if there is enough space in the
	 * alignment.
	 * <p>
	 * The selection will always be empty after calling this method.
	 *
	 * @param column - the column where the cursor shall be located
	 * @param row - the new top most row of the cursor
	 */
	public void setNewCursorPosition(int column, int row) {
    setNewCursorPosition(column, row, cursor.getHeight());
	}


	/**
	 * Adopts the selection from another instance.
	 *
	 * @param otherModel - another selection model instance
	 */
	public void adoptFromOther(SelectionModel otherModel) {
		columnSelection.adoptFromOther(otherModel.columnSelection);
		rowSelection.adoptFromOther(otherModel.rowSelection);
		cursor.adoptFromOther(otherModel.cursor);

		cursorOnly = otherModel.cursorOnly;
	  cursorStartRow = otherModel.cursorStartRow;
	  cursorStartColumn = otherModel.cursorStartColumn;

	  fireSelectionChanged();
	}


	private int bringCursorColumnInRange(int column) {
		 return Math.max(0, Math.min(column, getOwner().getAlignmentModel().getMaxSequenceLength()));
	}


	private int bringRowInRange(int row) {
		 return Math.max(0, Math.min(row, getOwner().getAlignmentModel().getSequenceCount() - 1));
	}


	/**
	 * Specifies a new location and height for the cursor. The selection will always be empty after calling this method.
	 *
	 * @param column - the column where the cursor shall be located
	 * @param row - the new top most row of the cursor
	 * @param height - the new height of the cursor (must be >= 1)
	 */
	public void setNewCursorPosition(int column, int row, int height) {
		column = bringCursorColumnInRange(column);
		row = bringRowInRange(row);
		cursorStartRow = row;
		cursorStartColumn = column;
		height = Math.max(1, Math.min(getOwner().getAlignmentModel().getSequenceCount() - row, height));
		clear();
		cursor.setColumnRowHeight(column, row, height);
    fireSelectionChanged();
	}


	/**
	 * Extends the current selection to include the specified cell and positions the cursor there.
	 * <p>
	 * The end of the selection does not necessarily have to be on the bottom right side of the selected
	 * rectangle, but can be at any side.
	 *
	 * @param column - the new cursor position
	 * @param row - the new cursor position
	 */
	public void setSelectionEnd(int column, int row) {
		column = bringCursorColumnInRange(column);
		row = bringRowInRange(row);

		cursorOnly = cursorOnly && (column == cursor.getColumn());
		if (cursorOnly) {
			if (cursorStartRow < row) {
				cursor.setHeight(row - cursorStartRow + 1);
			}
			else {
				cursor.setRow(row);
				cursor.setHeight(cursorStartRow - row + 1);
			}
		}
		else {
			if (column == getStartColumn()) {  // Selection shrunk to zero
				clear();
				cursor.setColumn(column);
				cursorOnly = true;
			}
			else {
				if (isEmpty()) {  // Define selection start
					if (column < cursor.getColumn()) {
						columnSelection.setNewSelection(cursor.getColumn() - 1);
					}
					else {
						columnSelection.setNewSelection(cursor.getColumn());
					}
					rowSelection.setNewSelection(cursorStartRow);
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

				if (!movedSidewards && (getStartColumn() == cursor.getColumn())) {
					clear();
				}
			}
		}
    fireSelectionChanged();
	}


	/**
	 * Selects all cells in the alignment.
	 */
	public void selectAll() {
		columnSelection.selectAll();
		rowSelection.selectAll();
		AlignmentModel<?> model = getOwner().getAlignmentModel();
		cursor.setColumnRowHeight(model.getMaxSequenceLength(), 0, model.getSequenceCount());
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


	/**
	 * Returns the index of the first column that is contained in the selection.
	 *
	 * @return the index of the first selected column or the cursor position if the selection is empty
	 */
	public int getFirstColumn() {
		if (columnSelection.isEmpty()) {
			return getCursorColumn();
		}
		else {
			return columnSelection.getFirstPos();
		}
	}


	/**
	 * Returns the index of the the last column that is contained in the selection.
	 *
	 * @return the index of the last selected column or the cursor position if the selection is empty
	 */
	public int getLastColumn() {
		if (columnSelection.isEmpty()) {
			return getCursorColumn();
		}
		else {
			return columnSelection.getLastPos();
		}
	}


	/**
	 * Returns the index of the the first row that is contained in the selection.
	 *
	 * @return the index of the first selected row or the topmost cursor row if the selection is empty
	 */
	public int getFirstRow() {
		if (rowSelection.isEmpty()) {
			return getCursorRow();
		}
		else {
			return rowSelection.getFirstPos();
		}
	}


	/**
	 * Returns the index of the the last row that is contained in the selection.
	 *
	 * @return the index of the last selected row or the bottommost cursor row if the selection is empty
	 */
	public int getLastRow() {
		if (rowSelection.isEmpty()) {
			return getCursorRow() + getCursorHeight() - 1;
		}
		else {
			return rowSelection.getLastPos();
		}
	}


	/**
	 * Returns the number of columns that are currently part of the selection.
	 *
	 * @return a value greater than 0 or 0 if the selection is empty
	 * @see #getCursorHeight()
	 */
	public int getWidth() {
		return columnSelection.getLength();
	}


	/**
	 * Clears the current selection. (The cursor height will not be influenced by this operation.)
	 */
	public void clear() {
		columnSelection.clear();
		rowSelection.clear();
	}


	/**
	 * Checks if any columns or rows are currently selected.
	 *
	 * @return {@code true} if no cells are currently contained in the selection, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return columnSelection.isEmpty() && rowSelection.isEmpty();
	}


	/**
	 * Adds a lister to this object that will be informed about future changes of the selection.
	 *
	 * @param listener - the listener object to be notified in the future
	 * @return {@code true} (as specified by {@link Collection#add(Object)})
	 */
	public boolean addSelectionListener(SelectionListener<GenericEventObject<SelectionModel>> listener) {
		return selectionListeners.add(listener);
	}


	/**
	 * Removes the specified listener from this objects list.
	 *
	 * @param listener - the listener to be removed
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean removeSelectionListener(SelectionListener<GenericEventObject<SelectionModel>> listener) {
		return selectionListeners.remove(listener);
	}


	/**
	 * Informs all listeners that the selection changed.
	 */
	@SuppressWarnings("unchecked")
	protected void fireSelectionChanged() {
		GenericEventObject<SelectionModel> e = new GenericEventObject<SelectionModel>(this);
		for (SelectionListener<GenericEventObject<SelectionModel>> listener : selectionListeners.toArray(new SelectionListener[selectionListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.selectionChanged(e);
		}
	}
}
