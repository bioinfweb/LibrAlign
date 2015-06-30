/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
package info.bioinfweb.libralign.multiplealignments;


import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.SWTAlignmentArea;
import info.bioinfweb.libralign.alignmentarea.ToolkitSpecificAlignmentArea;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;



/**
 * The SWT component rendering all parts of an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTMultipleAlignmentsContainer extends Composite implements ToolkitSpecificMultipleAlignmentsContainer {
	public static final int NEEDED_BORDER_WIDTH = 3;
	
	
	private MultipleAlignmentsContainer independentComponent;
	private SashForm sashForm;
	

	public SWTMultipleAlignmentsContainer(Composite parent, int style, MultipleAlignmentsContainer independentComponent) {
		super(parent, style);
		this.independentComponent = independentComponent;		
		init();
	}
	
	
	private void init() {
		super.setLayout(new FillLayout(SWT.HORIZONTAL));
		addControlListener(new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						getIndependentComponent().redistributeHeight();
					}
				});
		
		adoptChildAreas();
		getIndependentComponent().assignSizeToAll();  // Make sure all components adopt higher widths of other components below. (Previous insertion events did not trigger any updated as long as no toolkit component was present.)

		// Synchronize horizontal scrolling:
		ScrolledCompositeSyncListener horizontalSyncListener = 
				ScrolledCompositeSyncListener.newLinkedInstance(getScrolledCompositeIterable(), true); 
		horizontalSyncListener.registerToAll();
	}
	
	
	@Override
	public void adoptChildAreas() {
		// Create components:
		sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setSashWidth(AlignmentArea.DIVIDER_WIDTH);
		
		// Create areas:
		Iterator<AlignmentArea> iterator = getIndependentComponent().getAlignmentAreas().iterator();
		while (iterator.hasNext()) {
			SWTAlignmentArea area = iterator.next().createSWTWidget(sashForm, SWT.NONE);
			area.setHideHorizontalScrollBar(iterator.hasNext());  // Show scroll bar only in the lowest area.
		}
		
		// Set size of areas (Widths of one area can depend on a lower area which is not known at creation time.):
		for (AlignmentArea area : getIndependentComponent().getAlignmentAreas()) {
			area.getContentArea().assignSize();
			area.getLabelArea().assignSize();
			area.assignSize();  //TODO Create method that calls assignSize() on all child components?
		}
		
		//TODO Distribute height (possible in independentComponent?)
		//sashForm.setWeights(new int[]{1, 2, 1});  //TODO Adjust
	}


	private Iterable<ScrolledComposite> getScrolledCompositeIterable() {
		return new Iterable<ScrolledComposite>() {
					@Override
					public Iterator<ScrolledComposite> iterator() {
						final Iterator<AlignmentArea> iterator = getIndependentComponent().getAlignmentAreas().iterator();
						return new Iterator<ScrolledComposite>() {
							@Override
							public boolean hasNext() {
								return iterator.hasNext();
							}

							@Override
							public ScrolledComposite next() {
								ToolkitSpecificAlignmentArea area = iterator.next().getToolkitComponent();
								if (area instanceof SWTAlignmentArea) {
									return ((SWTAlignmentArea)area).getContentScroller();
								}
								else {
									throw new IllegalStateException("The current alignment area does not have a SWT toolkit component.");
								}
							}

							@Override
							public void remove() {
								throw new UnsupportedOperationException("This iterator does not support the remove method.");
							}
						};
					}
				};
	}


	@Override
	public MultipleAlignmentsContainer getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public void repaint() {
		redraw();
	}

	
	@Override
	public int getAvailableHeight() {
		getSashForm().getParent().layout();  // Otherwise the old height of the sashForm would be returned.
		return getSashForm().getSize().y - 
				((SWTAlignmentArea)getIndependentComponent().getAlignmentAreas().get(  // Subtract height reserved for horizontal scroll bar.
						getIndependentComponent().getAlignmentAreas().size() - 1).getToolkitComponent()).getHorizontalScrollbarHeight();
	}


	@Override
	public int getNeededHeight(int alignmentIndex) {
		return getIndependentComponent().getAlignmentAreas().get(alignmentIndex).getContentArea().getSize().height + 
				NEEDED_BORDER_WIDTH;
	}


	@Override
	public void setDividerLocations(int[] heights) {
		// Add height of scroll bar to last height:
		int index = heights.length - 1;
		heights[index] += ((SWTAlignmentArea)getIndependentComponent().getAlignmentAreas().get(index).getToolkitComponent()).
				getHorizontalScrollbarHeight();
		
  	getSashForm().setWeights(heights);
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
