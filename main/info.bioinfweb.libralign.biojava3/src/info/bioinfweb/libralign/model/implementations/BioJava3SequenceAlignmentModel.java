/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.implementations;

import org.biojava3.alignment.template.AlignedSequence;
import org.biojava3.alignment.template.Profile;
import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.LightweightProfile;
import org.biojava3.core.sequence.template.Sequence;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.SequenceAccessAlignmentModel;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;
import info.bioinfweb.libralign.model.tokenset.BioJava3TokenSet;
import info.bioinfweb.libralign.model.tokenset.TokenSet;



/**
/**
 * An implementation of {@link AlignmentModel} backed by a set of <i>BioJava</i> {@link Sequence} implementations.
 * This class does not allow to write tokens (compounds) since this is not supported by the 
 * {@link Sequence} interface.
 * <p>
 * Although sequences may be added and removed from instances of this class, {@link #addSequence(String)} will
 * add {@code null} as a new sequence. The technical reason is that this class cannot create instance of the 
 * unknown type {@code S}. It would anyway not make much sense to use this method, since the new sequence could 
 * not be edited. If concrete new sequences shall be added, {@link #addSequence(String, Object)} should be used 
 * instead.   
 * 
 * @author Ben St&ouml;ver
 *
 * @param <S> the type of <i>BioJava</i> sequence objects used by the underlying {@link LightweightProfile} 
 * @param <C> the compound type used by the underlying <i>BioJava</i> sequence objects
 */
public class BioJava3SequenceAlignmentModel<S extends Sequence<C>, C extends Compound> 
		extends AbstractUnmodifyableAlignmentModel<S, C> implements SequenceAccessAlignmentModel<S, C> {
	
	/**
	 * Creates a new instance of this class using a possibly shared ID manager.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 */
	public BioJava3SequenceAlignmentModel(TokenSet<C> tokenSet, SequenceIDManager idManager) {
		super(tokenSet, idManager);
	}
	
	
	/**
	 * Creates a new instance of this class using its own ID manager.
	 * 
	 * @param tokenSet - the token set which is supported by the implementation
	 */
	public BioJava3SequenceAlignmentModel(TokenSet<C> tokenSet) {
		this(tokenSet, new SequenceIDManager());
	}
	
	
	/**
	 * Creates a new instance of this class and copies the sequences contained {@code underlyingModel} to it. Later changes
	 * in {@code underlyingModel} will not be reflected by the new instance.
	 * 
	 * @param namePrefix the prefix to be used for sequence names in the new instance (Sequence names will contain this prefix 
	 *        followed by the index of the according sequence in the underlying model. Sequence names may be changes using
	 *        {@link #renameSequence(String, String)} later.)
	 * @param underlyingModel the <i>BioJava</i> class containing the sequences to be added to the new model
	 * @param type the token type of the new instance's token set (Only a discrete type would make sense for this class.)
	 * @param spaceForGap determines whether the space key shall be associated with gap symbol in the token set
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @throws NullPointerException if {@code underlyingModel}, {@code type} or {@code compoundSet} are {@code null}
	 */
	public BioJava3SequenceAlignmentModel(LightweightProfile<S, C> underlyingModel, 
			String namePrefix, CharacterStateSetType setType, boolean spaceForGap, SequenceIDManager idManager) {
		
		this(new BioJava3TokenSet<C>(setType, underlyingModel.getCompoundSet(), spaceForGap));  // Statement thrown an NPE, if underlyingModel would be null.
		
		if (namePrefix == null) {
			namePrefix = "";
		}
		for (int i = 0; i < underlyingModel.getSize(); i++) {
			addSequence(namePrefix + i, underlyingModel.getAlignedSequences().get(i));
		}
	}
	
	
	/**
	 * Creates a new instance of this class and copies the sequences contained {@code underlyingModel} to it. Later changes
	 * in {@code underlyingModel} will not be reflected by the new instance.
	 * 
	 * @param underlyingModel the <i>BioJava</i> class containing the sequences to be added to the new model
	 * @param namePrefix the prefix to be used for sequence names in the new instance (Sequence names will contain this prefix 
	 *        followed by the index of the according sequence in the underlying model. Sequence names may be changes using
	 *        {@link #renameSequence(String, String)} later.)
	 * @param type the token type of the new instance's token set (Only a discrete type would make sense for this class.)
	 * @param spaceForGap determines whether the space key shall be associated with gap symbol in the token set
	 * @throws NullPointerException if {@code underlyingModel}, {@code type} or {@code compoundSet} are {@code null}
	 */
	public BioJava3SequenceAlignmentModel(LightweightProfile<S, C> underlyingModel, 
			String namePrefix, CharacterStateSetType setType, boolean spaceForGap) {
		
		this(underlyingModel, namePrefix, setType, spaceForGap, new SequenceIDManager());
	}
	
	
	/**
	 * Creates a new instance of this class and copies the sequences contained {@code underlyingModel} to it. Later changes
	 * in {@code underlyingModel} will not be reflected by the new instance.
	 * 
	 * @param underlyingModel the <i>BioJava</i> class containing the sequences to be added to the new model
	 * @param namePrefix the prefix to be used for sequence names in the new instance (Sequence names will contain this prefix 
	 *        followed by the index of the according sequence in the underlying model. Sequence names may be changes using
	 *        {@link #renameSequence(String, String)} later.)
	 * @param type the token type of the new instance's token set (Only a discrete type would make sense for this class.)
	 * @param spaceForGap determines whether the space key shall be associated with gap symbol in the token set
	 * @param idManager the ID manager to be used by the new instance (maybe shared among multiple instances) 
	 * @throws NullPointerException if {@code underlyingModel}, {@code type} or {@code compoundSet} are {@code null}
	 */
	public static <C extends Compound> BioJava3SequenceAlignmentModel<AlignedSequence<? extends Sequence<C>, C>, C> 
			newInstanceFromProfile(Profile<Sequence<C>, C> underlyingModel, String namePrefix, CharacterStateSetType setType, 
					boolean spaceForGap, SequenceIDManager idManager) {
		
		BioJava3SequenceAlignmentModel<AlignedSequence<? extends Sequence<C>, C>, C> result = 
				new BioJava3SequenceAlignmentModel<AlignedSequence<? extends Sequence<C>,C>, C>(
						new BioJava3TokenSet<C>(setType, underlyingModel.getCompoundSet(), spaceForGap), idManager);
		
		if (namePrefix == null) {
			namePrefix = "";
		}
		for (int i = 0; i < underlyingModel.getSize(); i++) {
			result.addSequence(namePrefix + i, underlyingModel.getAlignedSequences().get(i));
		}
		return result;
	}
	
	
	/**
	 * Creates a new instance of this class and copies the sequences contained {@code underlyingModel} to it. Later changes
	 * in {@code underlyingModel} will not be reflected by the new instance.
	 * 
	 * @param underlyingModel the <i>BioJava</i> class containing the sequences to be added to the new model
	 * @param namePrefix the prefix to be used for sequence names in the new instance (Sequence names will contain this prefix 
	 *        followed by the index of the according sequence in the underlying model. Sequence names may be changes using
	 *        {@link #renameSequence(String, String)} later.)
	 * @param type the token type of the new instance's token set (Only a discrete type would make sense for this class.)
	 * @param spaceForGap determines whether the space key shall be associated with gap symbol in the token set
	 * @throws NullPointerException if {@code underlyingModel}, {@code type} or {@code compoundSet} are {@code null}
	 */
	public static <C extends Compound> BioJava3SequenceAlignmentModel<AlignedSequence<? extends Sequence<C>, C>, C> 
			newInstanceFromProfile(Profile<Sequence<C>, C> underlyingModel, String namePrefix, CharacterStateSetType setType, 
					boolean spaceForGap) {
		
		return newInstanceFromProfile(underlyingModel, namePrefix, setType, spaceForGap, new SequenceIDManager());
	}
	
		
	@Override
	protected S createNewSequence(String sequenceID, String sequenceName) {
		return null;  //TODO Test, if this produces an exception anywhere.
	}
	
	
	@Override
	public int getSequenceLength(String sequenceID) {
		S sequence = getSequence(sequenceID);
		if (sequence != null) {
			return sequence.getLength();
		}
		else {
			return -1;
		}
	}
	
	
	@Override
	public C getTokenAt(String sequenceID, int index) {
		S sequence = getSequence(sequenceID);
		if (sequence != null) {
			return sequence.getCompoundAt(index + 1);  // BioJava indices start with 1.
		}
		else {
			throw new SequenceNotFoundException(this, sequenceID);
		}
	}
}
