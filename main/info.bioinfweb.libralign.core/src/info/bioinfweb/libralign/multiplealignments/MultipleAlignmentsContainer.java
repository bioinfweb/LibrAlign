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
package info.bioinfweb.libralign.multiplealignments;


import java.awt.Dimension;
import java.util.Set;

import javax.swing.JRootPane;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.GUITools;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.dataarea.implementations.sequenceindex.SequenceIndexArea;
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
	private EditSettings editSettings;
	private boolean distributeRemainingSpace = false;

	
	/**
	 * Creates a new instance of this class with its own instance of {@link EditSettings}.
	 */
	public MultipleAlignmentsContainer() {
		this(new EditSettings());
	}


	/**
	 * Creates a new instance of this class that uses the specified instance of {@link EditSettings}.
	 * <p>
	 * Use this constructor to share an edit settings object between multple container instances.
	 * 
	 * @param editSettings the edit settings object to be used
	 */
	public MultipleAlignmentsContainer(EditSettings editSettings) {
		super();
		this.editSettings = editSettings;
	}


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
	protected String getSwingComponentClassName(Object... parameters) {
		return "info.bioinfweb.libralign.multiplealignments.SwingMultipleAlignmentsContainer";
	}


	@Override
	protected String getSWTComponentClassName(Object... parameters) {
		return "info.bioinfweb.libralign.multiplealignments.SWTMultipleAlignmentsContainer";
	}


	@Override
	protected Object[] getSWTComponentConstructorParameters(Object... parameters) {
		return new Object[]{new Boolean(GUITools.determineUseSubcomponents(parameters))};
	}


	@Override
	public Dimension getSize() {
		// Just returns the size already set to the toolkit component because the size of an alignment area is determined just by the layout manager and not dependent of the alignment and data area size.
		if (hasToolkitComponent()) {
			return getToolkitComponent().getToolkitSize();
		}
		else {
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
   * Returns the alignment area contained in this component, that currently has the focus. (It is also possible,
   * that a subcomponent if the returned area (e.g. a sequence or data area) has the focus.)
   * <p>
   * Note that this method behaves slightly different if used in <i>Swing</i> or in <i>SWT</i>. In <i>Swing</i> 
   * applications the components contained in an alignment area may loose their focus, if the user clicks on a menu 
   * entry (unless the {@link JRootPane} is not set unfocusable) or a button. Therefore the focused alignment area 
   * may not be correctly determined inside an action attached to another <i>Swing</i> component (e.g. a button or a 
   * menu item), because this component then has the focus. This problem will not occur if an action is invoked 
   * using a keyboard shortcut.
   * <p>
   * In <i>SWT</i> applications the focused alignment area can usually still be determined, even if a button or menu 
   * item is currently active. 
   * 
   * @return the focused alignment area in this component or {@code null} of the focus is not inside this container
   */
	public AlignmentArea getFocusedAlignmentArea() {
		if (hasToolkitComponent()) {
			return getToolkitComponent().getFocusedAlignmentArea();
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Returns a set of property names that are synchronized between the paint setting objects of the alignment areas
	 * contained in this container. By default all properties are synchronized. Application code can  add names to define 
	 * single paint settings as global. (Such a property will than be changed in all alignment areas of this container, 
	 * as soon as it is modified in one of them.)
	 * <p>
	 * Note that all simple properties of {@link PaintSettings} can be added here, but it is not possible to synchronize
	 * token painters.
	 * 
	 * @return a modifiable set of paint setting property names
	 * @see PaintSettings
	 */
	public Set<String> getPaintSettingsToSynchronize() {
		return getAlignmentAreas().getPaintSettingsSynchronizer().getPropertiesToSynchronizes();
	}
	
	
	/**
	 * Sets all properties of {@link PaintSettings} to be synchronized between all alignment areas in this container.
	 */
	public void setAllPaintSettingPropertiesToSynchronize() {
		getAlignmentAreas().getPaintSettingsSynchronizer().addAllProperties();
	}
}
