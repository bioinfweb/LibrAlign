package info.bioinfweb.libralign.demo.swingapp.actions;


import javax.swing.AbstractAction;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;


@SuppressWarnings("serial")
public abstract class AbstractAlignmentEditorAction extends AbstractAction {
	private SwingAlignmentEditor editor;


	public AbstractAlignmentEditorAction(SwingAlignmentEditor editor) {
		super();
		this.editor = editor;
	}	
	
	
	protected SwingAlignmentEditor getEditor() {
		return editor;
	}
}