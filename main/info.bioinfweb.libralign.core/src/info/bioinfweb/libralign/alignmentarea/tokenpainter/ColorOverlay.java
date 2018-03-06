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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.awt.Color;



/**
 * Classes implementing this interface define areas in the alignment that should be overlain with a certain color.
 * 
 * @author Ben St&ouml;ver
 * @since 0.9.0
 */
public interface ColorOverlay {
	/**
	 * Determines the color to overlay the specified cell of the alignment.
	 * 
	 * @param alignmentArea the alignment area containing the cell to be painted
	 * @param sequenceID the ID of the sequence containing the token to be painted
	 * @param columnIndex the column of the cell to be painted
	 * @return the overlay color or {@code null} if no overlay should be done in this cell
	 */
	public Color getColor(AlignmentArea alignmentArea, String sequenceID, int columnIndex);
}
