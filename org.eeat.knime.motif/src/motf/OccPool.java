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
 * OccPool.java
 *
 * Created: 08 June 2005
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.4 $
 */

/** Ϳ����줿ʸ�񽸹���Ф��ơ�������˸���뤹�٤Ƥ�ʸ���򥭡��Ȥ�������ʸ���νи����ֽ�����ͤȤ��뼭�� */
@SuppressWarnings({ "unused", "serial" })
public class OccPool extends HashMap<Integer,Occurrence> {
    /** ��� DEBUG ���͡� */
	public static boolean useDebug  	= false; 
	public static boolean useDebugLarge  	= false; 

	DocSet docSet = null; 

	/** ���μ�����������롥 */
	public OccPool(DocSet docSet) {
		super();
		this.docSet = docSet;
	}

	/** ����ʸ��������Ѥߥꥹ�Ȥ��֤���
	 *  ����ʸ���ϥ�������ʸ���ξ�������󤵤�Ƥ���. */
	public List<Integer> keyList() {
		HashMap<Integer,Occurrence> hashmap = this;
		Set<Integer> keySet = hashmap.keySet();
		ArrayList<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		return keyList;
	}

	/** ����ʸ�����б������и��ꥹ�Ȥ��֤��� */
	public Occurrence get(int keychar) {
		return super.get(keychar);
	}

	/** ʸ��keychar�򥭡��Ȥ��ơ�keychar�νи����֤�ɽ�魯��<sid,pos>
	 *  ����Ͽ���롥���ΤȤ���ʸ��keychar���б��դ���줿�и����֥�������
	 *  �ˤ����ơ������ֹ�sid�νи��ꥹ�Ȥ�������pos����Ͽ���롥*/ 
	public void putPair(int keychar, int sid, int pos) {
		if (useDebugLarge) Log.println("\n@occpool insertPair:"+
									   " keychar="+(char)keychar+
									   " sid="+sid+" pos="+pos);

		HashMap<Integer,Occurrence> hashmap = this;

		//Lookup
		Occurrence occ = hashmap.get(keychar);

		if (useDebugLarge) Log.println(" -hashmap.get: occ=\n"+
									   occ+"\n -end");

		//Initialization of the keychar-entry
		if (occ == null) {
			if (useDebugLarge)
				Log.println(" -occ=null and call initialization");
			occ = new Occurrence(this.docSet);
			if (useDebug) Occurrence.useDebug = true; //For debug
			hashmap.put(keychar, occ); //debug: Problematic!
		}

		//Insertion
		occ.add(sid, pos);
		if (useDebugLarge)
			Log.println(" -add occ: \t"+sid+" "+pos);
	}

/** Ϳ����줿ʸ�񽸹���������ơ��и����뤹�٤Ƥ�ʸ���򥭡��Ȥ�������ʸ���νи����ֽ�����ͤȤ����б��դ��뼭����ۤ��롥 */
	public void build() {
		ArrayList<Sequence> docList = this.docSet.docList; 
		for (int sid = 0; sid < docList.size(); sid++) {
			Sequence seq = docList.get(sid);
			for (int pos = 0; pos < seq.size(); pos++) {
				//Index pair is <sid, pos> = <i,j>
				int C = seq.get(pos);
				if (useDebug) System.out.print(" "+((char)C));
				putPair(C, sid, pos); 
			}
			if (useDebug) System.out.println(); 
		}
	}

	/** ���ꤵ�줿���ϥ��ȥ꡼������Ƥ���Ϥ��롥 */
	public void writeFile(PrintStream out) {
		for (int keychar : this.keyList()) {
			//for (int keychar : this.keySet()) {

			//Lookup
			Occurrence occ = this.get(keychar);
			if (occ == null)
				Log.exit("error: OccPool.writeFile: occ is null!");
			//Print
			out.print("@key ["+((char)keychar)+"]\n");
			out.print(occ.toString());
		}
	}

	public static String keyListString(List<Integer> list) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for (int keychar : list) {
			buf.append((char)keychar+" ");
		}
		buf.append("]");
		return buf.toString();
	}

}

/* EOF */
