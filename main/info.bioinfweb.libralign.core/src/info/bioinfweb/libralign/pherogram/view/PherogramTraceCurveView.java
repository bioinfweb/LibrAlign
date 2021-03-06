/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.pherogram.view;


import java.awt.Dimension;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.pherogram.PherogramComponent;
import info.bioinfweb.libralign.pherogram.PherogramFormats;
import info.bioinfweb.libralign.pherogram.PherogramPainter;
import info.bioinfweb.libralign.pherogram.model.PherogramComponentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelListener;
import info.bioinfweb.libralign.pherogram.model.PherogramCutPositionChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramFirstSeqPosChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramProviderChangeEvent;
import info.bioinfweb.libralign.pherogram.model.PherogramShiftChangeUpdateEvent;



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
	private PherogramComponentModel model;
	private double horizontalScale = 1.0;
	private double verticalScale = 100.0;
	private PherogramFormats formats;
	private PherogramPainter painter = new PherogramPainter(this);
	private PherogramHeadingView headingView = null;
	
	private final PherogramModelListener MODEL_LISTENER = new PherogramModelListener() {
		@Override
		public void pherogramProviderChange(PherogramProviderChangeEvent event) {
			updateUI();
		}

		@Override
		public void leftCutPositionChange(PherogramCutPositionChangeEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				repaintAll();
			}
		}

		@Override
		public void rightCutPositionChange(PherogramCutPositionChangeEvent event) {
			if (!event.isMoreEventsUpcoming()) {
				repaintAll();
			}
		}

		@Override
		public void firstSequencePositionChange(PherogramFirstSeqPosChangeEvent event) {}  // nothing to do

		@Override
		public void shiftChangeEdited(PherogramShiftChangeUpdateEvent event) {}  // nothing to do
	}; 
	
	
	private final PropertyChangeListener FORMATS_LISTENER = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (getHeadingView() != null) {
				getHeadingView().assignSize();
				getHeadingView().repaint();  // Necessary for SWT if there was no size change.
			}
		}
	}; 
	
	
	public PherogramTraceCurveView() {
		super();
		formats = new PherogramFormats();
		formats.addPropertyChangeListener(FORMATS_LISTENER);
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
	
	
	@Override
	public PherogramComponentModel getModel() {
		return model;
	}


	public void setModel(PherogramComponentModel model) {
		if (model != this.model) {
			if (model == null) {
				throw new NullPointerException("Specifying a null model is not allowed.");
			}
			else {
				if (this.model != null) {
					this.model.removeModelListener(MODEL_LISTENER);
				}
				this.model = model;
				updateUI();
				this.model.addModelListener(MODEL_LISTENER);
			}
		}
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
		if (getModel() != null) {
			return (int)Math2.roundUp(getModel().getPherogramProvider().getTraceLength() * getHorizontalScale());
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
		    (int)Math2.roundUp((e.getRectangle().getMinX() + e.getRectangle().getWidth()) / getHorizontalScale()));
	}


	@Override
	public void paint(TICPaintEvent e) {
		e.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel() != null) {
			getPainter().paintUnscaledBackground(e.getGraphics(), e.getRectangle(), getHorizontalScale());
			
			SimpleSequenceInterval paintRange = calculatePaintRange(e);
			if (getFormats().isShowBaseCallLines()) {
				e.getGraphics().setColor(getFormats().getBaseCallLineColor());
				getPainter().paintUnscaledBaseCallLines(paintRange.getFirstPos(), paintRange.getLastPos(), 
						e.getGraphics(), e.getRectangle().getMinX(), 0, getSize().getHeight(), getHorizontalScale());
			}
			getPainter().paintUnscaledTraceCurves(paintRange.getFirstPos(), paintRange.getLastPos(), 
					e.getGraphics(), e.getRectangle().getMinX(), 0, getHorizontalScale());
		}
	}
}
