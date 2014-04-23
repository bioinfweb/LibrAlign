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
package info.bioinfweb.libralign.demo.swt;


import info.bioinfweb.commons.bio.biojava3.alignment.SimpleAlignment;
import info.bioinfweb.commons.bio.biojava3.alignment.template.Alignment;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentSourceDataType;
import info.bioinfweb.libralign.sequenceprovider.BioJavaSequenceDataProvider;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;



/**
 * Demo application showing the usage of LibrAlign with SWT.
 * 
 * @author Ben St&ouml;ver
 */
public class Main {
	protected Shell shell;
	
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
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
	
	
	private AlignmentArea createAlignmentArea() {
		Alignment<DNASequence, NucleotideCompound> alignment = 
				new SimpleAlignment<DNASequence, NucleotideCompound>();
		alignment.add("Sequence 1", new DNASequence("ATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAGATCGTAG"));
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
		
		Composite alignmentWidget = createAlignmentArea().createSWTWidget(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(alignmentWidget);
		scrolledComposite.setMinSize(alignmentWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
}
