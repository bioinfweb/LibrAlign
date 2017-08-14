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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Dimension;

import info.bioinfweb.tic.toolkit.DefaultSWTComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;



public class DirectSWTAlignmentContentArea extends DefaultSWTComposite {
	private Point origin = new Point (0, 0);
	
	
	//TODO Assign size probably needs to be suppressed in the independent component?

	public DirectSWTAlignmentContentArea(AlignmentContentArea ticComponent, Composite parent, int style) {
		super(ticComponent, parent, style | SWT.NO_BACKGROUND |	SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		getHorizontalBar().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int hSelection = getHorizontalBar().getSelection();
				int destX = -hSelection - origin.x;
				Dimension dimension = getIndependentComponent().getSize();
				scroll(destX, 0, 0, 0, dimension.width, dimension.height, false);
				origin.x = -hSelection;
			}
		});

		getVerticalBar().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int vSelection = getVerticalBar().getSelection();
				int destY = -vSelection - origin.y;
				Dimension dimension = getIndependentComponent().getSize();
				scroll(0, destY, 0, 0, dimension.width, dimension.height, false);
				origin.y = -vSelection;
			}
		});
		
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Dimension dimension = getIndependentComponent().getSize();
				Rectangle client = getClientArea();
				getHorizontalBar().setMaximum(dimension.width);
				getVerticalBar().setMaximum(dimension.height);
				getHorizontalBar().setThumb(Math.min (dimension.width, client.width));
				getVerticalBar().setThumb(Math.min (dimension.height, client.height));
				int hPage = dimension.width - client.width;
				int vPage = dimension.height - client.height;
				int hSelection = getHorizontalBar().getSelection ();
				int vSelection = getVerticalBar().getSelection ();
				if (hSelection >= hPage) {
					if (hPage <= 0) {
						hSelection = 0;
					}
					origin.x = -hSelection;
				}
				if (vSelection >= vPage) {
					if (vPage <= 0) {
						vSelection = 0;
					}
					origin.y = -vSelection;
				}
				redraw();  //TODO Is this really necessary?
			}
		});
	}
 
	
	@Override
	public AlignmentContentArea getIndependentComponent() {
		return (AlignmentContentArea)super.getIndependentComponent();
	}


	@Override
	public void paintControl(PaintEvent e) {
		setScrollOffsetX(origin.x);
		setScrollOffsetY(origin.y);
		super.paintControl(e);
	}
}
