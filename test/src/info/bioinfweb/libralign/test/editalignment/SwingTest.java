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
package info.bioinfweb.libralign.test.editalignment;


import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class SwingTest extends EditableAlignmentTest {
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
		frame.setTitle("Swing editable alignment test");
		
		frame.getContentPane().add(getAlignmentArea().createSwingComponent(), BorderLayout.CENTER);
		
		JMenuBar mainMenu = new JMenuBar();
		frame.setJMenuBar(mainMenu);
		
		JMenu testMenu = new JMenu("Test");
		mainMenu.add(testMenu);
		
		JMenuItem addPherogramMenuItem = new JMenuItem("Add Pherogram");
		addPherogramMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addPherogramSequence();
					}
				});
		testMenu.add(addPherogramMenuItem);
		
		JMenuItem toggleInsertLeftRightMenuItem = new JMenuItem("Toggle insert left/right");
		toggleInsertLeftRightMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getAlignmentArea().getEditSettings().toggleInsertLeftInDataArea();
			}
		});
		testMenu.add(toggleInsertLeftRightMenuItem);
		
		JMenuItem cutPherogramLeftMenuItem = new JMenuItem("Cut pherogram left");
		cutPherogramLeftMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(getAlignmentArea().getSelection().getFirstRow());
				((PherogramArea)getAlignmentArea().getDataAreas().getSequenceAreas(sequenceID).get(0)).setLeftCutPositionBySelection();
			}
		});
		testMenu.add(cutPherogramLeftMenuItem);
		
		JMenuItem cutPherogramRightMenuItem = new JMenuItem("Cut pherogram right");
		cutPherogramRightMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(getAlignmentArea().getSelection().getFirstRow());
				((PherogramArea)getAlignmentArea().getDataAreas().getSequenceAreas(sequenceID).get(0)).setRightCutPositionBySelection();
			}
		});
		testMenu.add(cutPherogramRightMenuItem);
		
		JMenuItem mntmReverseComplement = new JMenuItem("Reverse complement");
		mntmReverseComplement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(getAlignmentArea().getSelection().getFirstRow());
				((PherogramArea)getAlignmentArea().getDataAreas().getSequenceAreas(sequenceID).get(0)).getModel().reverseComplement();
			}
		});
		testMenu.add(mntmReverseComplement);
		testMenu.addSeparator();
		
		JMenuItem mntmToggleShowProbability = new JMenuItem("Toggle show probability values");
		mntmToggleShowProbability.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(getAlignmentArea().getSelection().getFirstRow());
				((PherogramArea)getAlignmentArea().getDataAreas().getSequenceAreas(sequenceID).get(0)).getFormats().toggleShowProbabilityValues();
			}
		});
		testMenu.add(mntmToggleShowProbability);
		
		JMenuItem mntmChangeQualityOutput = new JMenuItem("Change quality output");
		mntmChangeQualityOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sequenceID = getAlignmentArea().getSequenceOrder().idByIndex(getAlignmentArea().getSelection().getFirstRow());
				((PherogramArea)getAlignmentArea().getDataAreas().getSequenceAreas(sequenceID).get(0)).getFormats().changeQualityOutputType();
			}
		});
		testMenu.add(mntmChangeQualityOutput);
	}
}
