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


import info.bioinfweb.tic.SwingComponentFactory;
import info.bioinfweb.tic.TargetToolkit;
import info.bioinfweb.tic.toolkit.SwingComponentTools;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;



/**
 * The Swing implementation for {@link PherogramView}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swing
 */
public class SwingPhergramView extends JScrollPane implements ToolkitIndependentPhergramView {
	private PherogramView independentComponent;
	
	
	public SwingPhergramView(PherogramView independentComponent) {
		this.independentComponent = independentComponent;
		init();
	}

	
	private void init() {
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		setBorder(null);

		SwingComponentFactory factory = SwingComponentFactory.getInstance();
		setViewportView(factory.getSwingComponent(getIndependentComponent().getTraceCurveView()));
		setColumnHeaderView(factory.getSwingComponent(getIndependentComponent().getHeadingView()));
	}
	

	@Override
	public PherogramView getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public Dimension getToolkitSize() {
		return getSize();
	}


	@Override
	public Dimension getMaximumSize() {
		return SwingComponentTools.getMaximumSize(this, super.getMaximumSize());
	}


	@Override
	public void assignSize() {
		SwingComponentTools.assignSize(this);
	}


	@Override
	public TargetToolkit getTargetToolkit() {
		return TargetToolkit.SWING;
	}


	@Override
	public Point getLocationInParent() {
		return getLocation();
	}

	
	@Override
	public Dimension getPreferredSize() {
		Dimension traceCurveSize = getIndependentComponent().getTraceCurveView().getSize(); 
		return new Dimension(traceCurveSize.width, traceCurveSize.height + getIndependentComponent().getHeadingView().getSize().height);
		//TODO Add border width and height
	}
}
