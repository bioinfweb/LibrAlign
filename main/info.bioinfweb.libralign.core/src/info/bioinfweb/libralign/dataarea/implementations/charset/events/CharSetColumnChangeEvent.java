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
package info.bioinfweb.libralign.dataarea.implementations.charset.events;

import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;



/**
 * This event indicates that one or more neighboring columns have been added to or removed from an 
 * instance of {@link CharSet}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class CharSetColumnChangeEvent extends CharSetDataModelChangeEvent {
	private ListChangeType type;
	private int firstPos;
	private int lastPos;
	
	
	public CharSetColumnChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet charSet, 
			boolean added, int firstPos, int lastPos) {
		
		super(source, lastEvent, charSetID, charSet);
		this.type = added ? ListChangeType.INSERTION : ListChangeType.DELETION;
		this.firstPos = firstPos;
		this.lastPos = lastPos;
	}


	public ListChangeType getType() {
		return type;
	}


	public int getFirstPos() {
		return firstPos;
	}


	public int getLastPos() {
		return lastPos;
	}
}
