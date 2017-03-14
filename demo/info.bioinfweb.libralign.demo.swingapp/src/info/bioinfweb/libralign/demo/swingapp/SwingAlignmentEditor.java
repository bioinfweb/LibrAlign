package info.bioinfweb.libralign.demo.swingapp;


import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.tic.SwingComponentFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;



public class SwingAlignmentEditor {
	private JFrame frame;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingAlignmentEditor window = new SwingAlignmentEditor();
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
	public SwingAlignmentEditor() {
		initialize();
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Swing Alignment Editor Demo");
		
		// Create LibrAlign component instance:
		AlignmentArea area = new AlignmentArea();
		area.setAlignmentModel(new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance()), false);
		
		// Create instance specific to Swing:
		JComponent alignmentArea = SwingComponentFactory.getInstance().getSwingComponent(area);
		
		frame.getContentPane().add(alignmentArea, BorderLayout.CENTER);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmAddSequence = new JMenuItem("Add sequence");
		mnEdit.add(mntmAddSequence);
		
		JMenuItem mntmDeleteSequence = new JMenuItem("Delete sequence");
		mnEdit.add(mntmDeleteSequence);
	}
}
