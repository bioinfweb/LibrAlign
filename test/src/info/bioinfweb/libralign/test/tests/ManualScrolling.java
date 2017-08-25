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
package info.bioinfweb.libralign.test.tests;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;



/**
 * Snippet that demonstrated scrolling subcomponents with an overall size above 2^15 - 1.
 * <p>
 * Source: https://www.eclipse.org/forums/index.php/t/121005/ 
 */
public class ManualScrolling {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final int INDENT = 4;
		final Composite c = new Composite(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		c.setLayout(new GridLayout(1, false));
		for (int i = 0; i < 2000; i++) {
			Button b = new Button(c, SWT.BORDER);
			b.setText(Integer.toString(i));
		}
		final ScrollBar hBar = c.getHorizontalBar();
		hBar.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event e) {
				int hSelection = hBar.getSelection();
				GridLayout layout = (GridLayout)c.getLayout();
				layout.marginWidth = -hSelection + INDENT;
				c.layout(false); // the false is important - clearing the cache	each time will really slow things down
			}
		});
		final ScrollBar vBar = c.getVerticalBar ();
		vBar.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				int vSelection = vBar.getSelection();
				GridLayout layout = (GridLayout)c.getLayout();
				layout.marginHeight = -vSelection + INDENT;
				c.layout(false);
			}
		});
		c.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle size = c.getClientArea();
				size.width += vBar.getSize().x;
				size.height += hBar.getSize().y;
				Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				hBar.setMaximum(preferredSize.x);
				hBar.setThumb(Math.min(size.width, preferredSize.x));
				hBar.setPageIncrement(size.width);
				vBar.setMaximum(preferredSize.y);
				vBar.setThumb(Math.min(size.height, preferredSize.y));
				vBar.setPageIncrement(size.height);
				// this could be improved - scrolls back to origin on resize
				hBar.setSelection(0);
				vBar.setSelection(0);
				GridLayout layout = (GridLayout)c.getLayout();
				layout.marginWidth = INDENT;  // Why is that done? The value does not seem to change here and below.
				layout.marginHeight = INDENT;
				c.layout(false);
			}
		});
		shell.setSize(100, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}	
}
