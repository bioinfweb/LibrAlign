package info.bioinfweb.libralign.demo.swingapp;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.demo.swingapp.actions.AddSequenceAction;
import info.bioinfweb.libralign.demo.swingapp.actions.DeleteSequenceAction;
import info.bioinfweb.libralign.demo.swingapp.actions.NewAction;
import info.bioinfweb.libralign.demo.swingapp.actions.OpenAction;
import info.bioinfweb.libralign.demo.swingapp.actions.RemoveGapsAction;
import info.bioinfweb.libralign.demo.swingapp.actions.SaveAction;
import info.bioinfweb.libralign.demo.swingapp.actions.SaveAsAction;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelChangeListener;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;
import info.bioinfweb.tic.SwingComponentFactory;



@SuppressWarnings("serial")
public class SwingAlignmentEditor extends javax.swing.JFrame {
	public static final String APPLICATION_NAME = "LibrAlign Swing Alignment Editor Demo";
	public static final String APPLICATION_VERSION = "0.0.0";
	public static final String APPLICATION_URL = "http://r.bioinfweb.info/LibrAlignSwingDemoApp";
	public static final String DEFAULT_FORMAT = JPhyloIOFormatIDs.NEXML_FORMAT_ID;	
	
	// Create actions:
	private NewAction newAction = new NewAction(this);
	private OpenAction openAction = new OpenAction(this);
	private SaveAction saveAction = new SaveAction(this);
	private SaveAsAction saveAsAction = new SaveAsAction(this);
	
	private AddSequenceAction addSequence = new AddSequenceAction(this);
	private DeleteSequenceAction deleteSequence = new DeleteSequenceAction(this);
	private RemoveGapsAction removeGaps = new RemoveGapsAction(this);	
	
	
	private JFrame frame;
	private AlignmentArea alignmentArea;
	
	private File file = null;
	private String format = DEFAULT_FORMAT;
	private boolean changed = false;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingAlignmentEditor window = new SwingAlignmentEditor();
					//show frame
					window.frame.setVisible(true);
					window.alignmentArea.assignSizeToAll();
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
		super();
		initialize();
	}
	
	
	public JFrame getFrame() {
		return frame;
	}


	public AlignmentArea getAlignmentArea() {
		return alignmentArea;
	}


	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
		refreshWindowTitle();
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	public boolean isChanged() {
		return changed;
	}


	public void setChanged(boolean changed) {
		this.changed = changed;
		refreshWindowTitle();
	}


	private void refreshWindowTitle() {
		StringBuilder title = new StringBuilder();
		title.append(APPLICATION_NAME);
		title.append(" - ");
		if (isChanged()) {
			title.append("*");
		}
		if (getFile() != null) {
			title.append(getFile().getAbsolutePath());
		}
		else {
			title.append("Unsaved");
		}
		frame.setTitle(title.toString());
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		//frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(saveAction.handleUnsavedChanges()) {
					//.handleUnsavedChanges()
				//CurrentDirectoryModel.getInstance().removeFileChooser(getDocument().getFileChooser());
				//ExtendedScrollPaneSelector.uninstallScrollPaneSelector(getScrollPane());
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				}
				else {
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				}
			}
		});
		refreshWindowTitle();
		
		// Create LibrAlign component instance:
		alignmentArea = new AlignmentArea();
		alignmentArea.setAlignmentModel(new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance()), false);
		
		// Register changes listener to know when to ask for saving changes:
		alignmentArea.getAlignmentModel().getChangeListeners().add(new AlignmentModelChangeListener() {
			@Override
			public <T> void afterTokenChange(TokenChangeEvent<T> e) {
				setChanged(true);
			}
			
			@Override
			public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
				setChanged(true);
			}
			
			@Override
			public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
				setChanged(true);
			}
			
			@Override
			public <T, U> void afterProviderChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {}
		});
		
		// Create instance specific to Swing:
		JComponent swingAlignmentArea = SwingComponentFactory.getInstance().getSwingComponent(alignmentArea);
		
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(swingAlignmentArea, BorderLayout.CENTER);
		
		//alignmentArea.getPaintSettings().getTokenPainterList().set(0, new NucleotideTokenPainter());	
		
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		
		// Create file menu:
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mnFile.add(newAction);
		mnFile.add(openAction);
		mnFile.add(saveAction);
		mnFile.add(saveAsAction);
		
		// Create edit menu:
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);			
		
		mnEdit.add(addSequence);
		mnEdit.add(removeGaps);
		mnEdit.add(deleteSequence);		
	}
}
