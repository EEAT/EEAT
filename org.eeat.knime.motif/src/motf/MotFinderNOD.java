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

	/** ���Υѥ�����νи����礫�顤��ĥ���������뿷�����ѥ�����
	 *  ���Ф���и������׻����롥�������ѥ�����ϡ����Υѥ������
	 *  �����ˡ�offset-1�ĤΥ���å׵���*�ȱ���offsetʸ��ʬ�ΰ��֤�
	 *  1�Ĥ����������ɲä����������Ρ�
	 *  @param oldocc	���νи�����
	 *  @param offset	�ɲä��������������а��֡ʥ���å׵���ο��ܣ���
	 *  @param keychar	�ɲä����������
	 *  @return ����	�������и�����
	 */
	Occurrence update(Occurrence oldocc, int offset, int keychar) {
		Occurrence newocc = new Occurrence(docSet);

		//iterate over all positions in the oldocc
		for (int sid = 0; sid < oldocc.getSize(); sid++) {
			if (useDebugExpand)
				System.out.println(" @update: sid="+sid+
								 " with oldocc.size()="+oldocc.getSize());

			//sid���ܤνи��ꥹ�Ȥ���Ф�
			PosList olist = oldocc.get(sid);
			if (olist == null)
				continue; //skip this sid

			//sid���ܤη������Ф�
			Sequence seq  = docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //�ѥ�����νи����֤ΰ��
				int newpos = oldpos + offset;

				//�������и���ʸ��������Ϥߤ����Ƥʤ�����
				if (newpos >= seq.size()) { 
					break; //����ʸ��λĤ�ν����򥹥��åפ���
				}

				//�������и���ƥ��Ȥ���
				int newchar = seq.get(newpos);
				if (newchar == keychar)
					newocc.add(sid, newpos); //�������и��Ȥ��Ƴ�Ǽ����
				else
					; //���νи��ϥ����å�
			}//for i
		}//for sid
		return newocc; 
	}//update

}

/* EOF */
