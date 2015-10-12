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
package info.bionfweb.libralign.demo.docexamples.toolkit;


import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.SWTComponentFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;



/**
 * Example that demonstrates using LibrAlign GUI components in SWT applications.
 * <p>
 * This example is used in the 
 * <a href="http://bioinfweb.info/LibrAlign/Documentation/wiki/Working_with_toolkits">LibrAlign documentation</a>.
 * 
 * @author Ben St&ouml;ver
 */
public class SWTApplication extends AbstractApplication {
	protected Shell shell;
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTApplication window = new SWTApplication();
			window.open();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(300, 150);
		shell.setText("Basic SWT demo");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Create main container instance (TIC component):
		MultipleAlignmentsContainer container = createContainer();
		
		// Create SWT-specific component from TIC component and add it to the SWT GUI:
		SWTComponentFactory.getInstance().getSWTComponent(container, shell, SWT.NONE);
	}
}
