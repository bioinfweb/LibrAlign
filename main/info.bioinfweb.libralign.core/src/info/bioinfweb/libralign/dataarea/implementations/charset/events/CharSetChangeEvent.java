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


import info.bioinfweb.commons.collections.ListChangeType;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;



/**
 * This event indicates that an instance of {@link CharSet} was added, removed or replaced in an 
 * instance of {@link CharSetDataModel}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class CharSetChangeEvent extends CharSetDataModelChangeEvent {
	private ListChangeType type;
	private CharSet replacedCharSet;
	private CharSet newCharSet;
	private String charSetID;
	
	protected CharSetChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet newCharSet, 
			CharSet replacedCharSet, ListChangeType type) {
		
		super(source, lastEvent, charSetID, newCharSet);
		this.replacedCharSet = replacedCharSet;
		this.type = type;
		this.newCharSet = newCharSet;
		this.charSetID = charSetID;
	}
	
	
	public CharSetChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet charSet, boolean added) {
		this(source, lastEvent, charSetID, charSet, null, added ? ListChangeType.INSERTION : ListChangeType.DELETION);
	}
	
	
	public CharSetChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet newCharSet, 
			CharSet replacedCharSet) {
		
		this(source, lastEvent, charSetID, newCharSet, replacedCharSet, ListChangeType.REPLACEMENT);
	}
	
	
	public ListChangeType getType() {
		return type;
	}


	public CharSet getNewCharSet() {
		return newCharSet;
	}


	/**
	 * Returns a reference to the character set that has been replaced by the inserted set.
	 * 
	 * @return the replaced character set or {@code null} if none was replaced
	 */
	public CharSet getReplacedCharSet() {
		return replacedCharSet;
	}
}
