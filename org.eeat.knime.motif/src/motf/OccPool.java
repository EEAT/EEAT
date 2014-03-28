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

/** 与えられた文書集合に対して，その中に現れるすべての文字をキーとし，その文字の出現位置集合を値とする辞書． */
@SuppressWarnings({ "unused", "serial" })
public class OccPool extends HashMap<Integer,Occurrence> {
    /** 定数 DEBUG の値． */
	public static boolean useDebug  	= false; 
	public static boolean useDebugLarge  	= false; 

	DocSet docSet = null; 

	/** 空の辞書を生成する． */
	public OccPool(DocSet docSet) {
		super();
		this.docSet = docSet;
	}

	/** キー文字の整列済みリストを返す．
	 *  キー文字はアスキー文字の昇順に整列されている. */
	public List<Integer> keyList() {
		HashMap<Integer,Occurrence> hashmap = this;
		Set<Integer> keySet = hashmap.keySet();
		ArrayList<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		return keyList;
	}

	/** キー文字に対応した出現リストを返す． */
	public Occurrence get(int keychar) {
		return super.get(keychar);
	}

	/** 文字keycharをキーとして，keycharの出現位置を表わす対<sid,pos>
	 *  を登録する．このとき，文字keycharに対応付けられた出現オブジェクト
	 *  において，系列番号sidの出現リストの末尾にposを登録する．*/ 
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

/** 与えられた文書集合を走査して，出現するすべての文字をキーとし，その文字の出現位置集合を値として対応付ける辞書を構築する． */
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

	/** 指定された出力ストリームに内容を出力する． */
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
