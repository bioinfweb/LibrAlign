/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.io.alignment;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;

import java.io.File;
import java.io.InputStream;



/**
 * Interface to be implemented by all classes reading alignments in different formats into an instance of
 * {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 *
 * @param <T> - the token type to be read
 */
public interface AlignmentReader {
	public static final int PARSE_WHOLE_FILE = -1;
	
	
	/**
	 * Reads alignment data from the specified stream and stores it into {@code provider}. Previous
	 * contents of the specified provider are overwritten.
	 * <p>
	 * The token set of the specified provider must be compatible to the token set
	 * 
	 * @param stream - the stream to read the alignment data from
	 * @param provider - the sequence data provider to take up the alignment
	 * @throws Exception depending on the implementing class
	 */
	public void read(InputStream stream, SequenceDataProvider<?> provider) throws Exception;
	
	/**
	 * Reads alignment data from the specified file and stores it into {@code provider}. Previous
	 * contents of the specified provider are overwritten. 
	 * 
	 * @param file - the file to read the alignment data from
	 * @param provider - the sequence data provider to take up the alignment
	 * @throws Exception IO exceptions thrown while trying to write the file or possibly additional exceptions
	 *         of other types depending on the implementing class
	 */
	public void read(File file, SequenceDataProvider<?> provider) throws Exception;
	
	/**
	 * Reads the specified stream and checks which of the specified token sets contains all tokens found in the read
	 * data.
	 * <p>
	 * You can specify the number of sequences to be read. You could e.g. specify that only the first sequence from
	 * the data source should examined to make checking of large data sets faster, but in such a case it might happen
	 * that not all tokens of the alignment that are not contained in the token set are found.
	 * <p>
	 * Note that reading of the specified stream might be aborted at any position depending on {@code maxSequenceCount}. 
	 * 
	 * @param tokenSets - the token sets to be tested (Must contain at least one element.)
	 * @param maxSequenceCount - the maximum number of sequences to be read (Specify {@link #PARSE_WHOLE_FILE} here
	 *        to check the whole file.)
	 * @return an array with an entry for each specified token set. If a token set contains all tokens that have been read
	 *         from the file the according array element will be set to {@code true} and {@code false} otherwise.
	 * @throws Exception IO exceptions thrown while trying to write the file or possibly additional exceptions
	 *         of other types depending on the implementing class
	 */
	public boolean[] checkTokenSets(InputStream stream, TokenSet<?>[] tokenSets, int maxSequenceCount) throws Exception;
}
