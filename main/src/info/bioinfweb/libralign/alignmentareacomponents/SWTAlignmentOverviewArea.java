/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;



/**
 * The SWT component rendering all parts of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTAlignmentOverviewArea extends Composite implements ToolkitSpecificAlignmentOverviewArea {
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	private SashForm sashForm;
	private SWTAlignmentPartScroller headComponent;
	private SWTAlignmentPartScroller contentComponent;
	private SWTAlignmentPartScroller bottomComponent;
	

	public SWTAlignmentOverviewArea(Composite parent, int style, AlignmentArea independentComponent) {
		super(parent, style);
		this.independentComponent = independentComponent;		
		init();
	}
	
	
	private void init() {
		super.setLayout(new FillLayout(SWT.HORIZONTAL));

		// Create components:
		sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setSashWidth(AlignmentArea.DIVIDER_WIDTH);
		headComponent = new SWTAlignmentPartScroller(getIndependentComponent(), sashForm, DataAreaListType.TOP, true);
		contentComponent = new SWTAlignmentPartScroller(getIndependentComponent(), sashForm, DataAreaListType.SEQUENCE, true);
		bottomComponent = new SWTAlignmentPartScroller(getIndependentComponent(), sashForm, DataAreaListType.BOTTOM, false);
		sashForm.setWeights(new int[] {1, 1, 1});  //TODO Adjust
		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
		reinsertSubelements();
		
		// Resize label areas according to alignment part areas:
		headComponent.getLabelArea().assignSize();
		contentComponent.getLabelArea().assignSize();
		bottomComponent.getLabelArea().assignSize();
		
		// Synchronize horizontal scrolling:
		ScrolledCompositeSyncListener horizontalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{headComponent.getPartScroller(), contentComponent.getPartScroller(), 
						bottomComponent.getPartScroller()}, true);
		horizontalSyncListener.registerToAll();
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public void repaint() {
		redraw();
	}

	
	@Override
	public ToolkitSpecificAlignmentPartArea getPartArea(DataAreaListType position) {
		switch (position) {
			case TOP:
				return headComponent.getPartArea();
			case SEQUENCE:
				return contentComponent.getPartArea();
			default:
				return bottomComponent.getPartArea();
		}
	}


	@Override
	public void reinsertSubelements() {
		// Head elements:
		headComponent.getPartArea().removeAll();
		headComponent.getPartArea().addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		headComponent.getPartArea().assignSize();
		
		// Content elements:
		contentComponent.getPartArea().removeAll();
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			sequenceAreaMap.get(id).createSWTWidget(contentComponent.getPartArea(), SWT.NONE);
			sequenceAreaMap.get(id).assignSize();
			contentComponent.getPartArea().addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}
		contentComponent.getPartArea().assignSize();

		// Bottom elements:
		bottomComponent.getPartArea().removeAll();
		bottomComponent.getPartArea().addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
		bottomComponent.getPartArea().assignSize();

    //assignSize();
		//layout(true, true);  //TODO Necessary?
	}


	//TODO Overwriting this methods is probably not necessary.
//	@Override
//	public Point computeSize(int wHint, int hHint, boolean changed) {
//		// TODO Auto-generated method stub
//		return super.computeSize(arg0, arg1, arg2);
//	}
//
//
//	@Override
//	public void layout(boolean arg0) {
//		// TODO Auto-generated method stub
//		super.layout(arg0);
//	}


	/**
	 * Does nothing since this component only uses its own layout. 
	 */
	@Override
	public void setLayout(Layout layout) {}
	
	
	public SashForm getSashForm() {
		return sashForm;
	}
}
