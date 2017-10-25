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
package info.bionfweb.libralign.demo.paper;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.io.IOTools;
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;



public class SwingApplication {
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
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the application.
	 * @throws Exception if an I/O error occurs
	 */
	public SwingApplication() throws Exception {
		initialize();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 * @throws Exception if an I/O error occurs
	 */
	private void initialize() throws Exception {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Swing application");
		frame.setLayout(new BorderLayout());
		
		// Read an alignment file:
		List<AlignmentModel<?>> models = IOTools.readAlignments(new File("data/PaperExample.nexml"));
		
		// Create an alignment area displaying the first alignment from the file:
		AlignmentArea area = new AlignmentArea();
		area.setAlignmentModel(models.get(0), false);
		
		// Add the Swing version of alignment area to the GUI:
		frame.getContentPane().add(SwingComponentFactory.getInstance().getSwingComponent(area), BorderLayout.CENTER);
	}
}
