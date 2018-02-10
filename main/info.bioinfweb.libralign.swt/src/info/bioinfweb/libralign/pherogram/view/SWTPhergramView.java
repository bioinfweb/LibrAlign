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

import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.AbstractSWTComposite;
import info.bioinfweb.libralign.alignmentarea.SWTScrollableResizeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;



/**
 * The SWT implementation for {@link PherogramView}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
public class SWTPhergramView extends AbstractSWTComposite implements ToolkitIndependentPhergramView {
	private Composite headingContainer;
	private ScrolledComposite headingScroller;
	private SWTScrollableResizeListener headingResizeListener;  //TODO Move this type (maybe even to commons)?
	private Composite traceCurveContainer;
	private ScrolledComposite traceCurveScroller;
	private SWTScrollableResizeListener traceCurveResizeListener;

	
	public SWTPhergramView(PherogramView ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style);
		init();
	}
	
	
	private GridData createGridData(boolean grabExcessVerticalSpace) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;
		return gridData;
	}
	
	
	private void init() {
		// Set layout:
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		
		// Label components:
		headingContainer = new Composite(this, SWT.NONE);
		headingContainer.setLayoutData(createGridData(false));
		headingScroller = new ScrolledComposite(headingContainer,	SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		headingScroller.setAlwaysShowScrollBars(true);
		headingScroller.setContent(factory.getSWTComponent(
				getIndependentComponent().getHeadingView(), headingScroller, SWT.NONE));
		headingResizeListener = new SWTScrollableResizeListener(headingContainer, headingScroller, false, true);
		headingContainer.addControlListener(headingResizeListener);  // Must not be called before both fields are initialized.
		
		// Alignment area part components:
		traceCurveContainer = new Composite(this, SWT.NONE);
		traceCurveContainer.setLayoutData(createGridData(true));
		traceCurveScroller = new ScrolledComposite(traceCurveContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		traceCurveScroller.setAlwaysShowScrollBars(true);
		traceCurveScroller.setContent(factory.getSWTComponent(
				getIndependentComponent().getTraceCurveView(), traceCurveScroller, SWT.NONE));
		traceCurveResizeListener = new SWTScrollableResizeListener(traceCurveContainer, traceCurveScroller, false, false); 
		traceCurveContainer.addControlListener(traceCurveResizeListener);  // Must not be called before both fields are initialized.
		
		// Synchronize horizontal scrolling:
		ScrolledCompositeSyncListener horizontalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{headingScroller, traceCurveScroller}, true);
		horizontalSyncListener.registerToAll();		

		// Ensure correct height of heading components:
		((Composite)getIndependentComponent().getHeadingView().getToolkitComponent()).addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						Dimension size = getIndependentComponent().getHeadingView().getSize();
						int borderWidth = 2 * (headingContainer.getBorderWidth() + headingScroller.getBorderWidth());
						GridData data = (GridData)headingContainer.getLayoutData();
						data.heightHint = size.height + borderWidth;
						headingContainer.setSize(data.widthHint, data.heightHint);
						layout();  //TODO Can this become an endless recursion? => Should only happen if no data for the layout manager is set in here. Otherwise the recursion should terminate once the component has its desired size. 
					}
				});

		// Set correct size to recently created SWT components:
		getIndependentComponent().getTraceCurveView().assignSize();
		getIndependentComponent().getHeadingView().assignSize();
		
		//TODO add further listeners?
	}

	
	@Override
	public PherogramView getIndependentComponent() {
		return (PherogramView)super.getIndependentComponent();
	}
}
