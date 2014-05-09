/**
 * Contains classes that allow to edit the underlying data source of an implementation of 
 * {@link info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider} using swing edit objects
 * (implementations of {@link javax.swing.undo.UndoableEdit}).
 * <p>
 * Since {@link javax.swing.undo.UndoableEdit} and {@link javax.swing.undo.UndoManager} have not really
 * any Swing specific prerequisites, these classes could also be used to track the undo history in an
 * SWT application.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.1
 */
package info.bioinfweb.libralign.sequenceprovider.implementations.swingundo;