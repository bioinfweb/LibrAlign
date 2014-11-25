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
 * equal number of according columns.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentsContainer extends TICComponent implements List<AlignmentArea> {
	//TODO React to changes of the underlying list
	//TODO Throw exceptions if an alignment area that is not linked to this container is inserted also in complex methods and iterators.
	private List<AlignmentArea> alignmentAreas = new ArrayList<AlignmentArea>();
	private EditSettings editSettings = new EditSettings();

	
	/**
	 * Returns the edit settings that are shared among all contained alignment areas.
	 * 
	 * @return the edit settings object
	 */
	public EditSettings getEditSettings() {
		return editSettings;
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
}
