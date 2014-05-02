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


import java.awt.geom.Dimension2D;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaList;



/**
 * The SWT component rendering an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SWTAlignmentArea extends Composite implements ToolkitSpecificAlignmentArea {
	private AlignmentArea independentComponent;
	private SequenceAreaMap sequenceAreaMap;
	

	public SWTAlignmentArea(Composite parent, int style, AlignmentArea independentComponent) {
		super(parent, style);
		this.independentComponent = independentComponent;
		sequenceAreaMap = new SequenceAreaMap(getIndependentComponent());
		init();
	}
	
	
	private void init() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginBottom = 0;
		rowLayout.spacing = 0;
		super.setLayout(rowLayout);
		reinsertSubelements();
	}


	@Override
	public AlignmentArea getIndependentComponent() {
		return independentComponent;
	}


	@Override
	public void repaint() {
		redraw();
	}


	private void addDataAreaList(DataAreaList list) {
		Iterator<DataArea> iterator = list.iterator();
		while (iterator.hasNext()) {
			DataArea dataArea = iterator.next();
			if (dataArea.isVisible()) {
				Composite composite = dataArea.createSWTWidget(this, SWT.NONE);
				Dimension2D size = dataArea.getSize();
				composite.setLayoutData(new RowData((int)Math.round(size.getWidth()), (int)Math.round(size.getHeight())));
			}
		}
	}
	
	
	@Override
	public void reinsertSubelements() {
		for (Control control : getChildren()) {  // Temporary implementation for removeAll() in Swing
      control.dispose();                     //TODO Implement an alternative that keeps the instances that can be reused.
    }
		 
		addDataAreaList(getIndependentComponent().getDataAreas().getTopAreas());
		
		Iterator<Integer> idIterator = getIndependentComponent().getSequenceOrder().getIdList().iterator();
		while (idIterator.hasNext()) {
			Integer id = idIterator.next();
			
			SequenceArea sequenceArea = sequenceAreaMap.get(id);
			Composite composite = sequenceArea.createSWTWidget(this, SWT.NO_REDRAW_RESIZE);
			Dimension2D size = sequenceArea.getSize();
			composite.setLayoutData(new RowData((int)Math.round(size.getWidth()), (int)Math.round(size.getHeight())));
			
			addDataAreaList(getIndependentComponent().getDataAreas().getSequenceAreas(id));
		}

		addDataAreaList(getIndependentComponent().getDataAreas().getBottomAreas());
		
		//layout(true);  //TODO Necessary?
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
}
