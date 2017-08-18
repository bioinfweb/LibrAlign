/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;



/**
 * Provides tool methods for the GUI components of <i>LibrAlign</i>. This class is intended for internal purposes
 * and will usually not be useful in application code.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class GUITools {
	private GUITools() {
		super();
	}
	
	
	/**
	 * Used by {@link AlignmentArea} and {@link AlignmentContentArea} to determine whether to create subcomponents
	 * for each sequence and data area or not for <i>SWT</i>.
	 * 
	 * @param parameters the parameters passed to the <i>TIC</i> component factory
	 * @return {@code true} if subcomponents shall be created, {@code false} otherwise
	 */
	public static boolean determineUseSubcomponents(Object[] parameters) {
		boolean result = false;
		if ((parameters.length >= 1) && (parameters[0] instanceof Boolean)) {
			result = (Boolean)parameters[0];
		}
		return result;
	}
}
