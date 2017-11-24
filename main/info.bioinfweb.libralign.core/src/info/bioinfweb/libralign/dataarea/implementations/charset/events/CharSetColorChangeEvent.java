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


import info.bioinfweb.libralign.dataarea.implementations.charset.CharSet;
import info.bioinfweb.libralign.dataarea.implementations.charset.CharSetDataModel;

import java.awt.Color;



/**
 * This event indicates that the color of an instance of {@link CharSet} has been changed. It contains 
 * a property for the previous color. The current (new) color can be obtained from {@link #getCharSet()}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class CharSetColorChangeEvent extends CharSetDataModelChangeEvent {
	private Color previousColor;

	
	public CharSetColorChangeEvent(CharSetDataModel source, boolean lastEvent, String charSetID, CharSet charSet, Color previousColor) {
		super(source, lastEvent, charSetID, charSet);
		this.previousColor = previousColor;
	}


	public Color getPreviousColor() {
		return previousColor;
	}
}
