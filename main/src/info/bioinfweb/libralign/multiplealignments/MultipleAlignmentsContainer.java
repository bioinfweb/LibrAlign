/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
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
 * <p>
 * If you are using this component in a SWT GUI you need to call {@link #redistributeHeight()} after the creation of
 * the GUI component containing this instance is finished. In Swing GUIs that is not necessary.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentsContainer extends TICComponent implements List<AlignmentArea> {
	//TODO React to changes of the underlying list
	//TODO Throw exceptions if an alignment area that is not linked to this container is inserted also in complex methods and iterators.
	private List<AlignmentArea> alignmentAreas = new ArrayList<AlignmentArea>();
	private EditSettings editSettings = new EditSettings();
	private boolean distributeRemainingSpace = true;

	
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

	
	private void checkContainer(AlignmentArea alignmentArea) {
		if (alignmentArea.getContainer() != this) {
			throw new IllegalArgumentException("The alignment area to be inserted does not reference this instance as its container.");
		}
	}
	

	@Override
	public boolean add(AlignmentArea e) {
		checkContainer(e);
		return alignmentAreas.add(e);
	}
	

	@Override
	public void add(int index, AlignmentArea element) {
		checkContainer(element);
		alignmentAreas.add(index, element);
	}

	
	@Override
	public boolean contains(Object area) {
		return alignmentAreas.contains(area);
	}

	
	@Override
	public AlignmentArea get(int index) {
		return alignmentAreas.get(index);
	}

	
	@Override
	public int indexOf(Object o) {
		return alignmentAreas.indexOf(o);
	}

	
	@Override
	public AlignmentArea remove(int index) {
		return alignmentAreas.remove(index);
	}

	
	@Override
	public boolean remove(Object area) {
		return alignmentAreas.remove(area);
	}
	

	@Override
	public int size() {
		return alignmentAreas.size();
	}


	@Override
	public ListIterator<AlignmentArea> listIterator() {
		return alignmentAreas.listIterator();
	}


	@Override
	public boolean addAll(Collection<? extends AlignmentArea> arg0) {
		return alignmentAreas.addAll(arg0);
	}


	@Override
	public boolean addAll(int index, Collection<? extends AlignmentArea> c) {
		return alignmentAreas.addAll(index, c);
	}


	@Override
	public void clear() {
		alignmentAreas.clear();
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return alignmentAreas.containsAll(c);
	}


	@Override
	public boolean equals(Object other) {
		return alignmentAreas.equals(other);
	}


	@Override
	public int hashCode() {
		return alignmentAreas.hashCode();
	}


	@Override
	public boolean isEmpty() {
		return alignmentAreas.isEmpty();
	}


	@Override
	public Iterator<AlignmentArea> iterator() {
		return alignmentAreas.iterator();
	}


	@Override
	public int lastIndexOf(Object element) {
		return alignmentAreas.lastIndexOf(element);
	}


	@Override
	public ListIterator<AlignmentArea> listIterator(int index) {
		return alignmentAreas.listIterator(index);
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		return alignmentAreas.removeAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		return alignmentAreas.retainAll(c);
	}


	@Override
	public AlignmentArea set(int index, AlignmentArea element) {
		return alignmentAreas.set(index, element);
	}


	@Override
	public List<AlignmentArea> subList(int fromIndex, int toIndex) {
		return alignmentAreas.subList(fromIndex, toIndex);
	}


	@Override
	public Object[] toArray() {
		return alignmentAreas.toArray();
	}


	@Override
	public <T> T[] toArray(T[] array) {
		return alignmentAreas.toArray(array);
	}


	@Override
	public ToolkitSpecificMultipleAlignmentsContainer getToolkitComponent() {
		return (ToolkitSpecificMultipleAlignmentsContainer)super.getToolkitComponent();
	}


	public void redistributeHeight() {
  	// Calculate available and needed heights:
  	int availableHeight = getToolkitComponent().getAvailableHeight();  // Height that can be distributed among the scroll panes.
  	if (availableHeight > 0) {  // availableHeight 0 in the first call from SWT.
	  	int maxNeededHeight = 0;  // Height needed for all areas to be fully visible.
	  	int minNeededHeight = 0;  // Height needed only for the areas that do not allows scrolling to be fully visible.
	  	for (int i = 0; i < size(); i++) {
				int neededHeight = getToolkitComponent().getNeededHeight(i);
				maxNeededHeight += neededHeight;
				if (!get(i).isAllowVerticalScrolling()) {
					minNeededHeight += neededHeight;
				}
			}
	  	//TODO subtract scroll bar height from availableHeight?
	  	
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
	  	int[] heights = new int[size()];
	  	for (int i = 0; i < size(); i++) {
	  		heights[i] = getToolkitComponent().getNeededHeight(i);
	  		if (get(i).isAllowVerticalScrolling()) {
	  			heights[i] = Math.round(heights[i] * visibleFractionForScrolling);
	  			noOfScrollableComponents++;
	  		}
	  		else {
	  			heights[i] = Math.round(heights[i] * visibleFractionForNoScrolling);
	  		}
	  		usedHeight += heights[i];
			}
	  	
	  	// Distribute remaining height:
	  	if (minNeededHeight > availableHeight) {  // All areas have to be scrolled.
	  		noOfScrollableComponents = size();
	  	}
	  	availableHeight -= usedHeight; 
	  	int lastIndex = heights.length - 1;
	  	if (isDistributeRemainingSpace()) {
		  	int availableHeightPerComponent = availableHeight / noOfScrollableComponents;
		  	for (int i = 0; i < size(); i++) {
		  		if (scrollAllComponents || get(i).isAllowVerticalScrolling()) {
		  			heights[i] += availableHeightPerComponent;
		  			availableHeight -= availableHeightPerComponent;
		  			lastIndex = i;
		  		}
		  	}
	  	}
	  	heights[lastIndex] += availableHeight;  // Last area might get more space due to rounding issues.
	  	
	  	getToolkitComponent().setDividerLocations(heights);
  	}
	}
}
