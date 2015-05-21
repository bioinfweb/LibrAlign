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


import java.awt.Dimension;
import java.awt.RenderingHints;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.model.PherogramModel;



/**
 * GUI component that allows displaying a pherogram (trace file from Sanger sequencing) which is not attached
 * to a sequence in an {@link AlignmentArea}.
 * <p>
 * Concrete GUI components for Swing or SWT can be created using {@link #createSwingComponent()} or 
 * {@link #createSWTWidget(org.eclipse.swt.widgets.Composite, int)} as this class is a TIC component.
 * 
 * @author Ben St&ouml;ver
 * @see PherogramView
 * @see PherogramArea
 */
public class PherogramTraceCurveView extends TICComponent implements PherogramComponent {
	private PherogramModel pherogram;
	private int leftCutPosition = 0;
	private int rightCutPosition = 0;
	private double horizontalScale = 1.0;
	private double verticalScale = 100.0;
	private PherogramFormats formats = new PherogramFormats();
	private PherogramPainter painter = new PherogramPainter(this);
	private PherogramHeadingView headingView = null;
	
	
	public PherogramTraceCurveView() {
		super();
  }


	@Override
	public PherogramModel getProvider() {
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
	
	public void setProvider(PherogramModel pherogram) {
		this.pherogram = pherogram;
		leftCutPosition = 0;
		rightCutPosition = pherogram.getSequenceLength();
		updateUI();
	}


	@Override
	public int getLeftCutPosition() {
		return leftCutPosition;
	}


	@Override
	public void setLeftCutPosition(int baseCallIndex) {
		leftCutPosition = baseCallIndex;
		repaint();
	}


	@Override
	public int getRightCutPosition() {
		return rightCutPosition;
	}


	@Override
	public void setRightCutPosition(int baseCallIndex) {
		rightCutPosition = baseCallIndex;
		repaint();
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
	public PherogramFormats getFormats() {
		return formats;
	}


	@Override
	public void setFormats(PherogramFormats layout) {
		this.formats = layout;
		updateUI();
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
		if (getProvider() != null) {
			return (int)Math2.roundUp(getProvider().getTraceLength() * getHorizontalScale());
		}
		else {
			return 0;
		}
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
		e.getGraphics().setColor(getFormats().getBackgroundColor());
		e.getGraphics().fillRect(e.getRectangle().x, e.getRectangle().y, e.getRectangle().width, e.getRectangle().height);

		SimpleSequenceInterval paintRange = calculatePaintRange(e);
		if (getFormats().isShowBaseCallLines()) {
			e.getGraphics().setColor(getFormats().getBaseCallLineColor());
			getPainter().paintUnscaledBaseCallLines(paintRange.getFirstPos(), paintRange.getLastPos(), 
					e.getGraphics(), e.getRectangle().x, 0, getSize().getHeight(), getHorizontalScale());
		}
		getPainter().paintUnscaledTraceCurves(paintRange.getFirstPos(), paintRange.getLastPos(), 
				e.getGraphics(), e.getRectangle().x, 0, getHorizontalScale());		
	}
}
