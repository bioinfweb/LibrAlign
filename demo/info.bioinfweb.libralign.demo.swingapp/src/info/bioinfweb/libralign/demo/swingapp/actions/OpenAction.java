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


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.collections4.set.ListOrderedSet;

import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.commons.io.ExtensionFileFilter;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;



@SuppressWarnings("serial")
public class OpenAction extends AbstractFileAction {
	private JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
	private JFileChooser fileChooser = null;
	
	
	public OpenAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Open..."); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	public JFileChooser getOpenFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(true);
			
			ListOrderedSet<String> validExtensions = new ListOrderedSet<String>();
			for (String formatID : factory.getFormatIDsSet()) {
				JPhyloIOFormatInfo info = factory.getFormatInfo(formatID);
				if (info.isElementModeled(EventContentType.ALIGNMENT, true)) {
					ContentExtensionFileFilter filter = info.createFileFilter(TestStrategy.BOTH);
					validExtensions.addAll(filter.getExtensions());
					fileChooser.addChoosableFileFilter(filter);
				}
			}
			ExtensionFileFilter allFormatsFilter = new ExtensionFileFilter("All supported formats", false, validExtensions.asList());
			fileChooser.addChoosableFileFilter(allFormatsFilter);
			fileChooser.setFileFilter(allFormatsFilter);
		}
		return fileChooser;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (handleUnsavedChanges()) {
			try {
				if (getOpenFileChooser().showOpenDialog(getEditor().getFrame()) == JFileChooser.APPROVE_OPTION) {
					JPhyloIOEventReader eventReader = factory.guessReader(getOpenFileChooser().getSelectedFile(), new ReadWriteParameterMap());
					AlignmentDataReader mainReader = new AlignmentDataReader(eventReader, new BioPolymerCharAlignmentModelFactory());
					mainReader.readAll();

					// File does not contain any alignments
					if (mainReader.getAlignmentModelReader().getCompletedModels().size() == 0) {
						JOptionPane.showMessageDialog(getEditor().getFrame(), "The file \"" +  getOpenFileChooser().getSelectedFile().getAbsolutePath() + 
								"\" does not contain any alignments.", "Error while loading file", JOptionPane.ERROR_MESSAGE);
					}
					else {
						// File contains one alignment
						getEditor().getAlignmentArea().setAlignmentModel(mainReader.getAlignmentModelReader().getCompletedModels().get(0), true);
						getEditor().setFile(getOpenFileChooser().getSelectedFile());
						getEditor().setFormat(eventReader.getFormatID());
						getEditor().setChanged(false);

						// File contains more than one alignment --> just the first one was loaded
						if (mainReader.getAlignmentModelReader().getCompletedModels().size() > 1) {
							JOptionPane.showMessageDialog(getEditor().getFrame(),
									"The file contained more than one alignment. Only the first one was loaded.", "Multiple alignments found", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(getEditor().getFrame(), ex.getMessage() + " (" + ex.getClass().getSimpleName() + ")", "Error while loading file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
