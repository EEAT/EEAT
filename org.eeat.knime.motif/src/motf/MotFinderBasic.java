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
 * MotfMiner.java
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.6 $
 */

@SuppressWarnings("unused")
public abstract class MotFinderBasic extends MotFinder {

    /** Java 定数 DEBUG の値． */
    static final boolean useDebug = false;
    static final boolean useDebugOccPool = false;
    static final boolean useDebugExpand  = false;
	static final boolean useDebugOutput = false;
	static final boolean useDebugMinsup = false;

    /** 生成パターンのサイズを制限しないことを示す定数 */
    static boolean useMaxPatSize = false; 

	/*
	 * Procedures
	 */

	public MotFinderBasic(DocSet docSet) {
		super(docSet);
		//this.docSet = docSet; 
	}

    /** Motfメインプログラムを実行する．引数は最小頻度値の絶対値．
     */
    public PatSet run(int minSupCount)
    {
        Log.println("@log Minimum_Support_Absolute: "+minSupCount);

		//パターン集合を生成する
		this.patSet = new PatSet();

		//評価関数を設定する
		if (this.scorer == null)
			this.scorer = new PosScorer(docSet); 
		Log.println("@log Scorer: "+this.scorer.getVersionString());

		//文字の出現集合を生成する
		this.occPool = new OccPool(docSet);
		this.occPool.build(); 
		if (useDebugOccPool) {
			Log.println("@log Printing occPool");
			this.occPool.writeFile(System.out);
		}

		for (int keychar : this.occPool.keyList()) {
			Occurrence occ = this.occPool.get(keychar);

			if (occ == null)
				continue; //skip

			//Experimental: 頻度枝刈りを前倒しで行う
			if (scorer.count(occ) < minSupCount)
				continue; //skip an in-frequent pattern

			//新しい1文字パターンを生成する
			Pattern pattern = new Pattern(keychar);

			//探索のための再起呼び出し
			expand(pattern, occ, minSupCount, 0); 
		}
		
        // echo back
        Log.println("@log NumOfExpansion: " + this.numberOfExpansion);
        return this.patSet;
    }

	/** 深さ優先探索によりパターンを探索する．
	 *  探索の途中で発見したパターンは，メンバー変数のthis.PatSetへ格納する．
	 *  @param pattern	The original "parent" pattern to be expanded
	 *  @param occ  	The occurrence set for the parent pattern
	 *  @param minSupCount 	A minimum support threshold
	 *  @param depth 	An indicator for the recursion depth (not required)
	 */
	abstract void expand(Pattern pattern, Occurrence occ,
				int minSupCount, int depth); 

}

/* EOF */
