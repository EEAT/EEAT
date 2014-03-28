/*
 * Motf.java
 * Copyright 2005, Hiroki Arimura, Takeaki Uno, all rights reserved.
 * This file contains an implementation of the algorithm "UNOT".
 *
 * @see Efficient enumeration of maximal motifs in a sequence,
 * Hiroki Arimura and Takeaki Uno, Manuscript, Division of
 * Computer Science, * IST, Hokkaido University, 9 April 2005;
 * Modified 18 April 2005.  
 * <http:>
 *
 * This software may be used freely for any purpose. However, when
 * distributed, the original source must be clearly stated, and, 
 * when the source code is distributed, the copiright notice must be
 * retained and any alterations in the code must be clearly marked. 
 * No warranty is given regarding the quality of this software.
 */

package motf; 
import motf.*;
import java.io.*;
import java.util.*;

/**
 * Classname.java
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.4 $
 */
@SuppressWarnings("unused")
public abstract class Scorer {
	public final String versionString = "Scorer - Here comes the name of the subclass";
	public abstract String getVersionString(); 

	public abstract int count(Occurrence occ); 
}

/**
 * 文書頻度をスコアとして返す．
 */
class PosScorer extends Scorer {
	final String versionString = "PosScorer $Revision: 1.4 $";
	public String getVersionString() { return versionString; } 

	DocSet docSet = null; 

	public PosScorer(DocSet docSet) {
		this.docSet = docSet; 
	}

	public int count(Occurrence occ) {
		if (occ == null)
			return 0;
		else 
			return occ.getPosCount(); 
	}
}

/**
 * 文書頻度をスコアとして返す．
 */
class DocScorer extends Scorer {
	final String versionString = "DocScorer $Revision: 1.4 $";
	public String getVersionString() { return versionString; } 

	DocSet docSet = null; 

	public DocScorer(DocSet docSet) {
		this.docSet = docSet; 
	}

	public int count(Occurrence occ) {
		if (occ == null)
			return 0;
		else 
			return occ.getDocCount(); 
	}
}

/* EOF */
