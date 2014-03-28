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
 * Created: 08 June 2005
 *
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.5 $
 */

@SuppressWarnings("unused")
public class Pattern {
	/*
	 * 定数宣言
	 */
	public static final int STAR_SYMBOL = '*';

	/*
	 * メンバーフィールド
	 */

	/** パターン本体を表わす記号列． */
	ArrayList<Integer> seq = null; 

	/** パターンの位置出現数 */
	int posCount = Occurrence.UNDEF;

	/** パターンの文書出現数 */
	int docCount = Occurrence.UNDEF;

	Occurrence occ = null;

	/*
	 * 手続き
	 */

	/** 空系列パターンを生成する */
	public Pattern() {
		seq = new ArrayList<Integer>();
	}

	/** 一文字系列パターンを生成する */
	public Pattern(int C) {
		seq = new ArrayList<Integer>();
		seq.add(C);
	}

	/*
	 * Operations as an extension of ArrayList<Integer>
	 */
	public int size() { return seq.size(); }

	public void add(int C) { seq.add(C); }

	public int get(int i) { return seq.get(i); }

	public Pattern copy() {
		Pattern oldpat = this;
		Pattern newpat = new Pattern();
		newpat.seq.addAll(oldpat.seq);
		newpat.posCount = oldpat.posCount;
		newpat.occ = oldpat.occ;
		return newpat; 
	}

	public void setPosCount(int posCount) { this.posCount = posCount; };
	public void setDocCount(int docCount) { this.docCount = docCount; };
	public void setOccurrence(Occurrence occ) { this.occ = occ; };

	public int getPosCount() { return this.posCount; };
	public int getDocCount() { return this.docCount; };

	/** パターン末尾に，gapLength個のギャップ記号*と，
	 *  それに続く1個の定数記号keycharを追加する． */
	public void addGapAndChar(int gapLength, int keychar) {
		for (int i = 0; i < gapLength; i++)
			seq.add(STAR_SYMBOL);
		seq.add(keychar);
	}

	public void subst(int idx, int keychar) {
		if (idx < this.size()) {
			if (seq.get(idx) == STAR_SYMBOL) {
				seq.set(idx, keychar);
			}
			else {
				Log.println("Pattern.subst: cannot substitute for a constant: "+
							" idx="+idx+" keychar="+(char)keychar);
			}
		}
		else {
			while (seq.size() < idx)
				seq.add(STAR_SYMBOL);
			seq.add(keychar);
		}
	}

	/* Utilities */

	public String getSeqString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < seq.size(); i++) {
			int C = seq.get(i); 
			buf.append((char) C);
		}
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("@pattern \t["+this.getSeqString()+"]\n");
		buf.append("@size    \t"+ this.seq.size()+"\n");
		if (this.posCount > Occurrence.UNDEF)
			buf.append("@posCount\t"+this.posCount+"\n");
		if (this.docCount > Occurrence.UNDEF)
			buf.append("@docCount\t"+this.docCount+"\n");
		if (this.occ != null)
			buf.append("@occurrence<<\n"+this.occ+">>\n");
		return buf.toString();
	}
}

/* EOF */
