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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;



/**
 * Implements shared functionality between implementations of {@link TokenPainter}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public abstract class AbstractTokenPainter implements TokenPainter {
	/**
	 * Method to be implemented by inherited classes performing the actual paint operation. It extends the paint 
	 * method specified in {@link TokenPainter} by an additional parameter providing the string representation of
	 * the token.
	 * 
-	 * @param alignmentArea the alignment area displaying the token to be painted
	 * @param sequenceID the ID of the sequence containing the token to be painted
	 * @param columnIndex the index of the alignment column containing  the token to be painted
	 * @param token the token to be painted
	 * @param tokenRepresentation the string representation of {@code token} determined from the token set of 
	 *        {@code alignmentModel} 
	 * @param g the graphics context to paint to
	 * @param paintArea the rectangle to be filled with the representation of the token
	 * @param selectionColor this color must be mixed by half with the painted output if it is not {@code null}
	 */
	protected abstract void doPaintToken(AlignmentArea alignmentModel, int sequenceID,	int columnIndex, Object token, 
			String tokenRepresentation,	Graphics2D g, Rectangle2D paintArea, Color selectionColor);
	
	
	/**
	 * Default implementation that delegates to 
	 * {@link #doPaintToken(AlignmentModel, Object, String, Graphics2D, Rectangle2D)} and determines 
	 * the string representation of the specified token from the token set of the specified alignment 
	 * model.
	 * 
-	 * @param alignmentArea the alignment area displaying the token to be painted
	 * @param sequenceID the ID of the sequence containing the token to be painted
	 * @param columnIndex the index of the alignment column containing  the token to be painted
	 * @param g the graphics context to paint to
	 * @param paintArea the rectangle to be filled with the representation of the token
	 * @param selectionColor this color must be mixed by half with the painted output if it is not {@code null}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void paintToken(AlignmentArea alignmentArea, int sequenceID,	int columnIndex, Graphics2D g, 
			Rectangle2D paintArea, Color selectionColor) {
		
		AlignmentModel alignmentModel = (AlignmentModel)alignmentArea.getAlignmentModel();
		Object token = alignmentModel.getTokenAt(sequenceID, columnIndex);
		doPaintToken(alignmentArea, sequenceID, columnIndex, token, alignmentModel.getTokenSet().representationByToken(token), 
				g, paintArea, selectionColor);
	}


	/**
	 * Default implementation that returns {@code null}. Inherited classes providing colors should overwrite this method.
	 * 
	 * @param tokenRepresentation the string representation of the token
	 * @return always {@code null} 
	 * @see info.bioinfweb.libralign.alignmentarea.tokenpainter.TokenPainter#getColor(java.lang.String)
	 */
	@Override
	public Color getColor(String tokenRepresentation) {
		return null;
	}
}
