/*
 * PhyDE 2 - An alignment editor for phylogenetic purposes
 * Copyright (C) 2017  Ben Stöver, Jonas Bohn, Kai Müller
 * <http://bioinfweb.info/PhyDE2>
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
package info.bioinfweb.libralign.pherogram.provider;


import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;



public class BioJavaPherogramProviderByURL implements PherogramProviderByURL{
	private static BioJavaPherogramProviderByURL firstInstance = null;
	private Map<String, PherogramProvider> pherogramProviderMap = new TreeMap<String, PherogramProvider>();	

	
	private BioJavaPherogramProviderByURL() {
		super();
	}
	
	
	public static BioJavaPherogramProviderByURL getInstance() {
		if (firstInstance == null) {
			firstInstance = new BioJavaPherogramProviderByURL();
		}
		return firstInstance;
	}
	

	@Override
	public PherogramProvider getPherogramProvider(URL url) throws IOException {
		PherogramProvider pherogramProvider = null;		
		if (pherogramProviderMap.get(url.toString()) == null) {
			try {
				pherogramProvider = new BioJavaPherogramProvider(ChromatogramFactory.create(url.openStream()));
			} 
			catch (UnsupportedChromatogramFormatException e) {
				throw new IOException(e.getLocalizedMessage(), e);
			} 
			pherogramProviderMap.put(url.toString(), pherogramProvider);		
		}
		else {
			pherogramProvider = pherogramProviderMap.get(url.toString());
		}
		return pherogramProvider;
	}
}
