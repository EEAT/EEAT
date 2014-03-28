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
 * @version $Revision: 1.5 $
 */

@SuppressWarnings("unused")
public class MotFinderNOD extends MotFinderBasic {

	public final String versionString = "MotFinderNOD $Revision: 1.5 $";

	public String getVersionString() { return versionString; } 

	public MotFinderNOD(DocSet docSet) {
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
			//Experimental
			if (this.maxGapLen > -1 && off > this.maxGapLen + 1)
				return;

			for (int keychar : this.occPool.keyList()) {

				//for debug
				if (useDebugExpand)
					System.out.println("@expand"+
							  " oldpattern ["+pattern.getSeqString()+"]"+
							  " by offset="+off+
							  " keychar=["+(char)keychar+"]");
				if (useDebugExpand) System.out.print(occ.toString());

				Occurrence newOcc = update(occ, off, keychar);
				Pattern newPattern = pattern.copy();
				newPattern.addGapAndChar(off - 1, keychar);
				expand(newPattern, newOcc, minSupCount, depth+1);
			} //for keychar
		}//for off
	}

	/** 元のパターンの出現集合から，拡張して得られる新しいパターン
	 *  に対する出現集合を計算する．新しいパターンは，元のパターンの
	 *  末尾に，offset-1個のギャップ記号*と右にoffset文字分の位置に
	 *  1個の定数記号を追加して得られるもの．
	 *  @param oldocc	元の出現集合
	 *  @param offset	追加する定数記号の相対位置（ギャップ記号の数＋１）
	 *  @param keychar	追加する定数記号
	 *  @return 　　	新しい出現集合
	 */
	Occurrence update(Occurrence oldocc, int offset, int keychar) {
		Occurrence newocc = new Occurrence(docSet);

		//iterate over all positions in the oldocc
		for (int sid = 0; sid < oldocc.getSize(); sid++) {
			if (useDebugExpand)
				System.out.println(" @update: sid="+sid+
								 " with oldocc.size()="+oldocc.getSize());

			//sid番目の出現リストを取り出す
			PosList olist = oldocc.get(sid);
			if (olist == null)
				continue; //skip this sid

			//sid番目の系列を取り出す
			Sequence seq  = docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //パターンの出現位置の一つ
				int newpos = oldpos + offset;

				//新しい出現は文書末尾をはみだしてないか？
				if (newpos >= seq.size()) { 
					break; //この文書の残りの処理をスキップする
				}

				//新しい出現をテストする
				int newchar = seq.get(newpos);
				if (newchar == keychar)
					newocc.add(sid, newpos); //新しい出現として格納する
				else
					; //この出現はスキップ
			}//for i
		}//for sid
		return newocc; 
	}//update

}

/* EOF */
