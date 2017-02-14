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


import info.bioinfweb.libralign.pherogram.PherogramFormats.QualityOutputType;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;
import info.bioinfweb.libralign.pherogram.view.PherogramHeadingView;
import info.bioinfweb.libralign.pherogram.view.PherogramTraceCurveView;
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;



public class SwingPherogramHeadingViewTraceCurveTest extends AbstractPherogramViewTest {
	private PherogramTraceCurveView pherogramView = null;
	private JFrame frame;
	
	
	protected PherogramTraceCurveView getPherogramComponent() throws UnsupportedChromatogramFormatException, IOException {
		if (pherogramView == null) {
			pherogramView = new PherogramTraceCurveView();
//		pherogramView.setProvider(new BioJavaPherogramProvider(ChromatogramFactory.create(
//		new File("data\\pherograms\\Test_pel1PCR_Pel2Wdhg_PCR-7-A_1.ab1"))));
  		pherogramView.getModel().setProvider(new BioJavaPherogramProvider(ChromatogramFactory.create(
	      	new File("data\\pherograms\\Test_qualityScore.scf"))));
			pherogramView.setHorizontalScale(1);			
			pherogramView.setVerticalScale(100);
			pherogramView.getFormats().setQualityOutputType(QualityOutputType.ALL);
			pherogramView.getFormats().setShowProbabilityValues(true);
		}
		return pherogramView;
	}

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPherogramHeadingViewTraceCurveTest window = new SwingPherogramHeadingViewTraceCurveTest();
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
	public SwingPherogramHeadingViewTraceCurveTest() {
		initialize();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Swing PherogramTraceCurveView and PherogramHeadingView Test");
		
		try {
			JScrollPane scrollPane = new JScrollPane();
			SwingComponentFactory factory = SwingComponentFactory.getInstance();
			scrollPane.setViewportView(factory.getSwingComponent(getPherogramComponent()));
			scrollPane.setColumnHeaderView(factory.getSwingComponent(new PherogramHeadingView(getPherogramComponent())));

			frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
