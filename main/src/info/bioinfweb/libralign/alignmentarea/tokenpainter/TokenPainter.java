/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;



/**
 * Interface to be implemented by all classes responsible for painting tokens from an {@link AlignmentModel}
 * in an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public interface TokenPainter {
	/**
	 * Paints a representation of the specified token filling up the specified area.
	 * <p>
	 * Tokens are identified by the alignment area they are contained, the sequence ID of their sequence and
	 * their column index instead of a direct reference, to allow implementing classes to consider information
	 * e.g. on neighboring tokens.
	 * 
-	 * @param alignmentArea the alignment area displaying the token to be painted
	 * @param sequenceID the ID of the sequence containing the token to be painted
	 * @param columnIndex the index of the alignment column containing  the token to be painted
	 * @param g the graphics context to paint to
	 * @param paintArea the rectangle to be filled with the representation of the token
	 * @param selectionColor this color must be mixed by half with the painted output if it is not {@code null}
	 */
	public void paintToken(AlignmentArea alignmentArea, int sequenceID, int columnIndex, Graphics2D g, Rectangle2D paintArea, 
			Color selectionColor);
	
	public double getPreferredWidth();
	
	public double getPreferredHeight();
}
