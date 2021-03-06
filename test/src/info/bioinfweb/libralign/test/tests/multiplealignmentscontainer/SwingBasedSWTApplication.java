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
package info.bioinfweb.libralign.test.tests.multiplealignmentscontainer;


import java.awt.BorderLayout;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import info.bioinfweb.commons.swing.SwingUtils;
import info.bioinfweb.commons.swt.SWTUtils;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.SWTComponentFactory;
import info.bioinfweb.tic.SwingComponentFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;



public class SwingBasedSWTApplication extends AbstractApplication {
	protected Shell shell;
	private MultipleAlignmentsContainer alignmentsContainer;
	
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SwingBasedSWTApplication window = new SwingBasedSWTApplication();
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
		shell.setSize(450, 300);
		shell.setText("Swing based SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		alignmentsContainer = createAlignmentsContainer();
		SwingUtils.setNativeLF();
		SWTUtils.embedAWTComponent(SwingComponentFactory.getInstance().getSwingComponent(alignmentsContainer), shell);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmTest = new MenuItem(menu, SWT.CASCADE);
		mntmTest.setText("Test");
		
		Menu menu_1 = new Menu(mntmTest);
		mntmTest.setMenu(menu_1);
		
		MenuItem mntmPrintFocusedArea = new MenuItem(menu_1, SWT.NONE);
		mntmPrintFocusedArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(alignmentsContainer.getFocusedAlignmentArea());
			}
		});
		mntmPrintFocusedArea.setText("Print focused area");
	}
}
