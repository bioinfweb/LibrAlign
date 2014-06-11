/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
import java.awt.RenderingHints;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.graphics.GraphicsUtils;
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
public class PherogramTraceCurveView extends TICComponent implements PherogramComponent {
	public static final float BASE_CALL_LINE_COLOR_FACTOR = 0.1f;

	
	private PherogramProvider pherogram;
	private double horizontalScale = 1.0;
	private double verticalScale = 100.0;
	private SequenceColorSchema colorSchema = new SequenceColorSchema();
	private Color backgroundColor;
	private Color baseCallLineColor;
	private boolean showBaseCallLines = true;
	private PherogramPainter painter = new PherogramPainter(this);
	private PherogramHeadingView headingView = null;
	
	
	public PherogramTraceCurveView() {
		super();
		
		backgroundColor = getColorSchema().getDefaultBgColor();
		float factor;
		if (GraphicsUtils.rgbToGrayValue(getColorSchema().getDefaultBgColor()) > 127) {
			factor = 1f - BASE_CALL_LINE_COLOR_FACTOR;
		}
		else {
			factor = 1f + BASE_CALL_LINE_COLOR_FACTOR;
		}
		baseCallLineColor = GraphicsUtils.multiplyColorChannels(backgroundColor, factor);
}


	@Override
	public PherogramProvider getProvider() {
		return pherogram;
	}

	
	/**
	 * Resizes and repaints this component and the associated heading component.
	 */
	protected void updateUI() {
		assignSize();
		if (getHeadingView() != null) {
			getHeadingView().assignSize();
		}
		repaintAll();
	}
	
	
	/**
	 * Repaints this component and the associated heading component.
	 */
	protected void repaintAll() {
		repaint();
		if (getHeadingView() != null) {
			getHeadingView().repaint();
		}
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
		repaintAll();
	}

	
	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		repaint();  // Repainting the heading component is not necessary here because it stores its own background color.
	}


	public Color getBaseCallLineColor() {
		return baseCallLineColor;
	}


	public void setBaseCallLineColor(Color baseCallLineColor) {
		this.baseCallLineColor = baseCallLineColor;
		repaint();  // Repainting the heading component is not necessary here because it has no base call lines.
	}


	public boolean isShowBaseCallLines() {
		return showBaseCallLines;
	}


	public void setShowBaseCallLines(boolean showBaseCallLines) {
		this.showBaseCallLines = showBaseCallLines;
		repaint();
	}


	public PherogramPainter getPainter() {
		return painter;
	}


	public PherogramHeadingView getHeadingView() {
		return headingView;
	}


	/**
	 * Use this method to specify the heading component used together with this component. Only if the heading
	 * component is registered here it will automatically be updated if propertied of this class change.
	 * 
	 * @param headingView - the heading component used together with this instance
	 */
	public void setHeadingView(PherogramHeadingView headingView) {
		this.headingView = headingView;
	}


	public int getWidth() {
		return (int)Math2.roundUp(getProvider().getTraceLength() * getHorizontalScale());
	}
	

	@Override
	public Dimension getSize() {
		return new Dimension(getWidth(), (int)Math2.roundUp(getPainter().calculateTraceCurvesHeight()));
	}
	
	
	protected SimpleSequenceInterval calculatePaintRange(TICPaintEvent e) {
		return new SimpleSequenceInterval(
		    (int)(e.getRectangle().getX() / getHorizontalScale()),
		    (int)Math2.roundUp((e.getRectangle().x + e.getRectangle().width) / getHorizontalScale()));
	}


	@Override
	public void paint(TICPaintEvent e) {
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		e.getGraphics().setColor(getBackgroundColor());
		e.getGraphics().fillRect(e.getRectangle().x, e.getRectangle().y, e.getRectangle().width, e.getRectangle().height);

		SimpleSequenceInterval paintRange = calculatePaintRange(e);
		if (isShowBaseCallLines()) {
			e.getGraphics().setColor(getBaseCallLineColor());
			
			getPainter().paintBaseCallLines(paintRange.getFirstPos(), paintRange.getLastPos(), 
					e.getGraphics(), e.getRectangle().x, 0, getSize().getHeight(), getHorizontalScale());
		}
		getPainter().paintUnscaledTraceCurves(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, 0, getHorizontalScale());		
	}
}
