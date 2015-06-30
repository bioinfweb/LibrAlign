/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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


import java.util.EventObject;



/**
 * 
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class EditSettingsChangeEvent extends EventObject {
	private WorkingMode previousWorkingMode;
	
	
	/**
	 * Creates a new instance of this class. The previous working mode is set to the current working mode
	 * of {@code source}.
	 * 
	 * @param source - the object firing this event
	 */
	public EditSettingsChangeEvent(EditSettings source) {
		super(source);
		previousWorkingMode = source.getWorkingMode();
	}

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source - the object firing this event
	 * @param previousWorkingMode - the working mode {@code source} had before this event happened
	 */
	public EditSettingsChangeEvent(Object source, WorkingMode previousWorkingMode) {
		super(source);
		this.previousWorkingMode = previousWorkingMode;
	}


	/* (non-Javadoc)
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public EditSettings getSource() {
		return (EditSettings)super.getSource();
	}


	/**
	 * Returns the working mode that the source object had before this event happened.
	 * 
	 * @return the previous working mode
	 */
	public WorkingMode getPreviousWorkingMode() {
		return previousWorkingMode;
	}
}
