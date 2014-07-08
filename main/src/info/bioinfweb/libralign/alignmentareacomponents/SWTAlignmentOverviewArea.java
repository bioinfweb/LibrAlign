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
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;



/**
 * The SWT component rendering all parts of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTAlignmentOverviewArea extends Composite implements ToolkitSpecificAlignmentOverviewArea {
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	private SWTAlignmentPartArea headArea;
	private SWTAlignmentPartArea contentArea;
	private SWTAlignmentPartArea bottomArea;
	private ScrolledComposite headScrolledComposite;
	private ScrolledComposite contentScrolledComposite;
	private ScrolledComposite bottomScrolledComposite;
	private SashForm sashForm;
	private Composite headContainer;
	private Composite contentContainer;
	private Composite bottomContainer;
	

	public SWTAlignmentOverviewArea(Composite parent, int style, AlignmentArea independentComponent) {
		super(parent, style);
		this.independentComponent = independentComponent;		
		init();
	}
	
	
	private Composite createContainer() {
		Composite result = new Composite(sashForm, SWT.NONE);
		result.setLayout(new FillLayout(SWT.HORIZONTAL));
		return result;
	}
	
	
	private void createLabelElements(Composite parent, DataAreaListType position) {
		Composite labelScrollContainer = new Composite(parent, SWT.NONE);
		ScrolledComposite labelScrolledComposite = new ScrolledComposite(labelScrollContainer, 
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		AlignmentLabelArea labelArea = new AlignmentLabelArea(getIndependentComponent(), position);
		labelScrolledComposite.setContent(labelArea.createSWTWidget(labelScrolledComposite, SWT.NONE));
		labelScrollContainer.addControlListener(  // Must not be called before both field are initialized.
				new SWTScrolledCompositeResizeListener(labelScrollContainer, labelScrolledComposite, true));
	}
	
	
	private void createHeadComponents() {
		headContainer = createContainer();
		createLabelElements(headContainer, DataAreaListType.TOP);

		Composite headScrollContainer = new Composite(headContainer, SWT.NONE);
		headScrolledComposite = new ScrolledComposite(headScrollContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		headArea = new SWTAlignmentPartArea(headScrolledComposite, SWT.NONE);
		headScrolledComposite.setContent(headArea);
		headScrolledComposite.setMinSize(headArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		headScrollContainer.addControlListener(  // Must not be called before both field are initialized.
				new SWTScrolledCompositeResizeListener(headScrollContainer, headScrolledComposite, false));
	}
	
	
	private void createContentComponents() {
		contentContainer = createContainer();
		createLabelElements(contentContainer, DataAreaListType.SEQUENCE);
		
		Composite contentScrollContainer = new Composite(contentContainer, SWT.NONE);
		contentScrolledComposite = new ScrolledComposite(contentScrollContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		contentArea = new SWTAlignmentPartArea(contentScrolledComposite, SWT.NONE);
		contentScrolledComposite.setContent(contentArea);
		contentScrolledComposite.setMinSize(contentArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		contentScrollContainer.addControlListener(  // Must not be called before both field are initialized.
				new SWTScrolledCompositeResizeListener(contentScrollContainer, contentScrolledComposite, false));		
	}
	
	
	private void createBottomComponents() {
		bottomContainer = createContainer();
		createLabelElements(bottomContainer, DataAreaListType.BOTTOM);
		
		bottomScrolledComposite = new ScrolledComposite(bottomContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		bottomArea = new SWTAlignmentPartArea(bottomScrolledComposite, SWT.NONE);
		bottomScrolledComposite.setContent(bottomArea);
		bottomScrolledComposite.setMinSize(bottomArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sashForm.setWeights(new int[] {1, 1, 1});  //TODO Adjust
		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
	}
	
	
	private void init() {
		super.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setSashWidth(AlignmentArea.DIVIDER_WIDTH);
		createHeadComponents();
		createContentComponents();
		createBottomComponents();
		reinsertSubelements();
		
		ScrolledCompositeSyncListener horizontalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{headScrolledComposite, contentScrolledComposite, bottomScrolledComposite}, true);
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
				return getHeadArea();
			case SEQUENCE:
				return getContentArea();
			default:
				return getBottomArea();
		}
	}


	@Override
	public void reinsertSubelements() {
		// Head elements:
		getHeadArea().removeAll();
		getHeadArea().addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		getHeadArea().assignSize();
		
		// Content elements:
		getContentArea().removeAll();
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			sequenceAreaMap.get(id).createSWTWidget(getContentArea(), SWT.NONE);
			sequenceAreaMap.get(id).assignSize();
			getContentArea().addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}
		getContentArea().assignSize();

		// Bottom elements:
		getBottomArea().removeAll();
		getBottomArea().addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
		getBottomArea().assignSize();

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
	
	
	public SWTAlignmentPartArea getHeadArea() {
		return headArea;
	}
	
	
	public SWTAlignmentPartArea getContentArea() {
		return contentArea;
	}
	
	
	public SWTAlignmentPartArea getBottomArea() {
		return bottomArea;
	}
	
	
	public ScrolledComposite getHeadScrolledComposite() {
		return headScrolledComposite;
	}
	
	
	public ScrolledComposite getContentScrolledComposite() {
		return contentScrolledComposite;
	}
	
	
	public ScrolledComposite getBottomScrolledComposite() {
		return bottomScrolledComposite;
	}
	
	
	public SashForm getSashForm() {
		return sashForm;
	}

	public Composite getBottomContainer() {
		return bottomContainer;
	}
}
