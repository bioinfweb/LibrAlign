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
package info.bioinfweb.libralign.io.alignment.fasta;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import info.bioinfweb.libralign.io.alignment.AbstractAlignmentReader;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;
import info.bioinfweb.libralign.sequenceprovider.tokenset.TokenSet;



/**
 * Allows to read alignment from FASTA files into a sequence data provider instance.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class FASTAReader extends AbstractAlignmentReader {
	private boolean checkLine(TokenSet<?> tokenSet, String line) {
		for (int charIndex = 0; charIndex < line.length(); charIndex++) {
			if (!tokenSet.contains(tokenSet.tokenByKeyChar(line.charAt(charIndex)))) {
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public boolean[] checkTokenSets(InputStream stream, TokenSet<?>[] tokenSets, int maxSequenceCount) throws Exception {
		// Initialize result:
		boolean[] result = new boolean[tokenSets.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = true;
		}
		
		// Calculate result:
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));  // Can double buffering be avoided?
		String line = reader.readLine();
		int linesRead = 0;
		while ((line != null) && ((linesRead == PARSE_WHOLE_FILE) || (linesRead < maxSequenceCount))) {
			if (line.startsWith(">")) {
				linesRead++;
				line = reader.readLine();  // Read first line with characters.  // TODO Possibly use different implementation to avoid loading long lines into a string.  
				while ((line != null) && !line.startsWith(">")) {
					boolean tokenSetLeft = false;
					
					for (int i = 0; i < tokenSets.length; i++) {
						if (result[i]) {  // Otherwise an invalid token for already found for this set.
							result[i] = result[i] && checkLine(tokenSets[i], line);
							tokenSetLeft = tokenSetLeft || result[i];
						}
					}
					
					if (!tokenSetLeft) {
						return result;  // Return earlier if no token set is left, that could be valid.
					}
					
					line = reader.readLine();					
				}
			}
			else {
				throw new IOException("A fasta file has to start with \">\".");
			}
		}
		return result;
	}

	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void appendLine(SequenceDataProvider provider, int sequenceID, String line) {
		TokenSet<?> set = provider.getTokenSet();
		for (int i = 0; i < line.length(); i++) {
			provider.insertTokenAt(sequenceID, provider.getSequenceLength(sequenceID), set.tokenByKeyChar(line.charAt(i)));
		}
	}
	
	
	@Override
	protected void doRead(BufferedInputStream stream,	SequenceDataProvider<?> provider) throws Exception {
		//TODO Clear current contents?
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));  // Can double buffering be avoided?
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith(">")) {
				String name = line.substring(1);
				int sequenceID = provider.addSequence(name);  //TODO Check is sequence is already present?
				
				line = reader.readLine();  // TODO Possibly use different implementation to avoid loading long lines into a string.  
				while ((line != null) && !line.startsWith(">")) {
					appendLine(provider, sequenceID, line);
					line = reader.readLine();					
				}
			}
			else {
				throw new IOException("A fasta file has to start with \">\".");  // Can only happen in the first iteration.
			}
		}
	}
}
