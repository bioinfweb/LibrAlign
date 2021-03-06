package info.bioinfweb.libralign.demo.advancedpherogram;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.implementations.pherogram.PherogramArea;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.CutLeftAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.CutRightAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.DisplayProbabilityValuesAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.DisplayQualityScoresAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.ReverseComplementAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.ShowHideBasecalllinesAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.SwitchInsertOverwriteAction;
import info.bioinfweb.libralign.demo.advancedpherogram.actions.SwitchLeftRightInsertionAction;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.provider.BioJavaPherogramProvider;
import info.bioinfweb.libralign.pherogram.provider.PherogramProvider;
import info.bioinfweb.tic.SwingComponentFactory;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;



public class Application {
	private JFrame frame;
	private PherogramArea pherogramDataArea;
	private AlignmentArea alignmentArea;
	private DisplayQualityScoresAction displayQualityScoresAction = new DisplayQualityScoresAction(this);
	private DisplayProbabilityValuesAction displayProbabilityValuesAction = new DisplayProbabilityValuesAction(this);
	private ShowHideBasecalllinesAction showHideBasecalllinesAction = new ShowHideBasecalllinesAction(this);
	private SwitchLeftRightInsertionAction switchLeftRightInsertionAction = new SwitchLeftRightInsertionAction(this);
	private CutLeftAction cutLeftAction = new CutLeftAction(this);
	private CutRightAction cutRightAction = new CutRightAction(this);
	private SwitchInsertOverwriteAction switchInsertOverwriteAction = new SwitchInsertOverwriteAction(this);
	private ReverseComplementAction reverseComplementAction = new ReverseComplementAction(this);
	
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
		
		
		// Create alignment model with one empty sequence:
		AlignmentModel<Character> alignmentModel = 
				new PackedAlignmentModel<Character>(CharacterTokenSet.newDNAInstance(true));
		String id = alignmentModel.addSequence("Some sequence");
		
		// Use BioJava 1 classes to load a pherogram from an SCF file:
		Chromatogram chromatogram = ChromatogramFactory.create(new File ("data/Pherogram.scf")); 
		
		// Create pherogram provider that provides the data loaded into chromatogram to LibrAlign:
		PherogramProvider provider = new BioJavaPherogramProvider(chromatogram);
		
		// Create a pherogram model based on the provider. 
		// (The model contains additional mutable data, e.g., the pherogram cut positions.)
		PherogramAreaModel pherogramModel = new PherogramAreaModel(provider, alignmentModel, id);
		
		// Add contents of the base class sequence from the pherogram to the sequence in the alignment:
		for (int j = 0; j < pherogramModel.getPherogramProvider().getSequenceLength(); j++) {
			 alignmentModel.appendToken(id, pherogramModel.getPherogramProvider().getBaseCall(j));
		}

		
		// Create alignment area to display the alignment model contents:
		alignmentArea = new AlignmentArea();
		alignmentArea.setAlignmentModel(alignmentModel);
		
		// Create pherogram data area to display the pherogram raw data:
		pherogramDataArea = new PherogramArea(alignmentArea, pherogramModel);
		
		// Attach pherogram data area to the (only) sequence in the alignment area:
		alignmentArea.getDataAreas().getSequenceList(id).add(pherogramDataArea);
		
		
		// Create Swing component from the alignment area:
		JComponent swingAlignmentArea = SwingComponentFactory.getInstance().getSwingComponent(alignmentArea);
		
		// Add alignment area to frame:
		frame.getContentPane().add(swingAlignmentArea);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		
		// Create file menu:
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		mnEdit.add(switchLeftRightInsertionAction);
		mnEdit.add(cutRightAction);
		mnEdit.add(cutLeftAction);
		mnEdit.add(switchInsertOverwriteAction);
		mnEdit.add(reverseComplementAction);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mnView.add(displayQualityScoresAction);
		mnView.add(displayProbabilityValuesAction);
		mnView.add(showHideBasecalllinesAction);
	}


	public PherogramArea getPherogramDataArea() {
		return pherogramDataArea;
	}


	public AlignmentArea getAlignmentArea() {
		return alignmentArea;
	}
	
	public JFrame getFrame(){
		return frame;
	}
}
