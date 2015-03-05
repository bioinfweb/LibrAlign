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
package info.bioinfweb.libralign.test.performance.application;


import info.bioinfweb.commons.testing.TestLogger;
import info.bioinfweb.libralign.alignmentarea.SwingAlignmentArea;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;



public class SwingTest extends AbstractApplication {
	private JFrame frame;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Thread.sleep(0 /*10000*/);

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						SwingTest window = new SwingTest();

						TestLogger.getInstance().saveCurrentTime("setVisible");
						window.frame.setVisible(true);
						TestLogger.getInstance().logTimeSince("performance", "setVisible");
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create the application.
	 */
	public SwingTest() {
		initialize();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		TestLogger.getInstance().saveCurrentTime("createSwingComp");
		SwingAlignmentArea swingAlignmentArea = getAlignmentArea().createSwingComponent(); 
		TestLogger.getInstance().logTimeSince("performance", "createSwingComp");

		TestLogger.getInstance().saveCurrentTime("addToGUI");
		frame.getContentPane().add(swingAlignmentArea, BorderLayout.CENTER);
		TestLogger.getInstance().logTimeSince("performance", "addToGUI");
	}
}
