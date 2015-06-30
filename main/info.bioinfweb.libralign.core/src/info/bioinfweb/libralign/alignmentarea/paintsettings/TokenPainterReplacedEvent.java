/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.paintsettings;

import info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter;



/**
 * Event object indicating that a token painter in an instance of {@link TokenPainterList} has been replaced.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class TokenPainterReplacedEvent extends PaintSettingsEvent {
	private TokenPainter previousPainter;
	private TokenPainter newPainter;
	private int index;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source the list object where the change took place
	 * @param previousPainter the painter that has been replaced
	 * @param newPainter the painter that replaces the previous one
	 * @param index the index where the painter has been replaced
	 */
	public TokenPainterReplacedEvent(PaintSettings source,	TokenPainter previousPainter, TokenPainter newPainter, int index) {
		super(source);
		this.previousPainter = previousPainter;
		this.newPainter = newPainter;
		this.index = index;
	}


	/**
	 * Returns the painter that has been replaced.
	 * 
	 * @return the replaced painter or {@code null} if a {@code null} entry in the list has been replaced
	 */
	public TokenPainter getPreviousPainter() {
		return previousPainter;
	}


	/**
	 * Returns the painter that replaced the previous one
	 * 
	 * @return the new painter or {@code null} if the according entry in the list has been set to {@code null}
	 */
	public TokenPainter getNewPainter() {
		return newPainter;
	}


	/**
	 * Returns the index in the token painter list where the specified replace happened. 
	 * 
	 * @return the list index where the painter was replaced 
	 */
	public int getIndex() {
		return index;
	}
}
