/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.pherogram;


import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import info.bioinfweb.libralign.dataarea.PherogramArea;



/**
 * Utility class used by GUI components that display a pherogram (e.g. {@link PherogramView} and 
 * {@link PherogramArea}) that implements common painting routines.
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramPainter {
	private PherogramComponent owner; 
	
	
	public PherogramPainter(PherogramComponent owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Paints parts the four trace curves stored in the specified provider with a constant scale on x.
	 * <p>
	 * Note that no background will be painted by this method.
	 * 
	 * @param startX - the index in {@code provider} of the first trace value to be painted
	 * @param endX - the index in {@code provider} after the last value to be painted
	 * @param g - the graphics context to paint on
	 * @param paintX - the left most coordinate of the area where the trace curves will be painted
	 * @param paintY - the top most coordinate of the area where the trace curves will be painted
	 * @param horizontalScale - the factor by which the trace curves shall be scaled (zoomed) on the x-axis
	 */
	public void paintUnscaledTraceCurves(int startX, int endX, Graphics2D g, double paintX, double paintY, 
			double horizontalScale) {
		
		double height = 0.0;
		for (TraceCurve nucleotide: TraceCurve.values()) {
			height = Math.max(height, owner.getProvider().getMaxTraceValue(nucleotide));
		}
		
		for (TraceCurve nucleotide: TraceCurve.values()) {
			Path2D path = new Path2D.Double();
			double x = paintX;
			path.moveTo(x, paintY - height + 
					owner.getProvider().getTraceValue(nucleotide, startX) * owner.getVerticalScale());
			for (int traceX = startX + 1; traceX < endX; traceX++) {
				x += horizontalScale;
				path.lineTo(x, paintY - height + 
						owner.getProvider().getTraceValue(nucleotide, traceX) * owner.getVerticalScale());  //TODO curveTo() could be used alternatively.
			}

			g.setColor(owner.getColorSchema().getNucleotideColorMap().get("" + nucleotide.toString().charAt(0)));
			g.draw(path);
		}
	}
}
