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
import java.util.List;

import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.model.ShiftChange;



public class PherogramReference extends PherogramAreaModel {
	private URL url;
	
	
	public PherogramReference(AlignmentModel<?> alignmentModel, PherogramProvider provider, URL url, String sequenceID) {
		super(provider, alignmentModel, sequenceID);
		this.url = url;
	}


	public PherogramReference(AlignmentModel<?> alignmentModel, URL url, String sequenceID, PherogramProviderByURL providerByURL) throws IOException {
		this(alignmentModel, providerByURL.getPherogramProvider(url), url, sequenceID);
	}

	public PherogramReference(AlignmentModel<?> alignmentModel, PherogramProvider provider, URL url, String sequenceID, List<ShiftChange> shiftChangeList) {
		super(provider, alignmentModel,sequenceID, shiftChangeList);
		this.url = url;
	}

	public URL getURL() {
		return url;
	}


	@Override
	public String toString() {
		return getURL().toString();
	}
}
