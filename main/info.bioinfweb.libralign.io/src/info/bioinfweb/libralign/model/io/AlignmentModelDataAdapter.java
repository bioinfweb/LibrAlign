package info.bioinfweb.libralign.model.io;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.io.XMLUtils;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoCharDefsNoSetsMatrixDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;



/**
 * An <i>JPhyloIO</i> data adapter that allows writing the contents of an implementation of {@link AlignmentModel}
 * to an implementation of {@link JPhyloIOEventWriter}.
 * <p>
 * Whether the sequence start events links an associated OTU or not can be defined by the constructor parameter
 * {@code linkOTUs}. If OTUs shall be linked, it will be assumed that such an OTU will have an ID consisting of the
 * concatenation of {@link ReadWriteConstants#DEFAULT_OTU_ID_PREFIX} and the sequence ID used in the underlying 
 * {@link AlignmentModel}. If a more complex OTU linking is necessary, the method {@link #getLinkedOTUID(int)} can
 * be overwritten accordingly. 
 * <p>
 * This implementation does not write any metadata associated with sequences or the alignment as a whole. If such data
 * shall be written the methods {@link #writeMetadata(JPhyloIOEventReceiver)} or 
 * {@link #writeSequenceMetadata(JPhyloIOEventReceiver, String)} should be overwritten accordingly. If character 
 * definitions or sets shall be written, the according methods need to be overwritten as well.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> the type of sequence elements (tokens) used by the underlying alignment model object
 */
public class AlignmentModelDataAdapter<T> extends NoCharDefsNoSetsMatrixDataAdapter implements MatrixDataAdapter {
	private static final int MAX_TOKENS_PER_EVENT = 64;
	
	
	private String idPrefix;
	private LinkedLabeledIDEvent startEvent;
	private AlignmentModel<T> model;
	private boolean linkOTUs;
	private TokenSetAdapter<T> tokenSetAdapter;
	

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param idPrefix the ID prefix to be used for all generated <i>JPhyloIO</i> IDs of sequences and token sets.
	 *        (Must be a valid <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param startEvent the <i>JPhyloIO</i> alignment start event to be used
	 * @param model the <i>LibrAlign</i> alignment model which shall be the source for this adapter
	 * @param linkOTUs Specify {@code true} here, if each sequence shall link an OTU or {@code false} otherwise.
	 *        (See {@link #getLinkedOTUID(int)} for details on how OTU IDs are generated.) 
	 */
	public AlignmentModelDataAdapter(String idPrefix, LinkedLabeledIDEvent startEvent, AlignmentModel<T> model, boolean linkOTUs) {
		super();
		if (!XMLUtils.isNCName(idPrefix)) {
			throw new IllegalArgumentException("The ID prefix  (\"" + idPrefix + "\") is not a valid NCName.");
		}
		else if (startEvent == null) {
			throw new NullPointerException("startEvent must not be null.");
		}
		else if (model == null) {
			throw new NullPointerException("model must not be null.");
		}
		else {
			this.idPrefix = idPrefix;
			this.startEvent = startEvent;
			this.model = model;
			this.linkOTUs = linkOTUs;
			tokenSetAdapter = new TokenSetAdapter<T>(idPrefix + "ts_", model.getTokenSet());
		}
	}


	public String getIDPrefix() {
		return idPrefix;
	}


	public boolean isLinkOTUs() {
		return linkOTUs;
	}


	@Override
	public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return startEvent;
	}


	@Override
	public long getSequenceCount(ReadWriteParameterMap parameters) {
		return model.getSequenceCount();
	}


	@Override
	public long getColumnCount(ReadWriteParameterMap parameters) {
		// TODO Determine whether sequences have unequal lengths
		return 0;
	}


	@Override
	public boolean containsLongTokens(ReadWriteParameterMap parameters) {
		return model.getTokenSet().maxRepresentationLength() > 1;
	}


	@Override
	public ObjectListDataAdapter<TokenSetDefinitionEvent> getTokenSets(ReadWriteParameterMap parameters) {
		if (model instanceof ConcatenatedAlignmentModel) {
			throw new InternalError("Support for concatenated alignment models not implemented.");  //TODO Implement when ConcatenatedAlignmentModel is available
		}
		else {
			return tokenSetAdapter;
		}
	}
	
	
	/**
	 * Returns the <i>JPhyloIO</i> sequence ID to be used for the specified sequence ID used in the underlying model.
	 * <p>
	 * This default implementation returns the concatenation of {@link #getIDPrefix()} and {@code sequenceID}. It can be 
	 * overwritten to use a different pattern.
	 * 
	 * @param modelSequenceID the ID of the sequence used in the underlying {@link AlignmentModel} instance
	 * @return the <i>JPhyloIO</i> sequence ID
	 */
	protected String jPhyloIOByModelSequenceID(int modelSequenceID) {
		return idPrefix + modelSequenceID;
	}


	/**
	 * Returns the <i>LibrAlign</i> model sequence ID associated with the specified <i>JPhyloIO</i> sequence ID.
	 * <p>
	 * This default implementation extracts the integer ID that was used to create such an ID using 
	 * {@link #jPhyloIOByModelSequenceID(int)}. If {@link #jPhyloIOByModelSequenceID(int)} was overwritten this method
	 * must be overwritten accordingly.
	 * <p>
	 * Note that this method does not test, if the extracted ID actually exists in the underlying <i>LibrAlign</i>
	 * alignment model.
	 * 
	 * @param jPhyloIOsequenceID the sequence ID used by <i>JPhyloIO</i>
	 * @return the sequence ID used by the underlying alignment model
	 * @throws IllegalArgumentException if no integer ID can be extracted from the specified ID
	 */
	protected int modelByJPhyloIOSequenceID(String jPhyloIOsequenceID) throws IllegalArgumentException {
		try {
			return Integer.parseInt(jPhyloIOsequenceID.substring(idPrefix.length()));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("The specified JPhyloIO sequence ID \"" + jPhyloIOsequenceID + 
					"\" is invalid an cannot be converted to a model ID.", e);
		}
	}


	/**
	 * Returns the ID of the <i>JPhyloIO</i> OTU that shall be linked with the specified sequence.
	 * <p>
	 * This default implementation returns the concatenation of {@link ReadWriteConstants#DEFAULT_OTU_ID_PREFIX} and 
	 * {@code sequenceID} if {@link #isLinkOTUs()} returns {@code true} or {@code null} if {@link #isLinkOTUs()} returns
	 * {@code false}.
	 * <p>
	 * Note that the ID prefix for sequences and token sets, which can be specified in the constructor will not be used 
	 * for IDs creates by this method.
	 * <p>
	 * This method can be overwritten to use a different pattern of OTU linking.
	 * 
	 * @param sequenceID the ID of the sequence used in the underlying {@link AlignmentModel} instance
	 * @return the linked OTU or {@code null} if no OTU shall be linked.
	 */
	protected String getLinkedOTUID(int sequenceID) {
		if (isLinkOTUs()) {
			return ReadWriteConstants.DEFAULT_OTU_ID_PREFIX + sequenceID;
		}
		else {
			return null;
		}
	}


	@Override
	public Iterator<String> getSequenceIDIterator(ReadWriteParameterMap parameters) {
		final Iterator<Integer> iterator = model.sequenceIDIterator();
		return new Iterator<String>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			
			@Override
			public String next() {
				return jPhyloIOByModelSequenceID(iterator.next());
			}


			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}
	
	
	@Override
	public LinkedLabeledIDEvent getSequenceStartEvent(ReadWriteParameterMap parameters, String sequenceID) {
		int modelID = modelByJPhyloIOSequenceID(sequenceID);
		if (model.containsSequence(modelID)) {
			return new LinkedLabeledIDEvent(EventContentType.SEQUENCE, sequenceID, model.sequenceNameByID(modelID), 
					getLinkedOTUID(modelID));
		}
		else {
			throw new IllegalArgumentException("No according sequence to the ID \"" + sequenceID + 
					"\" could be found in the underlying model.");
		}
	}


	@Override
	public long getSequenceLength(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
		return model.getSequenceLength(modelByJPhyloIOSequenceID(sequenceID));
	}
	
	
	/**
	 * Can be used to write metadata associated with a sequence.
	 * <p>
	 * This default implementation is empty, but can be overwritten by inherited classes.
	 * 
	 * @param receiver the receiver to write the events to
	 * @param sequenceID the ID of the sequence carrying the metadata
	 * @throws IOException if an I/O error occurs while {@code receiver} is trying to write the data
	 * @throws IllegalArgumentException if not sequence with the specified ID exists
	 */
	protected void writeSequenceMetadata(JPhyloIOEventReceiver receiver, String sequenceID) 
			throws IOException,	IllegalArgumentException {}  //TODO Automatically write content of linked data models here (if there are such in future versions of AlignmentModel)?


	@Override
	public void writeSequencePartContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String sequenceID, 
			long startColumn, long endColumn) throws IOException, IllegalArgumentException {
		
		int modelID = modelByJPhyloIOSequenceID(sequenceID);
		int sequenceLength = model.getSequenceLength(modelID); 
		if (Math2.isBetween(startColumn, 0, sequenceLength - 1) && Math2.isBetween(endColumn, startColumn, sequenceLength)) {
			if (startColumn == 0) {
				writeSequenceMetadata(receiver, sequenceID);
			}
			
			ArrayList<String> tokens = new ArrayList<String>(MAX_TOKENS_PER_EVENT);
			for (int column = (int)startColumn; column < (int)endColumn; column++) {
				if (tokens.size() >= MAX_TOKENS_PER_EVENT) {
					receiver.add(new SequenceTokensEvent(tokens));
					tokens = new ArrayList<String>(MAX_TOKENS_PER_EVENT);
				}
				tokens.add(model.getTokenSet().representationByToken(model.getTokenAt(modelID, column)));
			}
			if (!tokens.isEmpty()) {
				receiver.add(new SequenceTokensEvent(tokens));
			}
		}
		else {
			throw new IndexOutOfBoundsException("The specified start (" + startColumn + ") or end column (" + endColumn + 
					") was invalid.");  //TODO Allow endColumn to be behind the end of the sequence?  
		}
	}
}
