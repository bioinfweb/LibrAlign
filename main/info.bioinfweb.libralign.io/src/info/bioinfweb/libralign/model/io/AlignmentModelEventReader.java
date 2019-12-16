/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
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
package info.bioinfweb.libralign.model.io;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.factory.StringAlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.TokenDefinition;
import info.bioinfweb.libralign.model.factory.continuous.DoubleAlignmentModelFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;



/**
 * Reads alignment models from a <i>JPhyloIO</i> event stream. Usually instances of this class will not be
 * used directly in application code, but an instance of {@link AlignmentDataReader} would be used instead.
 *
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @see AlignmentDataReader
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class AlignmentModelEventReader implements JPhyloIOEventListener {
	private final AlignmentModelFactory<?> defaultFactory;
	@SuppressWarnings("rawtypes") private final Map<CharacterStateSetType, AlignmentModelFactory> factoryMap;
	private String currentAlignmentID = null;
	private AlignmentModel<?> currentModel = null;
	private final ListOrderedMap<String, AlignmentModel<?>> completedModels = new ListOrderedMap<String, AlignmentModel<?>>();
	private String currentSequenceID = null; 
	private NewAlignmentModelParameterMap currentParameterMap = null;


	/**
	 * Creates a new instance of this class using {@link StringAlignmentModelFactory} as the default factory
	 * and {@link DoubleAlignmentModelFactory} as the factory for continuous data.
	 */
	public AlignmentModelEventReader() {
		this(new StringAlignmentModelFactory());
		getFactoryMap().put(CharacterStateSetType.CONTINUOUS, new DoubleAlignmentModelFactory());
	}


	/**
	 * Creates a new instance of this class with an empty factory map. The map can be populated later on using
	 * {@link #getFactoryMap()}.
	 *
	 * @param defaultFactory the factory to use to create new model instances for character state (token)
	 *        set types which are not defined in the factory map
	 */
	@SuppressWarnings("rawtypes")
	public AlignmentModelEventReader(AlignmentModelFactory<?> defaultFactory) {
		this(defaultFactory, new EnumMap<CharacterStateSetType, AlignmentModelFactory>(CharacterStateSetType.class));
	}


	/**
	 * Creates a new instance of this class.
	 *
	 * @param defaultFactory the factory to use to create new model instances for character state (token) set types
	 *        which are not defined in the factory map
	 * @param factoryMap a map of alignment model factories for different character state (token) set types
	 */
	@SuppressWarnings("rawtypes")
	public AlignmentModelEventReader(AlignmentModelFactory<?> defaultFactory, Map<CharacterStateSetType, AlignmentModelFactory> factoryMap) {
		super();
		this.defaultFactory = defaultFactory;
		this.factoryMap = factoryMap;
	}


	/**
	 * Returns the alignment model factory that is used to create new model instances for character state (token)
	 * set types which are not defined in the factory map.
	 *
	 * @return the default alignment model factory
	 * @see #getFactoryMap()
	 */
	public AlignmentModelFactory<?> getDefaultFactory() {
		return defaultFactory;
	}


	/**
	 * Returns the map of alignment model factories for different character state (token) set types.
	 *
	 * @return the map of factories
	 * @see #getDefaultFactory()
	 */
	@SuppressWarnings("rawtypes")
	public Map<CharacterStateSetType, AlignmentModelFactory> getFactoryMap() {
		return factoryMap;
	}


	/**
	 * Sets the specified map for all nucleotide factories (identified with the keys
	 * {@link CharacterStateSetType#NUCLEOTIDE}, {@link CharacterStateSetType#DNA}, {@link CharacterStateSetType#RNA}).
	 * <p>
	 * Previously defined factories for the according types are replaced by this operation.
	 *
	 * @param factory the alignment model factory to be used for all nucleotide token set types
	 */
	public void setNucleotideFactories(AlignmentModelFactory<?> factory) {
		getFactoryMap().put(CharacterStateSetType.NUCLEOTIDE, factory);
		getFactoryMap().put(CharacterStateSetType.DNA, factory);
		getFactoryMap().put(CharacterStateSetType.RNA, factory);
	}


	/**
	 * Returns the alignment model that is currently read. Note that this model might not yet contain all data,
	 * because this data is currently read from the underlying <i>JPhyloIO</i> event stream. As soon as a model
	 * is read completely (an alignment end event is reached), it is added to {@link #getCompletedModels()} and
	 * this method will return {@code null} until the next alignment is reached in the stream.
	 *
	 * @return the current alignment model or {@code null} if the current stream position is not between an
	 *         alignment start and end event
	 */
	public AlignmentModel<?> getCurrentModel() {
		return currentModel;
	}
	
	
	/**
	 * Returns the <i>JPhyloIO</i> ID of the alignment model that is currently read and returned by 
	 * {@link #getCurrentModel()}.
	 * 
	 * @return the ID or {@code null} if no model is currently read
	 */
	public String getCurrentAlignmentID() {
		return currentAlignmentID;
	}


	/**
	 * Returns the concatenated model that is currently read by this instance.
	 *
	 * @return the concatenated model or {@code null} if the model currently read is not an concatenated model
	 *         or not model currently is read
	 * @see #hasCurrentConcatenatedModel()
	 * @see #getCurrentModel()
	 */
	public ConcatenatedAlignmentModel getCurrentConcatenatedModel() {
		if (getCurrentModel() instanceof ConcatenatedAlignmentModel) {
			return (ConcatenatedAlignmentModel)getCurrentModel();
		}
		else {
			return null;
		}
	}


	/**
	 * Checks if a concatenated alignment model is currently read by this instance.
	 *
	 * @return {@code true} if a concatenated model is currently read, {@code false} if no model is read or the
	 *         current model is not a concatenated instance
	 */
	public boolean hasCurrentConcatenatedModel() {
		return getCurrentModel() instanceof ConcatenatedAlignmentModel;
	}


	/**
	 * Checks whether an alignment model that is currently filled with data from the stream is present.
	 * That would be the case every time the reader is positioned between an alignment start and end event.
	 *
	 * @return {@code true} if an alignment model is currently present, {@code false} otherwise.
	 */
	public boolean hasCurrentModel() {
		return getCurrentModel() != null;
	}


	/**
	 * Returns the list of alignment models that have been completely read from the underlying <i>JPhyloIO</i>
	 * event stream until now.
	 *
	 * @return the list of models (may be empty, but is never {@code null})
	 */
	public List<AlignmentModel<?>> getCompletedModels() {
		return completedModels.valueList();
	}
	
	
	/**
	 * Returns the alignment model associated with the specified <i>JPhyloIO</i> ID. (The ID of the alignment
	 * start event in <i>JPhyloIO</i>.) This method will first test if the currently loading model is
	 * associated with this ID and return it. Otherwise a completed model associated with this ID will be
	 * returned. (Note that the model that is currently loading might not yet be complete.)
	 * 
	 * @param id the <i>JPhyloIO</i> of the alignment model
	 * @return the alignment model or {@code null} is no model associated with the specified ID has yet been 
	 *         started to be loaded
	 */
	public AlignmentModel<?> getModelByJPhyloIOID(String id) {
		if (id == null) {
			return null;
		}
		else if (id.equals(getCurrentAlignmentID())) {
			return getCurrentModel();
		}
		else {
			return completedModels.get(id);
		}
	}


	private AlignmentModelFactory<?> getAlignmentModelFactory() {
		AlignmentModelFactory<?> factory = getFactoryMap().get(currentParameterMap.getCharacterStateSetType());
		if (factory == null) {
			factory = defaultFactory;
		}
		return factory;
	}

	
	private void ensureCurrentModelInstance() {
		// Ensure model instance:
		if (currentModel == null) {
			if (currentParameterMap.getCharacterStateSetType() == null) {
				currentParameterMap.setCharacterStateSetType(CharacterStateSetType.UNKNOWN);
			}
			currentModel = getAlignmentModelFactory().createNewModel(currentParameterMap);
		}
	}
	
	
	private void checkCurrentSequenceID() {
		if (currentSequenceID == null) {
			throw new IllegalStateException("A sequence tokens event was encountered ouside a sequence defintion.");  //TODO Replace by other exception? 
		}
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void readTokens(SequenceTokensEvent event) {
		AlignmentModelFactory factory = getAlignmentModelFactory();
		ArrayList<Object> tokens = new ArrayList<Object>(event.getTokens().size());
		for (String stringRepresentation : event.getTokens()) {
			tokens.add(factory.createToken(currentModel, stringRepresentation));
		}
		((AlignmentModel<Object>)currentModel).appendTokens(currentSequenceID, tokens);  //TODO Should currentModel have Object as its generic type?
	}


	@SuppressWarnings("unchecked")
	@Override
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) {
		switch (event.getType().getContentType()) {
//			case TOKEN_SET_DEFINITION:
//				TokenSetDefinitionEvent tokenSetEvent = event.asTokenSetDefinitionEvent();
//				if (tokenSetEvent.has) {  // concatenated case
//					if (currentModel == null) {
//						//TODO Create new concatenated instance (Can be created here already, because upcoming token definitions belong to the first part model.)
//					}
//					if (!(currentModel instanceof ConcatenatedAlignmentModel)) {
//						throw new IllegalStateException("Character state set for part of concatenated alignment found after global character state definition in the same alignment.");
//					}
//					else {
//						CharacterSetEvent characterSetEvent = charSetEvents.get(tokenSetEvent.getCharacterSetName());
//						if (characterSetEvent == null) {
//							//TODO Throw exception => This should probably be a checked exception. Therefore the signature of processEvent() would have to be changed in the interface.
//						}
//						else {
//							currentParameterMap.put(NewAlignmentModelParameterMap.KEY_START_INDEX, characterSetEvent.getStart());
//							currentParameterMap.put(NewAlignmentModelParameterMap.KEY_END_INDEX, characterSetEvent.getEnd());
//						}
//					}
//				}
//
//				if (currentParameterMap.getCharacterStateSetType() == null) {
//					currentParameterMap.setCharacterStateSetType(tokenSetEvent.getSetType());
//				}
//				else {
//					throw new IllegalStateException("Two global character state sets were defined in the same alignment.");
//				}
//				break;
			case TOKEN_SET_DEFINITION:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					//TODO Handle concatenated case (Currently only the last token set is used.)
					currentParameterMap.setCharacterStateSetType(event.asTokenSetDefinitionEvent().getSetType());
				}
				break;
			case SINGLE_TOKEN_DEFINITION:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					if ((currentModel == null) || (currentModel instanceof ConcatenatedAlignmentModel)) {
						SingleTokenDefinitionEvent definitionEvent = event.asSingleTokenDefinitionEvent(); 
						currentParameterMap.getDefinedTokens().add(
								new TokenDefinition(definitionEvent.getTokenName(), definitionEvent.getMeaning()));
						//TODO Will the character set name be needed for ConcantenatedAlignmentModels?
					}
					else {  // Token set definition encountered, after a model with token set was already created.
						//TODO Possibly throw exception here or just ignore event.
					}
				}
				break;
			case SEQUENCE:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					LinkedLabeledIDEvent sequenceEvent = event.asLinkedLabeledIDEvent();
					ensureCurrentModelInstance();
					currentSequenceID = currentModel.addSequence(sequenceEvent.getLabel()/*, sequenceEvent.getID()*/);  //TODO Handle case that no label is present or labels are not unique.
					//TODO Can't sequences be continued by an additional start event? It that case addSequence() should not be called again. => Create test case e.g. with MEGA or interleaved Nexus.
				}
				else {  // END
					currentSequenceID = null;
				}
				break;
			case SEQUENCE_TOKENS:
				checkCurrentSequenceID();
				readTokens(event.asSequenceTokensEvent());
				break;
			case SINGLE_SEQUENCE_TOKEN:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					checkCurrentSequenceID();
					((AlignmentModel<Object>)currentModel).appendToken(currentSequenceID, 
							((AlignmentModelFactory<Object>)getAlignmentModelFactory()).createToken((AlignmentModel<Object>)currentModel, 
									event.asSingleSequenceTokenEvent().getToken()));  //TODO Should currentModel have Object as its generic type?
				}
				break;
			case ALIGNMENT:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					LabeledIDEvent alignmentEvent = event.asLabeledIDEvent();
					currentParameterMap = new NewAlignmentModelParameterMap();
					currentParameterMap.put(NewAlignmentModelParameterMap.KEY_ALIGNMENT_LABEL, alignmentEvent.getLabel());
					currentAlignmentID = alignmentEvent.getID();
					currentParameterMap.put(NewAlignmentModelParameterMap.KEY_ALIGNMENT_ID, currentAlignmentID);
					ensureCurrentModelInstance();  // Necessary to have current model available for all future events, e.g., when data element readers call getModelByJPhyloIOID().
				}
				else {
					if (currentModel != null) {
						completedModels.put(currentAlignmentID, currentModel);
						currentModel = null;
						currentAlignmentID = null;
					}
				}
				break;
			default:
			    break;  // Nothing to do
		}
	}
}
