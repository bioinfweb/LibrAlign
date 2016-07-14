/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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


import java.util.EventListener;



/**
 * Classes that need to be informed about changes in {@link EditSettings} should implement this interface.
 * 
 * @author BenStoever
 * @since 0.3.0
 */
public interface EditSettingsListener extends EventListener {
	/**
	 * Called if the <i>insert</i> flag of {@link EditSettings} was changed.
	 * 
	 * @param event - the event object describing the change
	 */
	public void insertChanged(EditSettingsChangeEvent event);
	
	/**
	 * Called if the working mode of {@link EditSettings} was changed.
	 * 
	 * @param event - the event object describing the change
	 */
	public void workingModeChanged(EditSettingsChangeEvent event);

	/**
	 * Called if the <i>insertLeftInDataArea</i> flag of {@link EditSettings} was changed.
	 * 
	 * @param event - the event object describing the change
	 */
	public void insertLeftInDataAreaChanged(EditSettingsChangeEvent event);
}
