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
package info.bioinfweb.libralign.demo.swingapp.actions;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;
import info.bioinfweb.libralign.model.io.IOTools;



@SuppressWarnings("serial")
public abstract class AbstractFileAction extends AbstractAlignmentEditorAction {
	private static JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
	private static JFileChooser fileChooser = null;
	
	
	public AbstractFileAction(SwingAlignmentEditor editor) {
		super(editor);
	}
	
	
	protected static JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser() {
			    @Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        JPhyloIOContentExtensionFileFilter filter = (JPhyloIOContentExtensionFileFilter)getFileFilter();
			        if (!filter.accept(f)) {
			        	f = new File(f.getAbsolutePath() + "." + filter.getDefaultExtension());
			        	setSelectedFile(f);
			        }
			        
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
			ReadWriteParameterMap parameters = new ReadWriteParameterMap();
			parameters.put(ReadWriteParameterMap.KEY_APPLICATION_NAME, SwingAlignmentEditor.APPLICATION_NAME);
			parameters.put(ReadWriteParameterMap.KEY_APPLICATION_VERSION, SwingAlignmentEditor.APPLICATION_VERSION);
			parameters.put(ReadWriteParameterMap.KEY_APPLICATION_URL, SwingAlignmentEditor.APPLICATION_URL);
			
			IOTools.writeSingleAlignment(getEditor().getAlignmentArea().getAlignmentModel(), null, getEditor().getFile(), getEditor().getFormat(), parameters);
			// Note that files containing multiple alignments or additional trees or OTU lists would be overwritten with a single alignment file here. 
			// This problem is not handled here, to keep this example simple.
			
			getEditor().setChanged(false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getEditor().getFrame(), ex.getMessage(), "Error while writing file", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	protected boolean save() {
		boolean result = promptFileName();
		if (result) {
			writeFile();
		}
		return result;
	}
	
	
	public boolean handleUnsavedChanges() {
		if (getEditor().isChanged()) {
            switch (JOptionPane.showConfirmDialog(getEditor().getFrame(), "There are unsaved changes. Do you want to save the changes?", 
            		"Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION)) {
            
	            case JOptionPane.YES_OPTION:
	            	return save();
	            case JOptionPane.NO_OPTION:
	            	return true;
            }
    		return false;
		}
		else {
			return true;
		}
	}
}

