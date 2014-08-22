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
package info.bioinfweb.libralign.cursor;


import info.bioinfweb.libralign.selection.AlignmentCursor;

import java.util.EventListener;



/**
 * This interface should be implemented by classes that want to track changes of the position and height of
 * an {@link AlignmentCursor} object.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface CursorListener extends EventListener {
	/**
	 * This method is called every time the cursor is moved or its height changed.
	 * 
	 * @param event - an object containing further information on this event
	 */
	public void cursorMovedResized(CursorChangeEvent event);
}
