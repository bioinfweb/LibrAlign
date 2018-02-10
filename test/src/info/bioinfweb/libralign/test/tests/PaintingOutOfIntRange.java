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
package info.bioinfweb.libralign.test.tests;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class PaintingOutOfIntRange {
	private JFrame frame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PaintingOutOfIntRange window = new PaintingOutOfIntRange();
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
	public PaintingOutOfIntRange() {
		initialize();
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics graphics) {
				super.paint(graphics);
				
				Graphics2D g = (Graphics2D)graphics;
				double offset = 0.9 * Integer.MAX_VALUE;
				//double offset = 2.0 * Integer.MAX_VALUE;
				System.out.println(Integer.MAX_VALUE);
				System.out.println((long)offset);
				g.translate(0.0, - offset);
				g.draw(new Line2D.Double(5.0, offset + 5.0, 250.0, offset + 250.0));
				
				// => Values larger than Integer.MAX_VALUE are not possible in Graphics2D, even when stored as double values.
				
				// Precision testing:
//				double a = 2.0 * Integer.MAX_VALUE;
//				System.out.println(new DecimalFormat("#.##################").format(a));
//				a = a - 100;
//				System.out.println(new DecimalFormat("#.##################").format(a));
			}
		};
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}
}
