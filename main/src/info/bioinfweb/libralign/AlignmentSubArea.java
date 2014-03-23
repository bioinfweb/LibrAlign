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
package info.bioinfweb.libralign;


import info.bioinfweb.commons.tic.TICComponent;



/**
 * All GUI components that are part of an {@link AlignmentArea} should inherit from this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AlignmentSubArea extends TICComponent {
	private AlignmentArea owner = null;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 */
	public AlignmentSubArea(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment area that displays this data area.
	 */
	public AlignmentArea getOwner() {
		return owner;
	}
}
