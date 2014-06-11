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
	private Color backgroundColor;
	private Font baseCallFont = new Font(Font.DIALOG, Font.BOLD, 12);
	private Font indexFont = new Font(Font.DIALOG, Font.PLAIN, 8);
	
	
	public PherogramHeadingView(PherogramTraceCurveView traceCurveView) {
		super();
		this.traceCurveView = traceCurveView;
		backgroundColor = traceCurveView.getBackgroundColor();
	}


	public PherogramTraceCurveView getTraceCurveView() {
		return traceCurveView;
	}


	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		repaint();
	}


	public Font getBaseCallFont() {
		return baseCallFont;
	}


	public void setBaseCallFont(Font font) {
		this.baseCallFont = font;
		assignSize();
		repaint();
	}


	public Font getIndexFont() {
		return indexFont;
	}


	public void setIndexFont(Font indexFont) {
		this.indexFont = indexFont;
		assignSize();
		repaint();
	}


	@Override
	public void paint(TICPaintEvent e) {
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		e.getGraphics().setColor(getBackgroundColor());
		e.getGraphics().fillRect(e.getRectangle().x, e.getRectangle().y, e.getRectangle().width, e.getRectangle().height);
		SimpleSequenceInterval paintRange = getTraceCurveView().calculatePaintRange(e);
		
		e.getGraphics().setFont(getIndexFont());
		e.getGraphics().setColor(Color.BLACK);
		getTraceCurveView().getPainter().paintBaseCallIndices(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, 0, getTraceCurveView().getHorizontalScale());

		e.getGraphics().setFont(getBaseCallFont());
		getTraceCurveView().getPainter().paintUnscaledBaseCalls(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, getIndexFont().getSize() * FONT_HEIGHT_FACTOR, 
				getTraceCurveView().getHorizontalScale());
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(getTraceCurveView().getWidth(), 
				(int)Math2.roundUp((getBaseCallFont().getSize() + getIndexFont().getSize()) * FONT_HEIGHT_FACTOR));
	}
}
