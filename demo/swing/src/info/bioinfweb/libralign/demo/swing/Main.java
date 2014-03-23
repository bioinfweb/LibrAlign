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
package info.bioinfweb.libralign.demo.swing;


import info.bioinfweb.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.biojava3.alignment.io.fasta.FastaReader;
import info.bioinfweb.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.sequenceprovider.BioJavaSequenceDataProvider;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;



/**
 * Demo application showing the usage of LibrAlign with Swing.
 * 
 * @author Ben St&ouml;ver
 */
public class Main {
	private JFrame frame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
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
	public Main() {
		initialize();
	}
	
	
	private AlignmentArea createAlignmentArea() {
		Alignment<DNASequence, NucleotideCompound> alignment = 
				new SimpleAlignment<DNASequence, NucleotideCompound>();
		alignment.add("Sequence 1", new DNASequence("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG"));
		alignment.add("Sequence 2", new DNASequence("AT-GTTG"));
		alignment.add("Sequence 3", new DNASequence("AT-GTAG"));
		
		BioJavaSequenceDataProvider<DNASequence, NucleotideCompound> sequenceProvider = 
				new BioJavaSequenceDataProvider<DNASequence, NucleotideCompound>(
						alignment, AlignmentSourceDataType.NUCLEOTIDE);
		
		AlignmentArea result = new AlignmentArea();
		result.setSequenceProvider(sequenceProvider, false);
		return result;
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		AlignmentArea alignmentArea = createAlignmentArea();
		JComponent upperAlignmentPanel = alignmentArea.createSwingComponent();
		scrollPane.setViewportView(upperAlignmentPanel);
	}
}
