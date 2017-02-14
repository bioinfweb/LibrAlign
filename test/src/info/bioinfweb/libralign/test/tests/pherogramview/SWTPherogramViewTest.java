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
package info.bioinfweb.libralign.test.tests.pherogramview;


import info.bioinfweb.tic.SWTComponentFactory;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;



public class SWTPherogramViewTest extends AbstractPherogramViewTest {	
	protected Shell shell;
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTPherogramViewTest window = new SWTPherogramViewTest();
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
		//getPherogramView().getTraceCurveView().assignSize();
		//getPherogramView().getHeadingView().assignSize();
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
		shell.setText("SWT PherogramView Test");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		
		SWTComponentFactory.getInstance().getSWTComponent(getPherogramView(), shell, SWT.NONE);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmTest_1 = new MenuItem(menu, SWT.CASCADE);
		mntmTest_1.setText("Test");
		
		Menu menu_1 = new Menu(mntmTest_1);
		mntmTest_1.setMenu(menu_1);
		
		MenuItem mntmToggleShowProbability = new MenuItem(menu_1, SWT.NONE);
		mntmToggleShowProbability.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getPherogramView().getTraceCurveView().getFormats().toggleShowProbabilityValues();
			}
		});
		mntmToggleShowProbability.setText("Toggle show probability values");
	}
}
