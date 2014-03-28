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

    /** Java ��� DEBUG ���͡� */
    public static final boolean useDebug = false;
    public static final boolean useDebugOccPool = false;
    public static final boolean useDebugExpand  = false;
	public static final boolean useDebugOutput = false;
	public static final boolean useDebugMinsup = false;

	public final String versionString = "MotFinder - Here the name of routine comes";

    /** �����ѥ�����Υ����������¤��ʤ����Ȥ򼨤���� */
    public static boolean useMaxPatSize = false; 

	/** �ѥ�����ȯ�����оݤȤʤ�����ʸ�񽸹� */
	DocSet docSet = null; 

	/** ����ʸ�񽸹��ʸ���и������ɽ */
	OccPool occPool = null; 

    /** ȯ�������ѥ�����Υꥹ�Ȥ��Ǽ����ѥ����󥻥åȡ� */
    PatSet patSet = null;

	/** �ѥ������ɾ���ؿ���׻����륪�֥������� */
	Scorer scorer = null; 

    /** �ѥ�����κ��祵������
	 *  �ǥե���Ȥ����¤ʤ���ɽ�魯 Integer.MAX_VALUE = 2^{31}-1��*/
    int maxPatSize = Integer.MAX_VALUE; 

    /** �ѥ�����κ��祮��å�Ĺ��*/
    int maxGapLen = -1; 

	/** õ�������ѥ��������� */
	int numberOfExpansion = 0;

    /** �����å������٤Ȥ���ʸ�����٤��Ѥ��뤫�ɤ����򼨤��� */
	public boolean useDocCount = false;

    /** �����å����ƥѥ�����Ȱ��˰������٤�ʸ�����٤�������뤫�� */
	public static boolean usePrintCount 	= false; 

    /** �����å����ƥѥ�����Ȱ��˰������٤�ʸ�����٤�������뤫�� */
	public static boolean usePrintOcc	= false;

    /** �׻��β�����������뤫�ɤ����Υ����å���*/
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

    /** Motf�ᥤ��ץ�����¹Ԥ��롥�����ϺǾ������ͤ������͡�
     */
    public PatSet run(double minSupRatio)
    {
        Log.println("@log Minimum_Support_Relative: "+minSupRatio);
		return run(docSet.convertSupRatioToAbsolute(minSupRatio));
	}

    /** Motf�ᥤ��ץ�����¹Ԥ��롥�����ϺǾ������ͤ������͡�
     */
    public abstract PatSet run(int minSupCount); 

	//
	//Utilities
	//

	/** ���Ĥ��ä��ѥ������PatSet����Ͽ���롥
	 */
	void registerPattern(Pattern pattern, Occurrence occ) {
		//�ѥ����󤬸��Ĥ��ä�
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
