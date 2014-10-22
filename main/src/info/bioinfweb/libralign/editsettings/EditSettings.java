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
	
	
	public void changeInsert() {
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
}
