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

	/** ����ͥ��õ���ˤ��ѥ������õ�����롥 */
	void expand(Pattern pattern, Occurrence occ, 
				int minSupCount, int depth)
	{
		numberOfExpansion++;

		//���ߤΥѥ�����򸡺����롥
		if (MotFinderBasic.useMaxPatSize && depth >= this.maxPatSize)
			return;
		if (scorer.count(occ) < minSupCount)
			return; //���λޤ�õ������ߤ���

		//���Ĥ��ä��ѥ������PatSet����Ͽ���롥
		registerPattern(pattern, occ);

		//��ĥ���Ѥ��ƻ�¹�Υѥ������Ƶ�Ū��õ�����롥		

		for (int off = 1; off <= this.maxPatSize - pattern.size(); off++) {
			//Note: off is the offset of the new constant symbol keychar
			//from the end point of the pattern

			//Experimental
			if (this.maxGapLen > -1 && off > this.maxGapLen + 1)
				return;

			//�и�ʬ�۵�ˡ (OccurrenceDeliver technique)
			OccPool occPool = MotFinderOD.occDeliver(occ, off);

			for (int keychar : this.occPool.keyList()) {
				Occurrence newOcc = occPool.get(keychar);
				Pattern newPattern = pattern.copy();
				newPattern.addGapAndChar(off - 1, keychar);
				expand(newPattern, newOcc, minSupCount, depth+1);

			} //for keychar
		}//for off
	} //expand


	/** �и���ʬ��ˡ��
	 *  Ϳ����줿�и�������Ф��ơ����ե��å�ʬ�����û��������֤˽и�����
	 *	ʸ���򡤤���ʸ���򥭡��Ȥ����б�����и�������ͤȤ��Ƴ�Ǽ�����и�
	 *	�ס�����֤���
	 *  ���ߡ�memb.raw.dat�ǡ����Ǥμ¸��ǡ�NonOD��3�����٤ι�®����ã����
	 *  @param oldocc	���νи�����
	 *  @param offset	���֤���ʬ�����ʱ����ˤޤ�����ʺ����ˤ��ͤ������
	 */
	static OccPool occDeliver(Occurrence oldocc, int offset) {
		OccPool occPool = new OccPool(Occurrence.docSet);
		
		//iterate over all positions in the oldocc
		for (int sid = 0; sid < oldocc.getSize(); sid++) {

			//sid���ܤνи��ꥹ�Ȥ���Ф�
			PosList olist = oldocc.get(sid);
			if (olist == null) continue; //skip this sid
			//sid���ܤη������Ф�
			Sequence seq  = Occurrence.docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //�ѥ�����νи����֤ΰ��
				int newpos = oldpos + offset;

				//�������и���ʸ���ξü��Ϥߤ����Ƥʤ�����
				if (newpos >= seq.size() || newpos < 0) 
					break; //����ʸ��λĤ�ν����򥹥��åפ���

				//����ؤ��ɲ�
				int newchar = seq.get(newpos); //���������֤˽и�����ʸ��
				occPool.putPair(newchar, sid, newpos); 

			}//for i (and oldpos)
		}//for sid
		return occPool;

	}//occDeliver

}

/* EOF */
