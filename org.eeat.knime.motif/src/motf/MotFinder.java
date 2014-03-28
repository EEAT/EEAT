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
 * @version $Revision: 1.9 $
 */

@SuppressWarnings("unused")
public abstract class MotFinder {

    /** Java 定数 DEBUG の値． */
    public static final boolean useDebug = false;
    public static final boolean useDebugOccPool = false;
    public static final boolean useDebugExpand  = false;
	public static final boolean useDebugOutput = false;
	public static final boolean useDebugMinsup = false;

	public final String versionString = "MotFinder - Here the name of routine comes";

    /** 生成パターンのサイズを制限しないことを示す定数 */
    public static boolean useMaxPatSize = false; 

	/** パターン発見の対象となる入力文書集合 */
	DocSet docSet = null; 

	/** 入力文書集合の文字出現集合の表 */
	OccPool occPool = null; 

    /** 発見したパターンのリストを格納するパターンセット． */
    PatSet patSet = null;

	/** パターンの評価関数を計算するオブジェクト */
	Scorer scorer = null; 

    /** パターンの最大サイズ．
	 *  デフォルトは制限なしを表わす Integer.MAX_VALUE = 2^{31}-1．*/
    int maxPatSize = Integer.MAX_VALUE; 

    /** パターンの最大ギャップ長．*/
    int maxGapLen = -1; 

	/** 探索したパターンの総数 */
	int numberOfExpansion = 0;

    /** スイッチ：頻度として文書頻度を用いるかどうかを示す． */
	public boolean useDocCount = false;

    /** スイッチ：各パターンと一緒に位置頻度と文書頻度を印刷するか． */
	public static boolean usePrintCount 	= false; 

    /** スイッチ：各パターンと一緒に位置頻度と文書頻度を印刷するか． */
	public static boolean usePrintOcc	= false;

    /** 計算の過程を印刷するかどうかのスイッチ．*/
	public static boolean usePrintTrace = false; 

	/*
	 * Procedures
	 */

	public MotFinder(DocSet docSet) {
		this.docSet = docSet; 
	}

	public abstract String getVersionString(); 

	public void setScorer(Scorer scorer) {
		this.scorer = scorer; 
	}

	public void setMaxPatSize(int maxSize) {
		useMaxPatSize = true;
		maxPatSize = maxSize;
		Log.println("@log maxPatSize:\t"+maxSize);
	}

	public void setMaxGapLen(int maxGapLen) {
		this.maxGapLen = maxGapLen; 
		Log.println("@log maxGapLen:\t"+maxGapLen);
	}

	public void setUseDocCount(boolean flag) {
		this.useDocCount = flag; 
	}

    /** Motfメインプログラムを実行する．引数は最小頻度値の相対値．
     */
    public PatSet run(double minSupRatio)
    {
        Log.println("@log Minimum_Support_Relative: "+minSupRatio);
		return run(docSet.convertSupRatioToAbsolute(minSupRatio));
	}

    /** Motfメインプログラムを実行する．引数は最小頻度値の絶対値．
     */
    public abstract PatSet run(int minSupCount); 

	//
	//Utilities
	//

	/** 見つかったパターンをPatSetへ登録する．
	 */
	void registerPattern(Pattern pattern, Occurrence occ) {
		//パターンが見つかった
		this.patSet.add(pattern);
		if (MotFinder.usePrintCount) {
			pattern.setPosCount(occ.getPosCount());
			pattern.setDocCount(occ.getDocCount());
		}

		if (MotFinder.usePrintOcc) 
			pattern.setOccurrence(occ);

		if (useDebugOutput) Log.println("@log expand: enter\n"+
					"pattern "+pattern.toString());

	}

}

/* EOF */
