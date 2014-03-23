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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.Transient;
import java.util.Iterator;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaChangeEvent;
import info.bioinfweb.libralign.dataarea.DataAreaList;
import info.bioinfweb.libralign.dataarea.DataAreaModelListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;



/**
 * The Swing component rendering an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class SwingAlignmentArea extends JPanel 
    implements ToolkitSpecificAlignmentArea, DataAreaModelListener, Scrollable {
	
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	

	public SwingAlignmentArea(AlignmentArea independentComponent) {
		super();
		this.independentComponent = independentComponent;
		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
		init();
	}
	
	
	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		reinsertSubelements();
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}
	
	
	private void addDataAreaList(DataAreaList list) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				add(dataArea.createSwingComponent());
			}
		}
	}
	
	
	@Override
	public void reinsertSubelements() {
		removeAll();
		addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			add(sequenceAreaMap.get(id).createSwingComponent());
			addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}

		addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
	}

	
	@Override
	@Transient
	public Dimension getPreferredSize() {
		//TODO AWT tree lock?;
		Dimension result = new Dimension(0, 0);
		Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			Dimension componentSize = components[i].getPreferredSize();
			result.width = Math.max(result.width, componentSize.width);
			result.height += componentSize.height;
		}
		return result;
	}


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return getPreferredSize();
	}


	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 10;
	}


	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 10;
	}


	private int componentIndex(JComponent component) {
		for (int i = 0; i < getComponentCount(); i++) {
			if (component == getComponent(i)) {
				return i;
			}
		}
		return -1;
	}
	

	@Override
	public void dataAreaInsertedRemoved(DataAreaChangeEvent e) {
		reinsertSubelements();
		//TODO possibly implement more efficient method that only deals with the affected elements later.
		//		switch (e.getType()) {
		//			case INSERTION:
		//				
		//				add(comp, index)
		//				e.getAffectedElements()
		//				break;
		//			case DELETION:
		//				break;
		//			case REPLACEMENT:
		//				break;
		//		}
	}


	@Override
	public void dataAreaVisibilityChanged(DataAreaChangeEvent e) {
		reinsertSubelements();
		//TODO possibly implement more efficient method that only deals with the affected elements later.
	}
}
