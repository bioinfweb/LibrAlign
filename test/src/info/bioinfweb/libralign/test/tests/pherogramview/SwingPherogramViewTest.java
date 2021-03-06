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
package info.bioinfweb.libralign.test.tests.pherogramview;


import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class SwingPherogramViewTest extends AbstractPherogramViewTest {
	private JFrame frame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPherogramViewTest window = new SwingPherogramViewTest();
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
	 */
	public SwingPherogramViewTest() {
		initialize();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Swing PherogramView Test");
		
		try {
			frame.getContentPane().add(SwingComponentFactory.getInstance().getSwingComponent(getPherogramView()), BorderLayout.CENTER);
			
			JMenuBar menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);
			
			JMenu mnTest = new JMenu("Test");
			menuBar.add(mnTest);
			
			JMenuItem mntmReverseComplement = new JMenuItem("Reverse complement");
			mntmReverseComplement.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getPherogramView().getTraceCurveView().getModel().reverseComplement();
				}
			});
			mnTest.add(mntmReverseComplement);
			
			JMenuItem mntmToggleShowProbability = new JMenuItem("Toggle show probability values");
			mntmToggleShowProbability.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getPherogramView().getTraceCurveView().getFormats().toggleShowProbabilityValues();
				}
			});
			mnTest.add(mntmToggleShowProbability);
			
			JMenuItem mntmChangeQualityOutput = new JMenuItem("Change quality output type");
			mntmChangeQualityOutput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getPherogramView().getTraceCurveView().getFormats().changeQualityOutputType();
				}
			});
			mnTest.add(mntmChangeQualityOutput);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
