/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.io;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.jphyloio.JPhyloIOEventListener;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.concatenated.ConcatenatedAlignmentModel;
import info.bioinfweb.libralign.model.factory.AlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.NewAlignmentModelParameterMap;
import info.bioinfweb.libralign.model.factory.StringAlignmentModelFactory;
import info.bioinfweb.libralign.model.factory.continuous.DoubleAlignmentModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;



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
	private final AlignmentModelFactory defaultFactory;
	private final Map<CharacterStateSetType, AlignmentModelFactory> factoryMap;
	private AlignmentModel<?> currentModel = null;
	private final List<AlignmentModel<?>> completedModels = new ArrayList<AlignmentModel<?>>();
	private int currentSequenceID = -1; 
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
	public AlignmentModelEventReader(AlignmentModelFactory defaultFactory, Map<CharacterStateSetType, AlignmentModelFactory> factoryMap) {
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
	public AlignmentModelFactory getDefaultFactory() {
		return defaultFactory;
	}


	/**
	 * Returns the map of alignment model factories for different character state (token) set types.
	 *
	 * @return the map of factories
	 * @see #getDefaultFactory()
	 */
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
	public void setNucleotideFactories(AlignmentModelFactory factory) {
		getFactoryMap().put(CharacterStateSetType.NUCLEOTIDE, factory);
		getFactoryMap().put(CharacterStateSetType.DNA, factory);
		getFactoryMap().put(CharacterStateSetType.RNA, factory);
	}


	/**
	 * Returns the alignment model that is currently read. Note that this model might not yet contain all data,
	 * because this data is currently read from the underlying <i>JPhyloIO</i> event stream. As soon as a model
	 * is read completely (an alignment end event if reached) it is added to {@link #getCompletedModels()} and
	 * this method will return {@code null} until the next alignment is reached in the stream.
	 *
	 * @return the current alignment model or {@code null} if the current stream position is not between an
	 *         alignment start and end event
	 */
	public AlignmentModel<?> getCurrentModel() {
		return currentModel;
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
		return completedModels;
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
		if (currentSequenceID < 0) {
			throw new IllegalStateException("A sequence tokens event was encountered ouside a sequence defintion.");  //TODO Replace by other exception? 
		}
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void readTokens(SequenceTokensEvent event) {
		AlignmentModelFactory factory = getAlignmentModelFactory();
		ArrayList<Object> tokens = new ArrayList<Object>(event.getCharacterValues().size());
		for (String stringRepresentation : event.getCharacterValues()) {
			tokens.add(factory.createToken(currentModel, stringRepresentation));
		}
		((AlignmentModel<Object>)currentModel).appendTokens(currentSequenceID, tokens);  //TODO Should currentModel have Object as its generic type?
	}


	@Override
	public void processEvent(JPhyloIOEventReader source, List<JPhyloIOEvent> parentEvents, JPhyloIOEvent event) throws IOException {
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
//			case SINGLE_TOKEN_DEFINITION:
//				if ((currentModel == null) || (currentModel instanceof ConcatenatedAlignmentModel)) {
//					SingleTokenDefinitionEvent definitionEvent = event.asSingleTokenDefinitionEvent(); 
//					currentParameterMap.getDefinedTokens().add(
//							new TokenDefinition(definitionEvent.getTokenName(), definitionEvent.getMeaning()));
//					//TODO Will the character set name be needed for ConcantenatedAlignmentModels?
//				}
//				else {
//					//TODO Possibly throw exception here or just ignore event.
//				}
//				break;
			case TOKEN_SET_DEFINITION:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					//TODO Handle concatenated case (Currently additional token sets are ignored.)
					if (currentParameterMap.getCharacterStateSetType() == null) {
						currentParameterMap.setCharacterStateSetType(event.asTokenSetDefinitionEvent().getSetType());
					}
				}
				break;
			case SEQUENCE:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					LinkedLabeledIDEvent sequenceEvent = event.asLinkedLabeledIDEvent();
					ensureCurrentModelInstance();
					currentSequenceID = currentModel.addSequence(sequenceEvent.getLabel());  //TODO Handle case that no label is present or labels are not unique.
				}
				else {  // END
					currentSequenceID = -1;
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
					currentParameterMap = new NewAlignmentModelParameterMap();
				}
				else {
					if (currentModel != null) {
						completedModels.add(currentModel);
						currentModel = null;
					}
				}
				break;
			default:
			    break;  // Nothing to do
		}
	}
}
