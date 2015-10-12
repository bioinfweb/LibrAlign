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
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.EventQueue;
import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;



/**
 * Example that demonstrates using LibrAlign GUI components in Swing applications.
 * <p>
 * This example is used in the 
 * <a href="http://bioinfweb.info/LibrAlign/Documentation/wiki/Working_with_toolkits">LibrAlign documentation</a>.
 * 
 * @author Ben St&ouml;ver
 */
public class SwingApplication extends AbstractApplication {
	private JFrame frame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingApplication window = new SwingApplication();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the application.
	 */
	public SwingApplication() {
		initialize();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 300, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Basic Swing demo");

		// Create main container instance (TIC component):
		MultipleAlignmentsContainer container = createContainer();
		
		// Create Swing-specific component from TIC component:
		JComponent swingContainer = SwingComponentFactory.getInstance().getSwingComponent(container);
		
		// Add Swing component to GUI:
		frame.getContentPane().add(swingContainer, BorderLayout.CENTER);
	}
}
