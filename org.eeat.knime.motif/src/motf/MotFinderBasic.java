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

    /** Java ��� DEBUG ���͡� */
    static final boolean useDebug = false;
    static final boolean useDebugOccPool = false;
    static final boolean useDebugExpand  = false;
	static final boolean useDebugOutput = false;
	static final boolean useDebugMinsup = false;

    /** �����ѥ�����Υ����������¤��ʤ����Ȥ򼨤���� */
    static boolean useMaxPatSize = false; 

	/*
	 * Procedures
	 */

	public MotFinderBasic(DocSet docSet) {
		super(docSet);
		//this.docSet = docSet; 
	}

    /** Motf�ᥤ��ץ�����¹Ԥ��롥�����ϺǾ������ͤ������͡�
     */
    public PatSet run(int minSupCount)
    {
        Log.println("@log Minimum_Support_Absolute: "+minSupCount);

		//�ѥ����󽸹����������
		this.patSet = new PatSet();

		//ɾ���ؿ������ꤹ��
		if (this.scorer == null)
			this.scorer = new PosScorer(docSet); 
		Log.println("@log Scorer: "+this.scorer.getVersionString());

		//ʸ���νи��������������
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

			//Experimental: ���ٻ޴�������ݤ��ǹԤ�
			if (scorer.count(occ) < minSupCount)
				continue; //skip an in-frequent pattern

			//������1ʸ���ѥ��������������
			Pattern pattern = new Pattern(keychar);

			//õ���Τ���κƵ��ƤӽФ�
			expand(pattern, occ, minSupCount, 0); 
		}
		
        // echo back
        Log.println("@log NumOfExpansion: " + this.numberOfExpansion);
        return this.patSet;
    }

	/** ����ͥ��õ���ˤ��ѥ������õ�����롥
	 *  õ���������ȯ�������ѥ�����ϡ����С��ѿ���this.PatSet�س�Ǽ���롥
	 *  @param pattern	The original "parent" pattern to be expanded
	 *  @param occ  	The occurrence set for the parent pattern
	 *  @param minSupCount 	A minimum support threshold
	 *  @param depth 	An indicator for the recursion depth (not required)
	 */
	abstract void expand(Pattern pattern, Occurrence occ,
				int minSupCount, int depth); 

}

/* EOF */
