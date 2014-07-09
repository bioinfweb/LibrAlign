/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.commons.swt.ScrolledCompositeSyncListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;



/**
 * SWT component displaying the head, content, or bottom area of an alignment area. This class also implements
 * the necessary scrolling functionality and offers workarounds for missing Swing functionality in SWT.
 * <p>
 * This class is meant for internal use by {@link AlignmentArea} and should not be instantiated directly.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SWTAlignmentPartScroller extends Composite {
	private AlignmentArea owner;
	private DataAreaListType position;
	
	private Composite labelContainer;
	private ScrolledComposite labelScroller;
  private AlignmentLabelArea labelArea;
  private Composite partContainer;
  private ScrolledComposite partScroller;
  private SWTAlignmentPartArea partArea;
  
  
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area using this component
	 * @param parent - the parent SWT component
	 * @param position - the position of this component in {@code owner}
	 * @param hidePartScrollBar - Specify {@code true} here if you want no horizontal scroll bar to be displayed
	 *        in this part of the alignment are or {@code false} otherwise.
	 */
	public SWTAlignmentPartScroller(AlignmentArea owner, Composite parent, DataAreaListType position, 
			boolean hidePartScrollBar) {
		
		super(parent, SWT.NONE);
		this.owner = owner;
		this.position = position;
		initGUI(hidePartScrollBar);
	}
	
	
	private void initGUI(boolean hidePartScrollBar) {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Label components:
		labelContainer = new Composite(this, SWT.NONE);
		labelScroller = new ScrolledComposite(labelContainer,	SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		labelArea = new AlignmentLabelArea(getOwner(), getPosition());
		labelScroller.setContent(labelArea.createSWTWidget(labelScroller, SWT.NONE));
		labelContainer.addControlListener(  // Must not be called before both field are initialized.
				new SWTScrolledCompositeResizeListener(labelContainer, labelScroller, true));
		
		// Alignment area part components:
		if (hidePartScrollBar) {
			partContainer = new Composite(this, SWT.NONE);
			partScroller = new ScrolledComposite(partContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		}
		else {
			partContainer = null;
			partScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		}
		partArea = new SWTAlignmentPartArea(partScroller, SWT.NONE);
		partScroller.setContent(partArea);
		if (hidePartScrollBar) {
			partContainer.addControlListener(  // Must not be called before both field are initialized.
					new SWTScrolledCompositeResizeListener(partContainer, partScroller, false));
		}
		
		// Link label area:
		labelArea.setAlignmentPartArea(partArea);
		
		// Synchronize vertical scrolling:
		ScrolledCompositeSyncListener verticalSyncListener = new ScrolledCompositeSyncListener(
				new ScrolledComposite[]{labelScroller, partScroller}, false);
		verticalSyncListener.registerToAll();		
	}


	public AlignmentArea getOwner() {
		return owner;
	}


	public DataAreaListType getPosition() {
		return position;
	}


	public Composite getLabelContainer() {
		return labelContainer;
	}


	public ScrolledComposite getLabelScroller() {
		return labelScroller;
	}


	public AlignmentLabelArea getLabelArea() {
		return labelArea;
	}


	public Composite getPartContainer() {
		return partContainer;
	}


	public ScrolledComposite getPartScroller() {
		return partScroller;
	}


	public SWTAlignmentPartArea getPartArea() {
		return partArea;
	}
}
