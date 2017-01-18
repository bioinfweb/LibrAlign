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
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;



/**
 * Implements shared functionality for painting nucleotide and amino acid tokens including
 * their ambiguity codes.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public abstract class AbstractBioPolymerTokenPainter extends SingleColorTokenPainter {
	/**
	 * To be implemented by inherited classes to determine whether the specified token representation 
	 * is an ambiguity code.
	 * 
	 * @param tokenRepresentation the token representation
	 * @return {@code true} if the token is ambiguous and shall have a background with multiple colors, 
	 *         {@code false} otherwise
	 */
	protected abstract boolean isAmbiguity(String tokenRepresentation);
	
	
	/**
	 * To be implemented by inherited classes to determine the non-ambiguous tokens that could be represented
	 * by the specified ambiguity code.  
	 * 
	 * @param tokenRepresentation the ambiguous token representation
	 * @return an array with according token representations
	 */
	protected abstract char[] calculateConstituents(String tokenRepresentation);
	
	
	@Override
	protected void doPaintToken(AlignmentArea alignmentArea, String sequenceID,	int columnIndex, Object token, 
			String tokenRepresentation,	Graphics2D g, Rectangle2D paintArea, Color selectionColor) {
		
		if (isAmbiguity(tokenRepresentation.toUpperCase())) {
		  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used:
			char[] constituents = calculateConstituents(tokenRepresentation.toUpperCase());
	  	final double height = paintArea.getHeight() / (double)constituents.length;
	  	double bgY = paintArea.getY();
	  	for (int i = 0; i < constituents.length; i++) {
	  		g.setColor(backgroundColorByRepresentation(Character.toString(constituents[i]), selectionColor));
	    	g.fill(new Rectangle2D.Double(paintArea.getX(), bgY, paintArea.getWidth(), height));
	    	bgY += height;
			}
			
			paintText(g, paintArea, tokenRepresentation, selectionColor);
		}
		else {
			super.doPaintToken(alignmentArea, sequenceID, columnIndex, token, tokenRepresentation, g, paintArea, selectionColor);
		}
	}
}
