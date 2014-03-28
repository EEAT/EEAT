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
 * PatSet.java
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.5 $
 */

@SuppressWarnings("unused")
public class PatSet extends ArrayList<Pattern> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 554392229042736437L;

	public PatSet() {
		super();
	}

	public void sort(PatternComparator comparator) {
		Collections.sort(this, comparator); 
	}

	/** 指定されたストリームに出力する */
	public void writeFile(PrintStream out) {
		ArrayList<Pattern> plist = this;
		for (int i = 0; i < plist.size(); i++) {
			Pattern pattern = plist.get(i);
			out.println(pattern.toString());
		}
	}

}

/* EOF */
