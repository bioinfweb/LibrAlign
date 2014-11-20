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
package info.bioinfweb.libralign.editsettings;


import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.MultipleAlignmentsContainer;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Object that stores the working mode and the edit mode (insert or overwrite) of an {@link AlignmentContentArea}
 * or a {@link MultipleAlignmentsContainer}.
 * 
 * @author BenStoever
 * @since 0.3.0
 */
public class EditSettings {
	private boolean insert = true; 
  private WorkingMode workingMode = WorkingMode.VIEW;
  private boolean insertLeftInDataArea = true;
  private List<EditSettingsListener> listeners = new ArrayList<EditSettingsListener>();
  
  
	public boolean isInsert() {
		return insert;
	}
	
	
	public void setInsert(boolean insert) {
		if (this.insert != insert) {
			this.insert = insert;
			fireInsertChanged();
		}
	}
	
	
	public void toggleInsert() {
		insert = !insert;
		fireInsertChanged();
	}
	
	
	public WorkingMode getWorkingMode() {
		return workingMode;
	}
	
	
	public void setWorkingMode(WorkingMode workingMode) {
		if (this.workingMode != workingMode) {
			WorkingMode previousWorkingMode = this.workingMode;
			this.workingMode = workingMode;
			fireWorkingModeChanged(previousWorkingMode);
		}
	}	

	
	/**
	 * Determines whether data areas displaying content that is aligned to the sequence they are attached to shall insert
	 * additional space on the left or the right if a new token is inserted into the sequence that carries the data area.
	 * <p>
	 * {@link PherogramArea} would be an example for a data area using this flag. It determines if the pherogram shall be
	 * stretched left or right of a newly inserted token which is not a gap.
	 * 
	 * @return {@code true} if new space shall be inserted left of the edit and {@code false} if new space shall be inserted 
	 *         on the right.
	 */
	public boolean isInsertLeftInDataArea() {
		return insertLeftInDataArea;
	}


	public void setInsertLeftInDataArea(boolean insertLeftInDataArea) {
		if (this.insertLeftInDataArea != insertLeftInDataArea) {
			this.insertLeftInDataArea = insertLeftInDataArea;
			fireInsertLeftInDataAreaChanged();
		}
	}


	public void toggleInsertLeftInDataArea() {
		insertLeftInDataArea = !insertLeftInDataArea;
		fireInsertLeftInDataAreaChanged();
	}
	
	
	public boolean addListener(EditSettingsListener listener) {
		return listeners.add(listener);
	}
	
	
	public boolean removeListener(EditSettingsListener listener) {
		return listeners.remove(listener);
	}
	
	
	protected void fireInsertChanged() {
		for (Iterator<EditSettingsListener> iterator = listeners.iterator(); iterator.hasNext();) {
			iterator.next().insertChanged(new EditSettingsChangeEvent(this));
		}
	}
	
	
	protected void fireWorkingModeChanged(WorkingMode previousWorkingMode) {
		for (Iterator<EditSettingsListener> iterator = listeners.iterator(); iterator.hasNext();) {
			iterator.next().workingModeChanged(new EditSettingsChangeEvent(this, previousWorkingMode));
		}
	}
	
	
	protected void fireInsertLeftInDataAreaChanged() {
		for (Iterator<EditSettingsListener> iterator = listeners.iterator(); iterator.hasNext();) {
			iterator.next().insertLeftInDataAreaChanged(new EditSettingsChangeEvent(this));
		}
	}
}
