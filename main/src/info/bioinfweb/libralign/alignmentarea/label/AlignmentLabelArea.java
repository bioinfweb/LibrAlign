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
package info.bioinfweb.libralign.alignmentarea.label;


import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;



/**
 * A toolkit independent GUI component displaying the sequence names in an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class AlignmentLabelArea extends TICComponent {
	public static final int BORDER_WIDTH = 2;
	
	
  private AlignmentArea owner;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that uses this instance
	 * @param position - Specify here whether this area will be used to label the head, the content, or the 
	 *        bottom part of the alignment area.
	 */
	public AlignmentLabelArea(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment area that uses this instance.
	 * 
	 * @return the owning alignment area
	 */
	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Calculates the needed with to label the associated alignment. Note that the actual width of this
	 * component is calculated using {@link #getGlobalMaximumNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public int getLocalMaximumNeededWidth() {
		int result = 0;
		if (getOwner().getContentArea().hasToolkitComponent()) {
			Iterator<AlignmentSubArea> iterator = getOwner().getContentArea().getToolkitComponent().subAreaIterator();
			while (iterator.hasNext()) {
				result = Math.max(result, iterator.next().getLabelSubArea().getNeededWidth());
			}
		}
		return result;
		//TODO Save value between calls
	}
	
	
	/**
	 * Returns the maximum needed to label the alignment calculated over all alignments contained in the parent
	 * {@link MultipleAlignmentsContainer}. If the parent alignment area is not contained in such a container, the
	 * return value is equal to {@link #getLocalMaximumNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public int getGlobalMaximumNeededWidth() {
		if (getOwner().hasContainer()) {
			int result = 0;
			for (AlignmentArea alignmentArea : getOwner().getContainer().getAlignmentAreas()) {
				result = Math.max(result, alignmentArea.getLabelArea().getLocalMaximumNeededWidth());
			}
			return result;
		}
		else {
			return getLocalMaximumNeededWidth();
		}
	}
	
	
	@Override
	public Dimension getSize() {
		return new Dimension(getGlobalMaximumNeededWidth(),	getOwner().getContentArea().getSize().height);  // If references starting from owner would be used here, there would be problems in initialization order.
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.	


	@Override
	protected JComponent doCreateSwingComponent() {
		return new SwingAlignmentLabelArea(this);
	}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		return new SWTAlignmentLabelArea(parent, style, this);
	}


	@Override
	public ToolkitSpecificAlignmentLabelArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentLabelArea)super.getToolkitComponent();
	}
}
