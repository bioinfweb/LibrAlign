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


import info.webinsel.util.Math2;



/**
 * Manages the selection of columns or rows.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class OneDimensionalSelection {
	public static final int NO_SELECTION = -1;
	
	
	private SelectionModel owner;
	private SelectionDimension dimension;
	private int firstPos = NO_SELECTION;
	private int lastPos = NO_SELECTION;
	private int startPos = NO_SELECTION;

	
	public OneDimensionalSelection(SelectionModel owner, SelectionDimension dimension) {
		super();
		this.owner = owner;
		this.dimension = dimension;
	}


	public SelectionModel getOwner() {
		return owner;
	}


	/**
	 * Determines of this selection represent columns or rows.
	 * 
	 * @return either {@link SelectionDimension#COLUMN} or {@link SelectionDimension#ROW}  
	 */
	public SelectionDimension getDimension() {
		return dimension;
	}


	/**
	 * Returns the first column (or row) that is contained in the selection.
	 * 
	 * @return the index of the first selected position or {@link #NO_SELECTION} if the selection is empty
	 */
	public int getFirstPos() {
		return firstPos;
	}
	
	
	/**
	 * Returns the last column (or row) that is contained in the selection.
	 * 
	 * @return the index of the last selected position or {@link #NO_SELECTION} if the selection is empty
	 */
	public int getLastPos() {
		return lastPos;
	}
	
	
  private int secureValidPos(int pos) {
  	if (pos != NO_SELECTION) {
  		if (getDimension().equals(SelectionDimension.COLUMN)) {
  			pos = Math.max(0, Math.min(getOwner().getOwner().getSequenceProvider().getMaxSequenceLength() - 1, pos));  //TODO Problematic if getMaxSequenceLength() would always be calculated over all sequences, since this method is called often.
  		}
  		else {  // SelectionDimension.ROW
  			pos = Math.max(0, Math.min(getOwner().getOwner().getSequenceProvider().getSequenceCount() - 1, pos));
  		}
  	}
  	return pos;
  }
	
  
  /**
   * Deletes the current selection and selects the column (or row) at the specified position.
   * <p>
   * If a value below 0 (except {@link OneDimensionalSelection#NO_SELECTION}) or greater than the 
   * length (or height) of the alignment is specified, the first or last column (or row) is selected.
   * </p>
   * 
   * @param pos - the index of the column (or row) to be selected
   * @throws IllegalArgumentException - if {@link #NO_SELECTION} is specified (Use {@link #clear()} instead.)
   */
  public void setNewSelection(int pos) {
  	pos = secureValidPos(pos);

  	if (pos == NO_SELECTION) {
  		throw new IllegalArgumentException("This method cannot be called with the value NO_SELECTION.");
  	}
  	else {
  		firstPos = pos;
  		lastPos = pos;
  		startPos = pos;
			getOwner().fireSelectionChanged();
  	}
  }
  
	
	/**
	 * Sets the first column (or row) of the selection.
	 * <p>
	 * If the specified position is greater than {@link #getLastPos()}, both values are swapped.
	 * </p>
	 * 
	 * @param firstPos - the index of the new selection start
	 */
	public void setFirstPos(int firstPos) {
  	firstPos = secureValidPos(firstPos);
		
		this.firstPos = firstPos;
		if ((getLastPos() < firstPos) || (firstPos == NO_SELECTION)) {  // also true if lastPos == NO_SELECTION
			lastPos = firstPos;
		}
		getOwner().fireSelectionChanged();
	}
	
	
	/**
	 * Sets the last column (or row) of the selection.
	 * <p>
	 * If the specified position is lower than {@link #getFirstPos()}, both values are swapped.
	 * If {@link #getFirstPos()} was not set, it is set to the specified value as well.
	 * </p>
	 * 
	 * @param firstPos - the index of the new selection start
	 */
	public void setLastPos(int lastPos) {
  	lastPos = secureValidPos(lastPos);
		
		if (lastPos == NO_SELECTION) {
			this.lastPos = getFirstPos();
		}
		else {
			if (getFirstPos() > lastPos) {
				this.lastPos = getFirstPos();
				firstPos = lastPos; 
			}
			else {
				if (getFirstPos() == NO_SELECTION) {
					firstPos = lastPos;
				}
				this.lastPos = lastPos;
			}
		}
		getOwner().fireSelectionChanged();
	}
	
	
	/**
	 * Checks if the specified column (or row) is included in this selection.
	 * 
	 * @param pos - the index of the column or row to be checked
	 * @return {@code true} if the specified column or row is included, {@code false} otherwise
	 */
	public boolean isSelected(int pos) {
		return Math2.isBetween(pos, getFirstPos(), getLastPos());
	}
	
	
	/**
	 * Moves the selection by the specified number of columns (or rows). The new selection
	 * will only be one column (or row) wide, no matter how wide is was before.
	 * <p>
	 * This method may be useful move through the alignment with the keyboard if no cursor 
	 * independent from the selection is used.
	 * </p>
	 * 
	 * @param count - the number of columns (or rows) to move the selection (Can be
	 *        positive or negative)
	 */
	public void moveSelection(int count) {
		int pos = 1;
		if (!isEmpty()) {
			pos = getFirstPos();
			if ((startPos != NO_SELECTION) && (startPos == getFirstPos())) {
				pos = getLastPos();
			}
		}
		startPos = pos;
		pos += count;
		setNewSelection(pos);  // calls fireColumnSelectionChanged()
	}
		
	
	/**
	 * Extends the selection in either direction so that the specified and column (or row) all 
	 * previously selected columns (or rows) are included.
	 * 
	 * @param pos - the index of the column or row that shall additionally be included in this selection
	 */
	public void extendSelectionTo(int pos) {
		if (startPos < pos) {
			firstPos = startPos;  // Not using the setter to avoid firing two events
			setLastPos(pos);
		}
		else {
			firstPos = pos;  // Not using the setter to avoid firing two events
			setLastPos(startPos);
		}
	}
	

	/**
	 * Extends the selection to the left (or to the bottom).
	 * 
	 * @param columnCount - the number of columns to be added to the selection.
	 */
	public void extendSelectionRelatively(int columnCount) {
		if (isEmpty()) {
			startPos = 1;
			firstPos = 1;  // setter not used to avoid firing a second event 
			setLastPos(columnCount);  // calls fireColumnSelectionChanged()
		}
		else {
			if ((getLastPos() > startPos) || (getFirstPos() == startPos)) {  // second condition must not be checked, if first is true
				setLastPos(getLastPos() + columnCount);  // calls fireColumnSelectionChanged()
			}
			else if (getFirstPos() < startPos) {
				setFirstPos(getFirstPos() + columnCount);  // calls fireColumnSelectionChanged()
			}
			else {
				setNewSelection(startPos);  // calls fireColumnSelectionChanged()
			}
		}
	}
	
	
	/**
	 * Selects all columns or rows (depending on {@link #getDimension()}).
	 */
	public void selectAll() {
		setFirstPos(0);
		if (getDimension().equals(SelectionDimension.COLUMN)) {
			setLastPos(getOwner().getOwner().getSequenceProvider().getMaxSequenceLength() - 1);
		}
		else {  // SelectionDimension.ROW
			setLastPos(getOwner().getOwner().getSequenceProvider().getSequenceCount() - 1);
		}
	}
		
	
	/**
	 * Checks if any columns or row (depending on {@link #getDimension()}) is selected.
	 * 
	 * @return {@code true} if any column or row is selected, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return getFirstPos() == NO_SELECTION;
	}
	
	
	/**
	 * Clears this selection.
	 */
	public void clear() {
		startPos = NO_SELECTION;
		setFirstPos(NO_SELECTION);  // lastPos will be set automatically
		getOwner().fireSelectionChanged();
	}	
}
