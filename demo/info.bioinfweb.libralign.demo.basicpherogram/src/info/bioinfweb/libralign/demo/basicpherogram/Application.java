package info.bioinfweb.libralign.demo.basicpherogram;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.implementations.swingundo.SwingUndoAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.PherogramModelChangeEvent;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;



public class Application {
	private JFrame frame;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
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
	 * @throws IOException 
	 * @throws UnsupportedChromatogramFormatException 
	 */
	public Application() throws UnsupportedChromatogramFormatException, IOException {
		initialize();
	}

	
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws UnsupportedChromatogramFormatException 
	 */
	private void initialize() throws UnsupportedChromatogramFormatException, IOException {
		// Create frame to display the demo:
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// Use BioJava 1 classes to load a pherogram from an SCF file:
		Chromatogram chromatogram = ChromatogramFactory.create(new File ("data/Pherogram.scf")); 
		
		// Create pherogram provider that provides the data loaded into chromatogram to LibrAlign:
		PherogramProvider provider = new BioJavaPherogramProvider(chromatogram);
		
		// Create a pherogram model based on the provider. 
		// (The model contains additional mutable data, e.g., the pherogram cut positions.)
		PherogramAreaModel pherogramModel = new PherogramAreaModel(provider);
		
		// Create alignment model with one empty sequence:
		AlignmentModel<Character> alignmentModel = 
				new PackedAlignmentModel<Character>(CharacterTokenSet.newDNAInstance(true));
		String id = alignmentModel.addSequence("Some sequence");
		
		// Add contents of the base class sequence from the pherogram to the sequence in the alignment:
		for (int j = 0; j < pherogramModel.getPherogramProvider().getSequenceLength(); j++) {
			 alignmentModel.appendToken(id, pherogramModel.getPherogramProvider().getBaseCall(j));
		}

		
		// Create alignment area to display the alignment model contents:
		AlignmentArea alignmentArea = new AlignmentArea();
		alignmentArea.setAlignmentModel(alignmentModel, false);
		
		// Create pherogram data area to display the pherogram raw data:
		DataArea pherogramDataArea = new PherogramArea(alignmentArea.getContentArea(), pherogramModel);
		
		// Attach pherogram data area to the (only) sequence in the alignment area:
		alignmentArea.getDataAreas().getSequenceAreas(id).add(pherogramDataArea);
		
		
		// Create Swing component from the alignment area:
		JComponent swingAlignmentArea = SwingComponentFactory.getInstance().getSwingComponent(alignmentArea);
		
		// Add alignment area to frame:
		frame.getContentPane().add(swingAlignmentArea);
	}
}
