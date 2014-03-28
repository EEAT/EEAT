/*
 * Motf - A maximal motif miner for a collection of sequences
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
 * Non OccurrenceDeliver version of MotFinder routine. 
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.6 $
 */

@SuppressWarnings("unused")
public class MotFinderOD extends MotFinderBasic {

	public final String versionString = "MotFinderOD $Revision: 1.6 $";

	public String getVersionString() { return versionString; } 

	static boolean useDebugOD  	= false; 

	public MotFinderOD(DocSet docSet) {
		super(docSet); 
	}

	/** 深さ優先探索によりパターンを探索する． */
	void expand(Pattern pattern, Occurrence occ, 
				int minSupCount, int depth)
	{
		numberOfExpansion++;

		//現在のパターンを検査する．
		if (MotFinderBasic.useMaxPatSize && depth >= this.maxPatSize)
			return;
		if (scorer.count(occ) < minSupCount)
			return; //この枝の探索を停止する

		//見つかったパターンをPatSetへ登録する．
		registerPattern(pattern, occ);

		//拡張を用いて子孫のパターンを再帰的に探索する．		

		for (int off = 1; off <= this.maxPatSize - pattern.size(); off++) {
			//Note: off is the offset of the new constant symbol keychar
			//from the end point of the pattern

			//Experimental
			if (this.maxGapLen > -1 && off > this.maxGapLen + 1)
				return;

			//出現分配技法 (OccurrenceDeliver technique)
			OccPool occPool = MotFinderOD1.occDeliver(occ, off);

			for (int keychar : this.occPool.keyList()) {
				Occurrence newOcc = occPool.get(keychar);
				Pattern newPattern = pattern.copy();
				newPattern.addGapAndChar(off - 1, keychar);
				expand(newPattern, newOcc, minSupCount, depth+1);

			} //for keychar
		}//for off
	} //expand


	//Experimental: 
	/** 出現配分技法．
	 *  与えられた出現集合に対して，オフセット分だけ加算した位置に出現する
	 *	文字を，その文字をキーとし，対応する出現集合を値として格納した出現
	 *	プールを返す．
	 *  現在，memb.raw.datデータでの実験で，NonODの3倍程度の高速化を達成．
	 *  @param oldocc	元の出現集合
	 *  @param offset	位置の増分．正（右方）または負（左方）の値を許す．
	 */
	public static OccPool occDeliver(Occurrence occ, int offset) {
		OccVisitorOD visitor = new OccVisitorOD(Occurrence.docSet);
		occ.traverse(visitor, offset);
		return visitor.getOccPool();
	}
}


class OccVisitorOD extends OccVisitor {

	DocSet docSet = null;
	OccPool occPool = null; 

	OccVisitorOD(DocSet docSet) {
		this.docSet = docSet;
		this.occPool = new OccPool(docSet);
	}

	public void visit(int sid, int oldpos, int newpos, int newchar) {
		this.occPool.putPair(newchar, sid, newpos); 
	}

	OccPool getOccPool() { return occPool; }

}


/* EOF */
