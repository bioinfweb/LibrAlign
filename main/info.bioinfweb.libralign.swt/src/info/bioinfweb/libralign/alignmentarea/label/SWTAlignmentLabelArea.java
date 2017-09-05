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
package info.bioinfweb.libralign.alignmentarea.label;


import info.bioinfweb.commons.Math2;
import info.bioinfweb.libralign.alignmentarea.content.ScrollContainerSWTAlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.rowsarea.SWTAlignmentRowsArea;
import info.bioinfweb.tic.SWTComponentFactory;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;



/**
 * The <i>SWT</i> component displaying the label components for an alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 * @bioinfweb.module info.bioinfweb.libralign.swt
 */
//TODO This class can later also be used when subcomponents are created. The ScrolledComposite for Labels can then be removed from ScrollContainerSWTAlignmentArea. AlignmentLabelArea does not need parameters to create different SWT classes anymore.
public class SWTAlignmentLabelArea extends SWTAlignmentRowsArea<AlignmentLabelSubArea> 
		implements ToolkitSpecificAlignmentLabelArea {
	
	/**
	 * Creates a new instance of this class.
   * <p>
   * Note that this method will only add subelements if the subelements of the associated {@link ScrollContainerSWTAlignmentContentArea}
   * are already present. Otherwise the component will be empty after calling this method and you need to call 
   * {@link #reinsertSubelements()} again after the associated alignment content area has been created. 
	 * 
	 * @param parent the parent component
	 * @param style the <i>SWT</i> style value
	 * @param independentComponent the toolkit independent component that uses this instance
	 */
	public SWTAlignmentLabelArea(AlignmentLabelArea independentComponent, Composite parent, int style) {
		super(independentComponent, parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		reinsertSubelements();
		addScrollListeners();
	}
	
	
	private void addScrollListeners() {
		final ScrollBar vBar = getVerticalBar();
		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.out.println("vertical bar listener");
				int vSelection = vBar.getSelection();
				RowLayout layout = (RowLayout)getLayout();
				layout.marginHeight = -vSelection;
				layout(false);
			}
		});
		//TODO Since the vertical scroll bar is never visible for the label area and programmatical setting of the selection does not trigger any events, the vertical scroll bar can be left out. Then no container is needed and scrolling can be performed in a setOrigin() method (that would anyway be needed).
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle size = getClientArea();
				//size.width += vBar.getSize().x;
				//size.height += hBar.getSize().y;  /TODO Is this needed?
				int fullHeight = (int)Math2.roundUp(getIndependentComponent().getOwner().getPaintHeight());
//				Point preferredSize = computeSize(SWT.DEFAULT, SWT.DEFAULT);  //TODO This returns illegal values, if components are already scrolled. => Replace by height from
//				hBar.setMaximum(preferredSize.x);
//				hBar.setThumb(Math.min(size.width, preferredSize.x));
//				hBar.setPageIncrement(size.width);
				vBar.setMaximum(fullHeight);
				vBar.setThumb(Math.min(size.height, fullHeight));
				vBar.setPageIncrement(size.height);
				// this could be improved - scrolls back to origin on resize
//				int hSelection = hBar.getSelection(); 
//				int vSelection = vBar.getSelection();
				
//				hBar.setSelection(0);
//				vBar.setSelection(0);
//				GridLayout layout = (GridLayout)c.getLayout();
//				layout.marginWidth = INDENT;
//				layout.marginHeight = INDENT;
				layout(false);				
				
//				hBar.setSelection(hSelection);
//				vBar.setSelection(vSelection);
//				layout.marginWidth = -hSelection + INDENT;
//				layout.marginHeight = -vSelection + INDENT;
//				c.layout(false);
			}
		});
		
	}


	@Override
	public AlignmentLabelArea getIndependentComponent() {
		return (AlignmentLabelArea)super.getIndependentComponent();
	}


	/**
   * Recreates the components displaying labels for sequences and data areas in the alignment according to
   * the current model information.
   * <p>
   * Note that this method will only add elements if the subelements of the associated 
   * {@link ScrollContainerSWTAlignmentContentArea} are already present. Otherwise the component will be 
   * empty after calling this method. 
	 */
	@Override
	public void reinsertSubelements() {
		if (getIndependentComponent().getOwner().getContentArea().hasToolkitComponent()) {
			removeAll();
			SWTComponentFactory factory = SWTComponentFactory.getInstance();
			Iterator<AlignmentLabelSubArea> iterator = getIndependentComponent().subAreaIterator();
			while (iterator.hasNext()) {
				final AlignmentLabelSubArea subArea = iterator.next();
				factory.getSWTComponent(subArea, this, SWT.NONE);
				subArea.assignSize();
			}
			getIndependentComponent().assignSize(); 
			layout(); // Needed to reposition elements if this methods is called again after the construction of the instance (e.g. when a new sequence was added).
		}
	}
}
