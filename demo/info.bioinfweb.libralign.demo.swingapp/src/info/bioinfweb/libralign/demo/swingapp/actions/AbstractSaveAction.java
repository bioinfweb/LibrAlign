package info.bioinfweb.libralign.demo.swingapp.actions;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;
import info.bioinfweb.libralign.model.io.IOTools;



@SuppressWarnings("serial")
public abstract class AbstractSaveAction extends AbstractAlignmentEditorAction {
	private static JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
	private static JFileChooser fileChooser = null;
	
	
	public AbstractSaveAction(SwingAlignmentEditor editor) {
		super(editor);
	}
	
	
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser() {
			    @Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        if(f.exists()) {
			            switch(JOptionPane.showConfirmDialog(this, "The file already exists. Do you want to overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION)) {
			                case JOptionPane.YES_OPTION:
			                    super.approveSelection();
			                    return;
			                case JOptionPane.NO_OPTION:
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    cancelSelection();
			                    return;
			            }
			        }
			        super.approveSelection();
			    }        
			};
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			for (String formatID : factory.getFormatIDsSet()) {
				JPhyloIOFormatInfo info = factory.getFormatInfo(formatID);
				if (info.isElementModeled(EventContentType.ALIGNMENT, true)) {
					JPhyloIOContentExtensionFileFilter filter = info.createFileFilter(TestStrategy.BOTH);
					fileChooser.addChoosableFileFilter(filter);
				}
			}
		}
		return fileChooser;
	}
	
	
	protected boolean promptFileName() {
		boolean result = (getFileChooser().showSaveDialog(getEditor().getFrame()) == JFileChooser.APPROVE_OPTION);
		if (result) {
	    	getEditor().setFile(getFileChooser().getSelectedFile());
	    	getEditor().setFormat(((JPhyloIOContentExtensionFileFilter)getFileChooser().getFileFilter()).getFormatID());
		}
		return result;
	}
	
	
	protected void writeFile() {
		try {
			IOTools.writeSingleAlignment(getEditor().getAlignmentArea().getAlignmentModel(), null, getEditor().getFile(), getEditor().getFormat());
			// Note that files containing multiple alignments or additional trees or OTU lists would be overwritten with a single alignment file here. 
			// This problem is not handles here, to keep this example simple.
			
			getEditor().setChanged(false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getEditor().getFrame(), ex.getMessage(), "Error while writing file", JOptionPane.ERROR_MESSAGE);
		}
	}
}
