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
package info.bionfweb.libralign.demo.paper;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.io.IOTools;
import info.bioinfweb.tic.SWTComponentFactory;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class SWTApplication {
	protected Shell shell;

	
	/**
	 * Launch the application.
	 * 
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
	 * 
	 * @throws Exception 
	 */
	public void open() throws Exception {
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
	 * 
	 * @throws Exception 
	 */
	protected void createContents() throws Exception {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Read an alignment file:
		List<AlignmentModel<?>> models = IOTools.readAlignments(new File("data/PaperExample.nexml"));
		
		// Create an alignment area displaying the first alignment from the file:
		AlignmentArea area = new AlignmentArea();
		area.setAlignmentModel(models.get(0), false);
		
		// Add the SWT version of alignment area to the GUI:
		SWTComponentFactory.getInstance().getSWTComponent(area, shell, SWT.NONE);
	}
}
