/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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
package info.bioinfweb.libralign.dataarea.implementations.consensus;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.AlignmentModelAdapter;
import info.bioinfweb.libralign.model.data.AbstractDataModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



public class ConsensusSequenceModel extends AbstractDataModel {
	private final Map<Integer, List<FractionInfo>> fractionsMap = new TreeMap<Integer, List<FractionInfo>>();
	private final Set<ConsensusSequenceModelListener> listeners = new HashSet<ConsensusSequenceModelListener>();


	public ConsensusSequenceModel(AlignmentModel<?> alignmentModel) {
		super(alignmentModel);
		alignmentModel.getChangeListeners().add(new AlignmentModelAdapter() {
			private void react() {
				fractionsMap.clear();
				fireAfterConsensusUpdated();
			}
			
			@Override
			public <T> void afterTokenChange(TokenChangeEvent<T> e) {
				if (getAlignmentModel() == e.getSource()) {
					react();
				}
			}
			
			@Override
			public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
				if (getAlignmentModel() == e.getSource()) {
					react();
				}
			}
			
			@Override
			public <T, U> void afterModelChanged(AlignmentModel<T> previous, AlignmentModel<U> current) {
				react();
			}
		});
	}


	private <T> String getRepresentation(AlignmentModel<T> model, String sequenceID, int column) {
		if (model.getSequenceLength(sequenceID) > column) {  // Sequences may have different lengths.
			T token = model.getTokenAt(sequenceID, column);
			if (!model.getTokenSet().isGapToken(token)) {
				return model.getTokenSet().representationByToken(token);
			}
		}
		return Character.toString(SequenceUtils.GAP_CHAR);  //TODO Is the missing data token also relevant here?
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List<FractionInfo> getFractions(int column) {
		List<FractionInfo> fractions = fractionsMap.get(column);
		if (fractions == null) {
			fractions = new ArrayList<FractionInfo>();
			
			AlignmentModel model = getAlignmentModel();
			TokenSet tokenSet = model.getTokenSet();
			Iterator<String> iterator = model.sequenceIDIterator();
			if (tokenSet.getType().isNucleotide() || tokenSet.getType().equals(CharacterStateSetType.AMINO_ACID)) {
				Map<Character, Double> frequencies;
				
				if (tokenSet.getType().isNucleotide()) {
					char[] tokens = new char[model.getSequenceCount()];
					int row = 0;
					while (iterator.hasNext()) {
						tokens[row] =	getRepresentation(model, iterator.next(), column).charAt(0);
						row++;
					}
					frequencies = SequenceUtils.nucleotideFrequencies(tokens);
				}
				else {  // Amino acid
					String[] tokens = new String[model.getSequenceCount()];
					int row = 0;
					while (iterator.hasNext()) {
						tokens[row] =	getRepresentation(model, iterator.next(), column);
						row++;
					}
					frequencies = SequenceUtils.aminoAcidFrequencies(tokens);
				}
				
				for (Character c : frequencies.keySet()) {
					fractions.add(new FractionInfo(c.toString(), frequencies.get(c)));
				}
			}
			else {  //TODO Implement special treatment (e.g., calculating the mean value) for discrete values one day?
		  	Map<String, Double> frequencies = new TreeMap<String, Double>();
		  	double sum = 0.0;
		  	while (iterator.hasNext()) {
					String sequenceID = iterator.next();
					if (model.getSequenceLength(sequenceID) > column) {  // Sequences may have different lengths.
						Object token = model.getTokenAt(sequenceID, column);
						if (!tokenSet.isGapToken(token)) {
							String representation = tokenSet.representationByToken(token);
							Double frequency = frequencies.get(representation);
							if (frequency == null) {
								frequency = 0.0;
							}
							frequencies.put(representation, frequency + 1);
							sum += 1;
						}
					}
		  	}
		  	
		  	for (String representation : frequencies.keySet()) {
					fractions.add(new FractionInfo(representation, frequencies.get(representation) / sum));
				}
			}
			
			fractionsMap.put(column, fractions);
		}
		return fractions;
	}
	
	
	/**
	 * Returns the string representation of the most frequent token at the specified column.
	 * It the specified column contains no tokens, the gap representation is returned.
	 * 
	 * @return the string representation of a token from {@link #getLabeledAlignmentModel()}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public String getConsensusToken(int column) {
		List<FractionInfo> fractions = getFractions(column);
		TokenSet tokenSet = getAlignmentModel().getTokenSet();
  	String result = tokenSet.representationByToken(tokenSet.getGapToken());  // This way makes sure that a custom token representation is used. 
  	double max = -1;
  	for (FractionInfo fraction : fractions) {
			if (fraction.getFraction() > max) {
				result = fraction.getRepresentation();
				max = fraction.getFraction();
			}
		}
  	return result;
	}
	
	
	public boolean addListener(ConsensusSequenceModelListener listener) {
		return listeners.add(listener);
	}
	
	
	public boolean removeListener(ConsensusSequenceModelListener listener) {
		return listeners.remove(listener);
	}
	
	
	protected void fireAfterConsensusUpdated() {
		GenericEventObject<ConsensusSequenceModel> event = new GenericEventObject<ConsensusSequenceModel>(this);
		listeners.forEach(listener -> listener.afterConsensusUpdated(event));
	}
}
