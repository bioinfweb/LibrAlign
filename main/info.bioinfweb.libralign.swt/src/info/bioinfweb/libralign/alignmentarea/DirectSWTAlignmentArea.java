/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.alignmentarea;


import java.awt.Rectangle;

import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.toolkit.AbstractSWTComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;



public class DirectSWTAlignmentArea extends AbstractSWTComposite implements ToolkitSpecificAlignmentArea {
	public DirectSWTAlignmentArea(AlignmentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style);
		init();
	}
	
	
	private void init() {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		SWTComponentFactory factory = SWTComponentFactory.getInstance();
		factory.getSWTComponent(getIndependentComponent().getLabelArea(), sashForm, SWT.NONE);  //TODO Does constructing these components in the "wrong" order also make a difference here, when subcomponents are not used?
		factory.getSWTComponent(getIndependentComponent().getContentArea(), sashForm, SWT.NO_BACKGROUND);
		sashForm.setWeights(new int[] {1, 10});  //TODO Specify calculated values here?
	}

	
	@Override
	public AlignmentArea getIndependentComponent() {
		return (AlignmentArea)super.getIndependentComponent();
	}


	@Override
	public void scrollAlignmentRectToVisible(Rectangle rectangle) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Rectangle getVisibleAlignmentRect() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setHideHorizontalScrollBar(boolean hideHorizontalScrollBar) {
		// TODO Auto-generated method stub
		
	}
}
