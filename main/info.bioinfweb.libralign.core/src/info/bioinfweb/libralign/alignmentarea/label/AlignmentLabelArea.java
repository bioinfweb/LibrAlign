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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;

import java.awt.Dimension;
import java.util.Iterator;



/**
 * A toolkit independent GUI component displaying the sequence names in an {@link AlignmentArea}.
 * <p>
 * Application developers will not need to create instances of this class directly but should 
 * use {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class AlignmentLabelArea extends TICComponent {
	public static final int BORDER_WIDTH = 2;
	public static final double RECALCULATE_VALUE = -1.0;
	
	
  private AlignmentArea owner;
  private double localMaxNeededWidth = RECALCULATE_VALUE;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that uses this instance
	 * @param position Specify here whether this area will be used to label the head, the content, or the 
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
	
	
	public AlignmentLabelSubAreaIterator subAreaIterator() {
		return new AlignmentLabelSubAreaIterator(getOwner().getContentArea().subAreaIterator());
	}


	/**
	 * Calculates the needed with to label the associated alignment. Note that the actual width of this
	 * component is calculated using {@link #getGlobalMaximumNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public double getLocalMaximumNeededWidth() {
		if (localMaxNeededWidth == RECALCULATE_VALUE) {
			localMaxNeededWidth = 0;
			if (getOwner().getContentArea().hasToolkitComponent()) {
				Iterator<AlignmentLabelSubArea> iterator = subAreaIterator();
				while (iterator.hasNext()) {
					localMaxNeededWidth = Math.max(localMaxNeededWidth, iterator.next().getNeededWidth());
				}
			}
		}
		return localMaxNeededWidth;
	}
	
	
	/**
	 * Flags the needed width to display all labels to be recalculated the next time it is accessed.
	 * <p>
	 * Note that this method only marks the needed width of this area to be recalculated. 
	 * {@link #setLocalMaxWidthRecalculateToAll()} should always be called instead, unless this method
	 * is called manually for all areas inside an {@link MultipleAlignmentsContainer}.
	 * <p>
	 * Application code will usually not have to call this method directly.
	 */
	public void setLocalMaxWidthRecalculate() {
		localMaxNeededWidth = RECALCULATE_VALUE;
	}
	
	
	public void setLocalMaxWidthRecalculateToAll() {
		if (getOwner().hasContainer()) {
			for (AlignmentArea alignmentArea : getOwner().getContainer().getAlignmentAreas()) {
				alignmentArea.getLabelArea().setLocalMaxWidthRecalculate();
			}
		}
		else {
			setLocalMaxWidthRecalculate();
		}
	}
	
	
	/**
	 * Returns the maximum needed width to label the alignment calculated over all alignments contained in the parent
	 * {@link MultipleAlignmentsContainer}. If the parent alignment area is not contained in such a container, the
	 * return value is equal to {@link #getLocalMaximumNeededWidth()}.
	 * 
	 * @return a value >= 0
	 */
	public double getGlobalMaximumNeededWidth() {
		if (getOwner().hasContainer()) {
			double result = 0;
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
		return new Dimension((int)Math.round(getGlobalMaximumNeededWidth()),	getOwner().getContentArea().getSize().height);  // If references starting from owner would be used here, there would be problems in initialization order.
	}
	
	
	public void assignSizeToAll() {
		if (hasToolkitComponent()) {
			Iterator<AlignmentLabelSubArea> iterator = subAreaIterator();
			while (iterator.hasNext()) {
				iterator.next().assignSize();
			}
		}
		assignSize();
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.	


	@Override
	protected String getSwingComponentClassName(Object... parameters) {
		return "info.bioinfweb.libralign.alignmentarea.label.ScrollContainerSwingAlignmentLabelArea";
	}


	@Override
	protected String getSWTComponentClassName(Object... parameters) {
		return "info.bioinfweb.libralign.alignmentarea.label.SWTAlignmentLabelArea";
	}


	@Override
	public ToolkitSpecificAlignmentLabelArea getToolkitComponent() {
		return (ToolkitSpecificAlignmentLabelArea)super.getToolkitComponent();
	}
}
