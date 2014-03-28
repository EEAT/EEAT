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
 * LetterDocSet.java: 系列を文書とする文書集合．各トークンはアスキー文字．
 * 
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("unused")
public class LetterDocSet extends DocSet {

	/*
	 * 定数宣言
	 */

	/*
	 * メンバーフィールド
	 */

	/*
	 * 手続き
	 */

	/*
	 * ファイル入力
	 */

	boolean isNewLineLetter(int c) {
		return c == '\n'|| c == '\r'; 
	}

	public void readFile(Reader in)
	{
		// このドキュメント中で読み込んだノードの数
		int numNodeReadInDoc = 0;

		try {
			int c;
			Sequence line = new Sequence();
			if (DocSet.useDebug) System.out.println("@debug: begin reading an input file...");
			while ((c = in.read()) != DocSet.EOF) {
				if (DocSet.useDebug) System.out.print((char)c);
				if (isNewLineLetter(c)) {
					if (line.size() > 0) {
						//Add a line only if it is nonempty.
						docList.add(line);
						line = new Sequence();
					}
				}
				else {
					line.add(c);
				}
			}
			if (DocSet.useDebug) System.out.println("@debug: end reading");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}//readFile

}

/* EOF */
