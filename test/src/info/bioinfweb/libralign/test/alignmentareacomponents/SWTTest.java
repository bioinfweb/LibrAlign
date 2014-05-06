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
package info.bioinfweb.libralign.test.alignmentareacomponents;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;



public class SWTTest {
	protected Shell shell;
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTTest window = new SWTTest();
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
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Composite parentComposite = new Composite(scrolledComposite, SWT.NONE);
		parentComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite composite1 = new Composite(parentComposite, SWT.NONE);
		
		Composite composite2 = new Composite(parentComposite, SWT.NONE);
		composite2.addMouseMoveListener(new MouseMoveListener() {
					public void mouseMove(MouseEvent e) {
						System.out.println("mouseMove " + e.x + " " + e.y);
					}
				});
		composite2.addMouseListener(new MouseListener() {
					@Override
					public void mouseUp(MouseEvent e) {
						System.out.println("mouseUp " + e.x + " " + e.y);
					}
					
					@Override
					public void mouseDown(MouseEvent e) {
						System.out.println("mouseDown " + e.x + " " + e.y);
					}
					
					@Override
					public void mouseDoubleClick(MouseEvent e) {
						System.out.println("mouseDoubleClick " + e.x + " " + e.y);
					}
				});
		
		Composite composite3 = new Composite(parentComposite, SWT.NONE);
		scrolledComposite.setContent(parentComposite);
		scrolledComposite.setMinSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
}
