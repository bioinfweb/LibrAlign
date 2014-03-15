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
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.libralign.AlignmentArea;



/**
 * The selection model of {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class AlignmentSelectionModel {
  private AlignmentArea owner;
  private SelectionType type = SelectionType.CELLS;
  private OneDimensionalSelection columnSelection = new OneDimensionalSelection(this, SelectionDimension.COLUMN);
  private OneDimensionalSelection rowSelection = new OneDimensionalSelection(this, SelectionDimension.ROW);
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>(16);
  
  
	public AlignmentSelectionModel(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	public SelectionType getType() {
		return type;
	}


	public void setType(SelectionType type) {
		this.type = type;
	}
	
	
	public OneDimensionalSelection getColumnSelection() {
		return columnSelection;
	}


	public OneDimensionalSelection getRowSelection() {
		return rowSelection;
	}

	
	public boolean isSelected(int column, int row) {
		boolean columnSelected = getColumnSelection().isSelected(column);
		boolean rowSelected = getRowSelection().isSelected(row);
		return (getType().equals(SelectionType.CELLS) && columnSelected && rowSelected) ||
				(getType().equals(SelectionType.ROW_ONLY) && rowSelected) ||
				(getType().equals(SelectionType.COLUMN_ONLY) && columnSelected);
	}
	
	
	public boolean addSelectionListener(SelectionListener listener) {
		return selectionListeners.add(listener);
	}
	

	public boolean removeSelectionListener(SelectionListener listener) {
		return selectionListeners.remove(listener);
	}


	protected void fireSelectionChanged() {
		Iterator<SelectionListener> iterator = selectionListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().selectionChanged(new SelectionChangeEvent(this));
		}

		//TODO Check if the following has to be implemented here:
		//repaint();  // Called here directly because this class does not register itself as a listener.
		//Main.getInstance().getMainFrame().getActionManagement().refreshActionStatus();
	}
}
