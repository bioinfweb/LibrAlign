/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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


import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;



/**
 * This event indicates that the name of an instance of {@link CharSet} has been changed. It contains 
 * a property for the previous name. The current (new) name can be obtained from {@link #getCharSet()}.
 * <p>
 * Note that {@link CharSetDataModel}s access character sets using string IDs. These IDs are independent
 * of the name and can never change during the lifetime of an character set. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class CharSetRenamedEvent extends CharSetDataModelChangeEvent {
	private String previousName;
	
	
	public CharSetRenamedEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet charSet, String previousName) {
		super(source, lastEvent, charSetID, charSet);
		this.previousName = previousName;
	}


	public String getPreviousName() {
		return previousName;
	}
}
