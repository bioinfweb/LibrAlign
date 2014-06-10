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


import java.awt.Dimension;
import java.awt.RenderingHints;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.SequenceColorSchema;
import info.bioinfweb.libralign.dataarea.PherogramArea;



/**
 * GUI component that allows displaying a pherogram (trace file from Sanger sequencing) which is not attached
 * to a sequence in an {@link AlignmentArea}.
 * <p>
 * Concrete GUI components for Swing or SWT can be created using {@link #createSwingComponent()} or 
 * {@link #createSWTWidget(org.eclipse.swt.widgets.Composite, int)} as this class is a TIC component.
 * 
 * @author Ben St&ouml;ver
 * @see PherogramArea
 */
public class PherogramView extends TICComponent implements PherogramComponent {
	private PherogramProvider pherogram; 
	private double horizontalScale = 1.0;
	private double verticalScale = 100.0;
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private PherogramPainter painter = new PherogramPainter(this);
	
	
	@Override
	public PherogramProvider getProvider() {
		return pherogram;
	}

	
	private void updateUI() {
		assignSize();
		repaint();
	}
	
	
	public void setProvider(PherogramProvider pherogram) {
		this.pherogram = pherogram;
		updateUI();
	}


	@Override
	public double getVerticalScale() {
		return verticalScale;
	}

	
	@Override
	public void setVerticalScale(double value) {
		verticalScale = value;
		updateUI();
	}


	public double getHorizontalScale() {
		return horizontalScale;
	}


	public void setHorizontalScale(double horizontalScale) {
		this.horizontalScale = horizontalScale;
		updateUI();
	}


	@Override
	public SequenceColorSchema getColorSchema() {
		return colorSchema;
	}


	public void setColorSchema(SequenceColorSchema colorSchema) {
		this.colorSchema = colorSchema;
	}


	@Override
	public Dimension getSize() {
		return new Dimension((int)Math2.roundUp(getProvider().getTraceLength() * getHorizontalScale()), 
				(int)Math2.roundUp(painter.calculateBaseCallHeight(getHorizontalScale()) + painter.calculateTraceCurvesHeight()));
	}


	@Override
	public void paint(TICPaintEvent e) {
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int startX = (int)(e.getRectangle().getX() / getHorizontalScale());
		int endX = (int)Math2.roundUp((e.getRectangle().x + e.getRectangle().width) / getHorizontalScale());
		painter.paintUnscaledBaseCalls(startX, endX, e.getGraphics(), e.getRectangle().x, 0, getHorizontalScale());
		painter.paintUnscaledTraceCurves(startX, endX, e.getGraphics(), 
				e.getRectangle().x, painter.calculateBaseCallHeight(getHorizontalScale()), getHorizontalScale());
	}
}
