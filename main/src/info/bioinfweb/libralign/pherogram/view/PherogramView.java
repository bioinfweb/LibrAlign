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
package info.bioinfweb.libralign.pherogram.view;


import java.awt.Dimension;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;



/**
 * GUI component that displays a scrollable pherogram with undistorted trace curves, its base base call sequence and 
 * nucleotide probabilities.
 * <p>
 * This component is the combination of {@link PherogramTraceCurveView} and {@link PherogramHeadingView} in according
 * Swing or SWT scroll containers.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * 
 * @see PherogramArea
 * @see PherogramTraceCurveView
 * @see PherogramHeadingView
 */
public class PherogramView extends TICComponent {
  private PherogramHeadingView headingView;
  private PherogramTraceCurveView traceCurveView;
  
  
	/**
	 * Creates a new instance of this class.
	 */
	public PherogramView() {
		super();

		traceCurveView = new PherogramTraceCurveView();
		headingView = new PherogramHeadingView(traceCurveView);
		traceCurveView.setHeadingView(headingView);
	}


	/**
	 * Returns the contained heading view.
	 * 
	 * @return the heading view component
	 */
	public PherogramHeadingView getHeadingView() {
		return headingView;
	}


	/**
	 * Returns the contained trace curve view.
	 * 
	 * @return the trace curve view component
	 */
	public PherogramTraceCurveView getTraceCurveView() {
		return traceCurveView;
	}


	@Override
	public Dimension getSize() {
		switch (getCurrentToolkit()) {
			case SWING:
				return ((JComponent)getToolkitComponent()).getPreferredSize();  //TODO correct size?
			case SWT:
				Point point = ((Composite)getToolkitComponent()).getSize();
				return new Dimension(point.x, point.y);
			default:
			  return new Dimension(0, 0);
		}
	}
	
	
	@Override
	public void paint(TICPaintEvent e) {}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		return new SWTPhergramView(parent, style, this);
	}


	@Override
	protected JComponent doCreateSwingComponent() {
		return new SwingPhergramView(this);
	}
	
	
	@Override
	public SWTPhergramView createSWTWidget(Composite parent, int style) {
		return (SWTPhergramView)super.createSWTWidget(parent, style);
	}


	@Override
	public SwingPhergramView createSwingComponent() {
		return (SwingPhergramView)super.createSwingComponent();
	}
}
