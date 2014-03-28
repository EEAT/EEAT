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

/** 文書集合中の文字の位置の部分集合を保持するデータ構造．
 *  各文書番号ごとの位置のリストをさらに配列リストにして格納する．
 *  位置を含まない文書番号に対しては，nullを格納する（空リストも可）．
 */
@SuppressWarnings({ "unused", "serial" })
public class Occurrence extends ArrayList<PosList> {
    /** 定数 DEBUG の値． */
	static int UNDEF  	= -1; 
	public static boolean useDebug  	= false; 
	public static boolean useDebugTooLarge = false; 

	static DocSet docSet = null;

	/** 総文字出現数．出現リストの長さの総和に等しい． */
	private int posCount = 0; //This must be zero

	/** 総文書出現数．これは出現を一つ以上含む出現リストの数．
		場所とりのための空nullエントリがあるので，配列docSetの
		エントリ数docSet.size()とはことなるので注意すること． */
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

	/** 初期化のとき以外は，系列の追加は禁止する．
	 *  出現リストの数は文書数と同じに保たれる． */
	public void add(int sid) {
		Log.exit("Occurrence.add: Any occ-list cannot be newly added!");
	}

	/** 文書番号sidの出現リストの末尾に位置posを追加する． */
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

			//sid番目の出現リストを取り出す
			PosList olist = oldocc.get(sid);
			if (olist == null) continue; //skip this sid
			//sid番目の系列を取り出す
			Sequence seq  = Occurrence.docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //パターンの出現位置の一つ
				int newpos = oldpos + offset;

				//新しい出現は文書の両端をはみだしてないか？
				if (newpos >= seq.size() || newpos < 0) {
					break; //この文書上の残りの出現をスキップ．
				}
				else {
					//文書の内容のみで作業する
					int newchar = seq.get(newpos); //新しい位置に出現する文字
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

			//sid番目の出現リストを取り出す
			PosList olist = oldocc.get(sid);
			if (olist == null) continue; //skip this sid
			//sid番目の系列を取り出す
			Sequence seq  = Occurrence.docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //パターンの出現位置の一つ
				int newpos = oldpos + offset;
				int newchar; 

				//新しい出現は文書の両端をはみだしてないか？
				if (newpos < 0) {
					//文書の外側も扱う
					newchar = (-1)*sid; //負数は文書の外側を表わす
					visitor.visit(sid, oldpos, newpos, newchar); 
				}
				else if (newpos >= seq.size()) {
					//文書の外側も扱う
					newchar = (-1)*sid - 1; //負数は文書の外側を表わす
					//注意：最後に-1するのは，添え字0を負数で符号化するため．
					visitor.visit(sid, oldpos, newpos, newchar); 
					break; //この文書上の残りの出現をスキップ．
				}
				else {
					//文書の内容
					newchar = seq.get(newpos); //新しい位置に出現する文字
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

			//sid番目の出現リストを取り出す
			PosList olist = oldocc.get(sid);
			if (olist == null) continue; //skip this sid
			//sid番目の系列を取り出す
			Sequence seq  = Occurrence.docSet.get(sid); 

			for (int i = 0; i < olist.size(); i++) {
				int oldpos = olist.get(i); //パターンの出現位置の一つ
				int newpos = oldpos + offset;
				int newchar; 

				boolean result; 						
				//新しい出現は文書の両端をはみだしてないか？
				if (newpos < 0) {
					//文書の外側も扱う
					newchar = (-1)*sid; //負数は文書の外側を表わす
					result =
						visitor.visitBreakable(sid, oldpos, newpos, newchar);
					//Experimental: Closure走査専用：単色でなければ脱出
					if (result == false) return; //Not monotone
				}
				else if (newpos >= seq.size()) {
					//文書の外側も扱う
					newchar = (-1)*sid - 1; //負数は文書の外側を表わす
					//注意：最後に-1するのは，添え字0を負数で符号化するため．
					result = visitor.visitBreakable(sid, oldpos, newpos, newchar); 
					//Experimental: Closure走査専用：単色でなければ脱出
					if (result == false) return; //Not monotone
					break; //この文書上の残りの出現をスキップ．
				}
				else {
					//文書の内容
					newchar = seq.get(newpos); //新しい位置に出現する文字
					result = visitor.visitBreakable(sid, oldpos, newpos, newchar);

					//Experimental: Closure走査専用：単色でなければ脱出
					if (result == false) return; //Not monotone
				}
			}//for i (and oldpos)
		}//for sid

	}//occDeliver

}



/* EOF */
