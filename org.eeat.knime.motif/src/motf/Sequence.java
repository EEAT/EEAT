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
 * Created: 08 June 2005
 *
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.3 $
 */

@SuppressWarnings("unused")
public class Sequence extends ArrayList<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3546387685409078506L;

	public String toString() {
		ArrayList<Integer> list = this;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			int C = list.get(i);
			buf.append((char)C);
		}
		return buf.toString();
	}
}

/* EOF */
