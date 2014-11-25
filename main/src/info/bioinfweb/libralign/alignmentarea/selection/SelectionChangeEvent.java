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
package info.bioinfweb.libralign.alignmentarea.selection;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.util.EventObject;



/**
 * Event that happens if the selection in an {@link AlignmentArea} changes.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SelectionChangeEvent extends EventObject {
	public SelectionChangeEvent(SelectionModel source) {
		super(source);
	}

	
	@Override
	public SelectionModel getSource() {
		return (SelectionModel)super.getSource();
	}	
}
