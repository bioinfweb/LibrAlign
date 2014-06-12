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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;



/**
 * The GUI component that displays the heading information (base call sequence and the nucleotide index) above 
 * the trace curves of a pherogram from Sanger sequencing.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class PherogramHeadingView extends TICComponent {
	public static final double FONT_HEIGHT_FACTOR = 1.2;
	
	
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
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		e.getGraphics().setColor(getTraceCurveView().getFormats().getBackgroundColor());
		e.getGraphics().fillRect(e.getRectangle().x, e.getRectangle().y, e.getRectangle().width, e.getRectangle().height);
		SimpleSequenceInterval paintRange = getTraceCurveView().calculatePaintRange(e);
		
		e.getGraphics().setFont(getTraceCurveView().getFormats().getIndexFont());
		e.getGraphics().setColor(Color.BLACK);
		getTraceCurveView().getPainter().paintBaseCallIndices(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, 0, getTraceCurveView().getHorizontalScale());

		e.getGraphics().setFont(getTraceCurveView().getFormats().getBaseCallFont());
		getTraceCurveView().getPainter().paintUnscaledBaseCalls(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, getTraceCurveView().getFormats().getIndexFont().getSize() * FONT_HEIGHT_FACTOR, 
				getTraceCurveView().getHorizontalScale());
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(getTraceCurveView().getWidth(), 
				(int)Math2.roundUp((getTraceCurveView().getFormats().getBaseCallFont().getSize() + 
						getTraceCurveView().getFormats().getIndexFont().getSize()) * FONT_HEIGHT_FACTOR));
	}
}
