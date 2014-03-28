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
 * Classname.java
 * List occ of List of Positions, where: 
 * For every i = 0,...,occ.size(), occ.get(i) is a list of
 * positions in the i-th document in the docSet.
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.7 $
 */

/** ʸ�񽸹����ʸ���ΰ��֤���ʬ������ݻ�����ǡ�����¤��
 *  ��ʸ���ֹ椴�Ȥΰ��֤Υꥹ�Ȥ򤵤������ꥹ�Ȥˤ��Ƴ�Ǽ���롥
 *  ���֤�ޤޤʤ�ʸ���ֹ���Ф��Ƥϡ�null���Ǽ����ʶ��ꥹ�Ȥ�ġˡ�
 */
@SuppressWarnings({ "unused", "serial" })
public class Occurrence extends ArrayList<PosList> {
    /** ��� DEBUG ���͡� */
	static int UNDEF  	= -1; 
	public static boolean useDebug  	= false; 
	public static boolean useDebugTooLarge = false; 

	static DocSet docSet = null;

	/** ��ʸ���и������и��ꥹ�Ȥ�Ĺ�������¤��������� */
	private int posCount = 0; //This must be zero

	/** ��ʸ��и���������Ͻи����İʾ�ޤ�и��ꥹ�Ȥο���
		���Ȥ�Τ���ζ�null����ȥ꤬����Τǡ�����docSet��
		����ȥ��docSet.size()�ȤϤ��Ȥʤ�Τ���դ��뤳�ȡ� */
	private int docCount = 0; //This must be zero

	public int getPosCount() { return posCount; }

	public int getDocCount() { return docCount; }
	//public int getDocCount() { return this.size(); }

	public int getSize() { return this.size(); }

// 	public int size() {
// 		Log.exit("Occurrence.size(): "+
// 				 "This cannot be used! Use getSize() instead.");
// 		return -1;
// 	}

	public Occurrence(DocSet docSet) {
		super();
		Occurrence.docSet = docSet;
		//Initialization
		for (int i = 0; i < docSet.size(); i++) {
			this.add(null); //a place holder
			//this.add(new PosList()); //a safe version
		}

		if (useDebugTooLarge) {
			Log.println("@debug a new occurrence is created!\n"+
						this.toString());
		}
	}

	/** ������ΤȤ��ʳ��ϡ�������ɲä϶ػߤ��롥
	 *  �и��ꥹ�Ȥο���ʸ�����Ʊ�����ݤ���롥 */
	public void add(int sid) {
		Log.exit("Occurrence.add: Any occ-list cannot be newly added!");
	}

	/** ʸ���ֹ�sid�νи��ꥹ�Ȥ������˰���pos���ɲä��롥 */
	public void add(int sid, int pos) {
		if (useDebug) System.out.print("<"+sid+","+pos+"> ");
		PosList olist = this.get(sid);
		if (olist == null) {
			olist = new PosList();
			this.set(sid,olist); //050609: a bug is removed
			docCount++; //Incremented only once for each sid
		}
		olist.add(pos);
		posCount++; 
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("@occurrence "+getDocCount()+" "+getPosCount()+"\n");
		//buf.append("@occurrence "+this.size()+" "+getPosCount()+"\n");
		for (int i = 0; i < this.size(); i++) {
			PosList olist = this.get(i);
			buf.append(i+" ");
			if (olist != null) {
				buf.append(olist.toString());
			}
			else
				buf.append("null");
			buf.append("\n");
		}
		return buf.toString();
	}


	//Experimental: 
	public void traverse(OccVisitor visitor, int offset) {
		Occurrence oldocc = this; 
		
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
				if (newpos >= seq.size() || newpos < 0) {
					break; //����ʸ���λĤ�νи��򥹥��åס�
				}
				else {
					//ʸ������ƤΤߤǺ�Ȥ���
					int newchar = seq.get(newpos); //���������֤˽и�����ʸ��
					visitor.visit(sid, oldpos, newpos, newchar);
				}
			}//for i (and oldpos)
		}//for sid

	}//occDeliver

	//Experimental: 
	public void traverseStrict(OccVisitor visitor, int offset) {
		Occurrence oldocc = this; 
		
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
				int newchar; 

				//�������и���ʸ���ξü��Ϥߤ����Ƥʤ�����
				if (newpos < 0) {
					//ʸ��γ�¦�ⰷ��
					newchar = (-1)*sid; //�����ʸ��γ�¦��ɽ�魯
					visitor.visit(sid, oldpos, newpos, newchar); 
				}
				else if (newpos >= seq.size()) {
					//ʸ��γ�¦�ⰷ��
					newchar = (-1)*sid - 1; //�����ʸ��γ�¦��ɽ�魯
					//��ա��Ǹ��-1����Τϡ�ź����0���������沽���뤿�ᡥ
					visitor.visit(sid, oldpos, newpos, newchar); 
					break; //����ʸ���λĤ�νи��򥹥��åס�
				}
				else {
					//ʸ�������
					newchar = seq.get(newpos); //���������֤˽и�����ʸ��
					visitor.visit(sid, oldpos, newpos, newchar);
				}
			}//for i (and oldpos)
		}//for sid

	}//occDeliver

	//Very Experimental: 
	public void traverseStrictBreakable(ColumnVisitor visitor, int offset) {
		Occurrence oldocc = this; 
		
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
				int newchar; 

				boolean result; 						
				//�������и���ʸ���ξü��Ϥߤ����Ƥʤ�����
				if (newpos < 0) {
					//ʸ��γ�¦�ⰷ��
					newchar = (-1)*sid; //�����ʸ��γ�¦��ɽ�魯
					result =
						visitor.visitBreakable(sid, oldpos, newpos, newchar);
					//Experimental: Closure�������ѡ�ñ���Ǥʤ����æ��
					if (result == false) return; //Not monotone
				}
				else if (newpos >= seq.size()) {
					//ʸ��γ�¦�ⰷ��
					newchar = (-1)*sid - 1; //�����ʸ��γ�¦��ɽ�魯
					//��ա��Ǹ��-1����Τϡ�ź����0���������沽���뤿�ᡥ
					result = visitor.visitBreakable(sid, oldpos, newpos, newchar); 
					//Experimental: Closure�������ѡ�ñ���Ǥʤ����æ��
					if (result == false) return; //Not monotone
					break; //����ʸ���λĤ�νи��򥹥��åס�
				}
				else {
					//ʸ�������
					newchar = seq.get(newpos); //���������֤˽и�����ʸ��
					result = visitor.visitBreakable(sid, oldpos, newpos, newchar);

					//Experimental: Closure�������ѡ�ñ���Ǥʤ����æ��
					if (result == false) return; //Not monotone
				}
			}//for i (and oldpos)
		}//for sid

	}//occDeliver

}



/* EOF */
