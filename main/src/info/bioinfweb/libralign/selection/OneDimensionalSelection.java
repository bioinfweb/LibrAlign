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
 * @since 1.0.0
 */
public class OneDimensionalSelection {
	public static final int NO_SELECTION = -1;
	
	
	private AlignmentSelectionModel owner;
	private SelectionDimension dimension;
	private int firstPos = NO_SELECTION;
	private int lastPos = NO_SELECTION;
	private int startPos = NO_SELECTION;

	
	public OneDimensionalSelection(AlignmentSelectionModel owner, SelectionDimension dimension) {
		super();
		this.owner = owner;
		this.dimension = dimension;
	}


	public AlignmentSelectionModel getOwner() {
		return owner;
	}


	public SelectionDimension getDimension() {
		return dimension;
	}


	public int getFirstPos() {
		return firstPos;
	}
	
	
	public int getLastPos() {
		return lastPos;
	}
	
	
  private int secureValidPos(int pos) {
  	if (pos != NO_SELECTION) {
  		if (getDimension().equals(SelectionDimension.COLUMN)) {
  			pos = Math.max(0, Math.min(getOwner().getOwner().getDataProvider().getMaxSequenceLength() - 1, pos));  //TODO Problematic if getMaxSequenceLength() would always be calculated over all sequences, since this method is called often.
  		}
  		else {  // SelectionDimension.ROW
  			pos = Math.max(0, Math.min(getOwner().getOwner().getDataProvider().getSequenceCount() - 1, pos));
  		}
  	}
  	return pos;
  }
	
  
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
  
	
	public void setFirstPos(int firstPos) {
  	firstPos = secureValidPos(firstPos);
		
		this.firstPos = firstPos;
		if ((getLastPos() < firstPos) || (firstPos == NO_SELECTION)) {  // also true if lastPos == NO_SELECTION
			lastPos = firstPos;
		}
		getOwner().fireSelectionChanged();
	}
	
	
	public void setLastPos(int lastColumn) {
  	lastColumn = secureValidPos(lastColumn);
		
		if (lastColumn == NO_SELECTION) {
			this.lastPos = getFirstPos();
		}
		else {
			if (getFirstPos() > lastColumn) {
				this.lastPos = getFirstPos();
				firstPos = lastColumn; 
			}
			else {
				if (getFirstPos() == NO_SELECTION) {
					firstPos = lastColumn;
				}
				this.lastPos = lastColumn;
			}
		}
		getOwner().fireSelectionChanged();
	}
	
	
	public boolean isSelected(int pos) {
		return Math2.isBetween(pos, getFirstPos(), getLastPos());
	}
	
	
	public void moveSelectionStart(int columnCount) {
		int pos = 1;
		if (!isEmpty()) {
			pos = getFirstPos();
			if ((startPos != NO_SELECTION) && (startPos == getFirstPos())) {
				pos = getLastPos();
			}
		}
		startPos = pos;
		pos += columnCount;
		setNewSelection(pos);  // calls fireColumnSelectionChanged()
	}
		
	
	public void extendSelectionTo(int column) {
		if (startPos < column) {
			firstPos = startPos;  // Not using the setter to avoid firing two events
			setLastPos(column);
		}
		else {
			firstPos = column;  // Not using the setter to avoid firing two events
			setLastPos(startPos);
		}
	}
	

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
		
	
	public boolean isEmpty() {
		return getFirstPos() == NO_SELECTION;
	}
	
	
	public void clear() {
		startPos = NO_SELECTION;
		setFirstPos(NO_SELECTION);  // lastPos will be set automatically
		getOwner().fireSelectionChanged();
	}	
}
