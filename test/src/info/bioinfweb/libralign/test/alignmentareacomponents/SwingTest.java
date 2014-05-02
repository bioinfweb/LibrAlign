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


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



public class SwingTest {
	private JFrame frame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingTest window = new SwingTest();
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
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel rootArea = new JPanel();
		scrollPane.setViewportView(rootArea);
		rootArea.setLayout(new BoxLayout(rootArea, BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		rootArea.add(panel1);
		
		JPanel panel2 = new JPanel();
		panel2.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						System.out.println("mouseReleased " + e.getX() + " " + e.getY());
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						System.out.println("mousePressed " + e.getX() + " " + e.getY());
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						System.out.println("mouseExited " + e.getX() + " " + e.getY());
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						System.out.println("mouseEntered " + e.getX() + " " + e.getY());
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("mouseClicked " + e.getX() + " " + e.getY());
					}
				});
		panel2.addMouseMotionListener(new MouseMotionListener() {
					@Override
					public void mouseMoved(MouseEvent e) {
						System.out.println("mouseMoved " + e.getX() + " " + e.getY());
					}
					
					@Override
					public void mouseDragged(MouseEvent e) {
						System.out.println("mouseDragged " + e.getX() + " " + e.getY());
					}
				});
		rootArea.add(panel2);
		
		JPanel panel3 = new JPanel();
		rootArea.add(panel3);
	}
}
