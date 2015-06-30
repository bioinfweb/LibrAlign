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

import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;



/**
 * GUI component that displays a scrollable pherogram with undistorted trace curves, its base call sequence and 
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
		// Just returns the size already set to the toolkit component because the size of an alignment area is determined just by the layout manager and not dependent of the alignment and data area size.
		if (hasToolkitComponent()) {
			return getToolkitComponent().getToolkitSize();
		}
		else {
			return new Dimension(0, 0);
		}
	}
	
	
	@Override
	public void paint(TICPaintEvent e) {}


	@Override
	protected String getSwingComponentClassName() {
		return "info.bioinfweb.libralign.pherogram.view.SwingPhergramView";
	}


	@Override
	protected String getSWTComponentClassName() {
		return "info.bioinfweb.libralign.pherogram.view.SWTPhergramView";
	}
}
