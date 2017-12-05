/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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


import java.util.EventListener;



/**
 * This interface should be implemented by classes that want to track changes of the selection
 * represented by an {@link SelectionModel} object.
 * <p>
 * Additionally, data area implementations may make use of this interface to allow tracking 
 * changes of their selection. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @param <E> the type of event fired on a selection change
 */
public interface SelectionListener<E> extends EventListener {
	/**
	 * This method is called every time the selection changes.
	 * 
	 * @param event an object containing further information on this event
	 */
	public void selectionChanged(E event);
}
