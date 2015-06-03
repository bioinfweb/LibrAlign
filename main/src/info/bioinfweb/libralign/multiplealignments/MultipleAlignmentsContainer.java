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
package info.bioinfweb.libralign.multiplealignments;


import java.awt.Dimension;
import java.util.Set;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;
import info.bioinfweb.libralign.editsettings.EditSettings;



/**
 * This class allows to use multiple instances of {@link AlignmentArea}, that are coupled related to horizontal
 * scrolling. This can e.g. be used to display a data area with heading information (e.g. {@link SequenceIndexArea}) 
 * which always remains visible independently of the vertical scrolling of the alignment it belongs to. (To achieve this
 * you would add to alignment areas to an instance of this class, one containing the heading data area and the other one
 * containing the alignment.)
 * <p>
 * Note that it makes only sense to combine alignment areas that display related information and therefore have an
 * equal number of according columns (except the area containing only data areas).
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentsContainer extends TICComponent {
	private AlignmentAreaList alignmentAreas = new AlignmentAreaList(this);
	private EditSettings editSettings = new EditSettings();
	private boolean distributeRemainingSpace = false;

	
	/**
	 * Use the returned instance to change the alignment areas contained in this container.
	 * 
	 * @return a reference to the list with the alignment areas currently contained in this container
	 */
	public AlignmentAreaList getAlignmentAreas() {
		return alignmentAreas;
	}


	/**
	 * Returns the edit settings that are shared among all contained alignment areas.
	 * 
	 * @return the edit settings object
	 */
	public EditSettings getEditSettings() {
		return editSettings;
	}


	/**
	 * Returns whether remaining space shall be distributed among all scrollable alignments. (See 
	 * {@link #setDistributeRemainingSpace(boolean)} for further details.) 
	 * 
	 * @return {@code true} is the height shall be distributed, {@code false} if the bottom most alignment
	 *         shall get all the space
	 */
	public boolean isDistributeRemainingSpace() {
		return distributeRemainingSpace;
	}


	/**
	 * If the height of this container is higher than the height that is needed to display all alignments without 
	 * scrolling, you can specify here how the remaining space shall be distributed among the alignments.
	 * 
	 * @param distributeRemainingSpace - Specify {@code true} here if you want the additional height to be distributed
	 *        equally among all alignments that are marked as vertically scrollable. If you specify {@code false} the
	 *        whole space is provided to the bottom most alignment (no matter if it is scrollable or not).
	 */
	public void setDistributeRemainingSpace(boolean distributeRemainingSpace) {
		this.distributeRemainingSpace = distributeRemainingSpace;
		redistributeHeight();
	}


	@Override
	protected Composite doCreateSWTWidget(Composite parent, int style) {
		return new SWTMultipleAlignmentsContainer(parent, style, this);
	}


	@Override
	protected JComponent doCreateSwingComponent() {
		return new SwingMultipleAlignmentsContainer(this);
	}


	@Override
	public SWTMultipleAlignmentsContainer createSWTWidget(Composite parent, int style) {
		return (SWTMultipleAlignmentsContainer)super.createSWTWidget(parent, style);
	}


	@Override
	public SwingMultipleAlignmentsContainer createSwingComponent() {
		return (SwingMultipleAlignmentsContainer)super.createSwingComponent();
	}


	@Override
	public Dimension getSize() {
		switch (getCurrentToolkit()) {
			case SWING:
				return ((JComponent)getToolkitComponent()).getPreferredSize();  //TODO correct size?
			case SWT:
				Point point = ((Composite)getToolkitComponent()).getSize();
				return new Dimension(point.x, point.y);
			default:
			  return new Dimension(0, 0);
		}
	}


	@Override
	public void paint(TICPaintEvent event) {}  // Remains empty because toolkit specific components are provided.

	
	@Override
	public ToolkitSpecificMultipleAlignmentsContainer getToolkitComponent() {
		return (ToolkitSpecificMultipleAlignmentsContainer)super.getToolkitComponent();
	}

	
	public void assignSizeToAll() {
		for (AlignmentArea alignmentArea : getAlignmentAreas()) {
			alignmentArea.assignSizeToAll();
		}
		assignSize();
		redistributeHeight();
	}	
	

	public void redistributeHeight() {
		if (hasToolkitComponent()) {
			// Calculate available and needed heights:
			int availableHeight = getToolkitComponent().getAvailableHeight();  // Height that can be distributed among the scroll panes.
			if (availableHeight > 0) {  // availableHeight 0 in the first call from SWT.
			  	int maxNeededHeight = 0;  // Height needed for all areas to be fully visible.
			  	int minNeededHeight = 0;  // Height needed only for the areas that do not allows scrolling to be fully visible.
			  	for (int i = 0; i < getAlignmentAreas().size(); i++) {
						int neededHeight = getToolkitComponent().getNeededHeight(i);
						maxNeededHeight += neededHeight;
						if (!getAlignmentAreas().get(i).isAllowVerticalScrolling()) {
							minNeededHeight += neededHeight;
						}
					}
			  	
			  	// Calculate the visible fraction of the two types of areas:
			  	boolean scrollAllComponents = (minNeededHeight > availableHeight);
			  	float visibleFractionForNoScrolling;
			  	float visibleFractionForScrolling;
			  	if (maxNeededHeight <= availableHeight) {  // No area has to be scrolled.
			  		visibleFractionForNoScrolling = 1.0f;
			  		visibleFractionForScrolling = 1.0f;
			  	}
			  	else if (!scrollAllComponents) {  // Only the areas that allow scrolling have to be scrolled.
			  		visibleFractionForNoScrolling = 1.0f;
			  		visibleFractionForScrolling = (float)(availableHeight - minNeededHeight)  // remaining available height for areas that allow scrolling 
			  				/ (float)(maxNeededHeight - minNeededHeight);  // needed height for areas that allow scrolling
			  	}
			  	else {  // All areas have to be scrolled.
			  		visibleFractionForNoScrolling = (float)availableHeight / (float)maxNeededHeight;
			  		visibleFractionForScrolling = visibleFractionForNoScrolling;
			  	}
			  	
			  	// Set divider locations:
			  	int usedHeight = 0;
			  	int noOfScrollableComponents = 0;
			  	int[] heights = new int[getAlignmentAreas().size()];
			  	for (int i = 0; i < getAlignmentAreas().size(); i++) {
			  		heights[i] = getToolkitComponent().getNeededHeight(i);
			  		if (getAlignmentAreas().get(i).isAllowVerticalScrolling()) {
			  			heights[i] = Math2.roundUp(heights[i] * visibleFractionForScrolling);
			  			noOfScrollableComponents++;
			  		}
			  		else {
			  			heights[i] = Math2.roundUp(heights[i] * visibleFractionForNoScrolling);
			  		}
			  			usedHeight += heights[i];
					}
			  	
			  	// Distribute remaining height:
			  	if (minNeededHeight > availableHeight) {  // All areas have to be scrolled.
			  		noOfScrollableComponents = getAlignmentAreas().size();
			  	}
			  	availableHeight -= usedHeight; 
			  	int lastIndex = heights.length - 1;
			  	if (isDistributeRemainingSpace()) {
				  	int availableHeightPerComponent = availableHeight / noOfScrollableComponents;
				  	for (int i = 0; i < getAlignmentAreas().size(); i++) {
				  		if (scrollAllComponents || getAlignmentAreas().get(i).isAllowVerticalScrolling()) {
				  			heights[i] += availableHeightPerComponent;
				  			availableHeight -= availableHeightPerComponent;
				  			lastIndex = i;
				  		}
				  	}
			  	}
			  	heights[lastIndex] += availableHeight;  // Last area might get more space due to rounding issues even if isDistributeRemainingSpace() returned true.
			  	
			  	getToolkitComponent().setDividerLocations(heights);
			}
		}
	}
	
	
	/**
	 * Returns a set of property names that are synchronized between the paint setting objects of the alignment areas
	 * contained in this container. By default this set is empty. Application code can add names to define single paint
	 * settings as global.
	 * 
	 * @return a modifiable set of paint setting property names
	 * @see PaintSettings
	 */
	public Set<String> getPaintSettingsToSynchronize() {
		return getAlignmentAreas().getPaintSettingsSynchronizer().getPropertiesToSynchronizes();
	}
}
