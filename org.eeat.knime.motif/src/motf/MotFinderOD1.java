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
 * @version $Revision: 1.1 $
 */

@SuppressWarnings("unused")
public class MotFinderOD1 extends MotFinderBasic {

	public final String versionString = "MotFinderOD1 $Revision$";

	public String getVersionString() { return versionString; } 

	static boolean useDebugOD  	= false; 

	public MotFinderOD1(DocSet docSet) {
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
			OccPool occPool = MotFinderOD.occDeliver(occ, off);

			for (int keychar : this.occPool.keyList()) {
				Occurrence newOcc = occPool.get(keychar);
				Pattern newPattern = pattern.copy();
				newPattern.addGapAndChar(off - 1, keychar);
				expand(newPattern, newOcc, minSupCount, depth+1);

			} //for keychar
		}//for off
	} //expand


	/** 出現配分技法．
	 *  与えられた出現集合に対して，オフセット分だけ加算した位置に出現する
	 *	文字を，その文字をキーとし，対応する出現集合を値として格納した出現
	 *	プールを返す．
	 *  現在，memb.raw.datデータでの実験で，NonODの3倍程度の高速化を達成．
	 *  @param oldocc	元の出現集合
	 *  @param offset	位置の増分．正（右方）または負（左方）の値を許す．
	 */
	static OccPool occDeliver(Occurrence oldocc, int offset) {
		OccPool occPool = new OccPool(Occurrence.docSet);
		
		//iterate over all positions in the oldocc
		for (int sid = 0; sid < oldocc.getSize(); sid++) {

			//sid番目の出現リストを取り出す
			PosList olist = oldocc.get(sid);
			if (olist == null) continue; //skip this sid
			//sid番目の系列を取り出す
			Sequence seq  = Occurrence.docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //パターンの出現位置の一つ
				int newpos = oldpos + offset;

				//新しい出現は文書の両端をはみだしてないか？
				if (newpos >= seq.size() || newpos < 0) 
					break; //この文書の残りの処理をスキップする

				//辞書への追加
				int newchar = seq.get(newpos); //新しい位置に出現する文字
				occPool.putPair(newchar, sid, newpos); 

			}//for i (and oldpos)
		}//for sid
		return occPool;

	}//occDeliver

}

/* EOF */
