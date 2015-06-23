/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.pherogram.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;



/**
 * The GUI component that displays the heading information (base call sequence and the nucleotide index) above 
 * the trace curves of a pherogram from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see PherogramView
 */
public class PherogramHeadingView extends TICComponent {
	private PherogramTraceCurveView traceCurveView;
	
	
	public PherogramHeadingView(PherogramTraceCurveView traceCurveView) {
		super();
		this.traceCurveView = traceCurveView;
	}


	public PherogramTraceCurveView getTraceCurveView() {
		return traceCurveView;
	}


	@Override
	public void paint(TICPaintEvent e) {
		PherogramFormats formats = getTraceCurveView().getFormats();
		PherogramPainter painter = getTraceCurveView().getPainter();
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		painter.paintUncaledBackground(e.getGraphics(), e.getRectangle(), getTraceCurveView().getHorizontalScale());
		SimpleSequenceInterval paintRange = getTraceCurveView().calculatePaintRange(e);
		
		e.getGraphics().setFont(formats.getIndexFont());
		e.getGraphics().setColor(Color.BLACK);
		painter.paintUnscaledBaseCallIndices(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, 0, getTraceCurveView().getHorizontalScale());

		e.getGraphics().setFont(formats.getBaseCallFont());
		painter.paintUnscaledBaseCalls(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, formats.getIndexFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR, 
				getTraceCurveView().getHorizontalScale());

	}

	
	@Override
	public Dimension getSize() {
		PherogramFormats formats = getTraceCurveView().getFormats(); 
		double height = ((formats.getBaseCallFont().getSize() + formats.getIndexFont().getSize()) * PherogramFormats.FONT_HEIGHT_FACTOR) + 
				formats.qualityOutputHeight();
		if (formats.isShowProbabilityValues()) {
			height += 3 * formats.getAnnotationFont().getSize() * PherogramFormats.FONT_HEIGHT_FACTOR;
		}
		
		return new Dimension(getTraceCurveView().getWidth(), 
				(int)Math2.roundUp(height));
	}
}
