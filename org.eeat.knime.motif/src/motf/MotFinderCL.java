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
public class MotFinderCL extends MotFinder {

	static boolean useDebugOD  	= false; 
	static boolean useDebugPP  	= false; 

	public final String versionString = "MotFinderCL $Revision$";

	public String getVersionString() { return versionString; } 

	public MotFinderCL(DocSet docSet) {
		super(docSet); 
	}

    /** Motfメインプログラムを実行する．引数は最小頻度値の絶対値．
	 *  MotFinderクラスの手続きを上書きする．
     */
    public PatSet run(int minSupCount)
    {
		//トレースの印刷の設定
		if (MotFinder.usePrintTrace) {
			useDebugOD = useDebugPP = true;
		}

        Log.println("@log Minimum_Support_Absolute: "+minSupCount);

		if (useDebugOD) Log.println(" [run   ] start mining...");

		//パターン集合を生成する
		this.patSet = new PatSet();

		//評価関数を設定する
		if (this.scorer == null)
			this.scorer = new PosScorer(docSet); 
		Log.println("@log Scorer: "+this.scorer.getVersionString());

		//文字の出現集合を生成する
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

			//Experimental: 頻度枝刈りを前倒しで行う
			if (scorer.count(occ) < minSupCount)
				continue; //skip an in-frequent pattern

			//新しい1文字パターンを生成する
			Pattern pattern = new Pattern(keychar);

			//探索のための再起呼び出し．対<keychar, coreidx>を手続きに渡すことで，
			//パターン生成を遅延している．
			int coreidx = 0; //The core index for the keychar
			expand(pattern, occ, keychar, coreidx, minSupCount, 0); 
		}
		
		if (useDebugOD) Log.println(" [run   ] done.\n");

        // echo back
        Log.println("@log NumOfExpansion: " + this.numberOfExpansion);

        return this.patSet;

    }//run

	// Utilities
	//
	private String repoCall(int depth)
	{
		return (" [expand] callid="+numberOfExpansion+" depth="+depth+" ");
	}

	private String repoExpd(Pattern pattern, int keychar, int coreidx)
	{
		return (String.format(" pattern=[%s] with coreidx=%d keychar=[%c]",
							  pattern.getSeqString(), coreidx, keychar));
	}

	private String repoSeed(int keychar, int coreidx)
	{
		return (String.format(" coreidx=%d keychar=[%c]", coreidx, keychar));
	}

	private String repoPatt(Pattern pattern)
	{
		return (String.format(" pattern=[%s]", pattern.getSeqString()));
	}

	/** 深さ優先探索によりパターンを探索する．パターン生成の遅延をして，
		再帰をしている． 
		技術的注意として，頻出パターン発見手続きと手続きの公正に互換性を持たせるために，再帰の位置が論文とずれていることに注意．パターン生成は，閉包計算により行う．
		@param prePattern	閉パターンに種となる定数記号を一つ加えた種パターン．
		@param preOcc    	種パターンの出現（明示的には計算されていない）
		@param keychar   	追加した定数記号
		@param coreidx   	追加した定数記号の位置
		@param minSupCount	最小頻度値の絶対値．マイニングパラメータ．
		@param depth     	再帰の深さを表わす非負整数．トップレベルが0.
	*/
	void expand(Pattern prePattern, Occurrence preOcc, int keychar, int coreidx, 
				int minSupCount, int depth)
	{
		/**********************************************
         * ステップ．種パターンから閉パターンを求める
         **********************************************/

		//種パターン（閉パターンに種となる定数記号を一つ加えたもの）
		//であるprePatternを受け取る

		numberOfExpansion++;
		if (useDebugOD) Log.println("\n"+ repoCall(depth));
		if (useDebugOD) Log.println(" [expand] "+
	       "@process "+repoPatt(prePattern)+
		   " with"+repoSeed(keychar, coreidx)+" a new occ...");
			
		//種パターンの接頭辞保存性をテスト
		//coreidx から左を走査
		if (useDebugOD) Log.println(" [expand] "+"@prefixcheck for"+repoExpd(prePattern, keychar, coreidx));
		if (isPrefixPreserving(prePattern, preOcc, coreidx) == false) {
			return; //種パターンの計算を中止．
		}

		//種パターンの接尾辞閉包により閉パターンとその出現を計算．
		//coreidx から右を走査
		if (useDebugOD) Log.println(" [expand] "+"@take closure of"+
			  repoExpd(prePattern, keychar, coreidx));
		Pattern pattern = closure(prePattern.copy(), preOcc, coreidx);
		Occurrence occ = shiftOcc(preOcc, pattern.size() - prePattern.size());
		if (useDebugOD) Log.println(" [expand] "+"@closed "+
		   repoPatt(pattern));

		/**********************************************
         * ステップ．閉パターンの検査と出力
         **********************************************/

		//パターンの検査：最大定数記号数
		if (MotFinder.useMaxPatSize && depth >= this.maxPatSize) {
			if (useDebugOD) Log.println("@pruned by maxexpand: "+
										" depth="+depth+
										": "+repoPatt(pattern));
			return;
		}

		//パターンの検査：最小頻度
		if (scorer.count(occ) < minSupCount) {
			if (useDebugOD) Log.println("@pruned by minsup:"+
										" score="+scorer.count(occ)+
										": "+repoPatt(pattern));
			return; //この枝の探索を停止する
		}

		//見つかったパターンをPatSetへ登録する．
		if (useDebugOD) Log.println(" [expand] "+"@register pattern: "+
									repoPatt(pattern));
		registerPattern(pattern, occ);

		/**********************************************
         * ステップ．子孫のパターンを再帰的に拡張を用いて探索する．		
         **********************************************/

		//For all left part common to the positions of the pattern: 
		if (useDebugOD) Log.println(" [expand] "+"@iterate for children..."); 
		for (int idx = coreidx + 1; idx < this.maxPatSize; idx++) {

			//定数記号の位置をスキップする
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; //skip a constant position
			}

			//出現分配技法 (OccurrenceDeliver technique)
			//oldOcc中の位置を分類する．
			int off = idx - pattern.size() + 1;
			OccPool occPool = occDeliverOrigOcc(occ, off);

			if (useDebugOD) Log.println(" [expand] "+"@collect symbols: "+
			   "  keychars="+OccPool.keyListString(occPool.keyList())); 

			//分類された位置集合ごとに処理
			for (int seedchar : this.occPool.keyList()) {
				Occurrence newocc = occPool.get(seedchar);

				//Note: A bug of occPool: This code should be remove in future.
				if (newocc == null)
					continue;
				else if (newocc.size() == 0)
					continue;

				//Experimental: GapLen-Bounded Rigid Patterns
				if (this.maxGapLen > -1 && off > this.maxGapLen + 1)
					return;

				if (useDebugOD) Log.println(" [expand] "+"@call for a child with seed="+(char)seedchar);
				//子となる種パターンへの再帰呼び出し
				expand(pattern, newocc, seedchar, idx, minSupCount, depth+1);

			} //for seedchar
		}//for off
	} //expand

	private String repoPPC(int idx, int off)
	{
		return (String.format(" idx=%3d off=%3d", idx, off));
	}

	enum ColState {ONE, MANY, BOUNDARY}; 

	boolean isIdxOfConstantSymbol(Pattern pattern, int idx) {
		return (idx >= 0 &&
				idx < pattern.size() &&
				pattern.get(idx) != Pattern.STAR_SYMBOL);
	}

	boolean isPrefixPreserving(Pattern pattern, Occurrence occ, int newIdx) {

		for (int idx = newIdx - 1; true; idx--) {
			int off = idx - pattern.size() + 1; 

			//Experimental: GapLen-Bounded Rigid Patterns
			//最大ギャップ間隔より離れた位置から先は拡張しない
			if (idx < 0 && this.maxGapLen > -1 && //パターン左端から左方の位置
				-1*(idx + 1) > this.maxGapLen) {  //ギャップ長がmaxGapLenを超えている
				if (useDebugOD) Log.println(" [prefix] @stop scanning by maxGapLen: "+this.maxGapLen);
				return true; //端点にぶつかった
			}

			//定数記号の位置をスキップする
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; //skip a constant position
			}

			if (useDebugPP) Log.println(" [prefix] @column check for"+repoPPC(idx,off));

			//コラムを垂直走査して，その位置の文字を調べる
			ColumnVisitor tester = checkMonotoneColumn(occ, off); 
			ColState state = tester.isMonotoneColumn();
			if (useDebugPP) Log.println(" [column] @colstate="+state);

			//ある入力文書の端にぶつかった
			if (state == ColState.BOUNDARY) {
				if (useDebugPP) Log.println(
				" [prefix] @result: prefix-preserving YES!");
				return true;
			}
			//少なくとも一つ単色コラムが見つかった
			else if (state == ColState.ONE) {
				if (useDebugPP) Log.println(
				   " [prefix] @result: prefix-preserving NO!");
				return false;
			}
			else { //state == ColState.MANY
				continue;
			}
		}
	}

	ColumnVisitor checkMonotoneColumn(Occurrence occ, int off) {
		if (useDebugPP) Log.println(" [column] @scan occ...");
		ColumnVisitor visitor = new ColumnVisitor();
		occ.traverseStrict(visitor, off);
		return visitor; 
	}

	/** Experimental */
	ColumnVisitor checkMonotoneColumnBreakable(Occurrence occ, int off) {
		if (useDebugPP) Log.println(" [column] @scan occ...");
		ColumnVisitor visitor = new ColumnVisitor();
		occ.traverseStrictBreakable(visitor, off);
		return visitor; 
	}

	Pattern closure(Pattern pattern, Occurrence occ, int newIdx) {
		int len = pattern.size(); 
		int lastIdx = newIdx; //先に見つけた単色カラム位置

		//キー文字の挿入位置とそれより右方のすべての位置について閉包をとる．
		//注意：遅延計算により，キー文字の代入をこの閉包計算で行うために，
		//キー文字の位置newIdxを含めていることに注意．
		for (int idx = newIdx; true; idx++) {
			//Note: It is critical to start from newIdx but from newIdx+1.
			//For the lazy evaluation of inserting keychar.
			int off = idx - len + 1; 

			//定数記号の位置をスキップする
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; 
			}

			//Experimental: GapLen-Bounded Rigid Patterns
			//最大ギャップ間隔より離れた位置から先は拡張しない
			if (this.maxGapLen > -1 && idx - lastIdx > this.maxGapLen + 1) {
				if (useDebugOD) Log.println(" [closur] @stop scanning by maxGapLen: "+this.maxGapLen);
				break; //残りの位置をスキップする
			}

			if (useDebugOD) Log.println(" [closur] @check column"+repoPPC(idx,off));

			//コラムを垂直走査して，その位置の文字を調べる
			//ColumnVisitor tester = checkMonotoneColumn(occ, off); 
			ColumnVisitor tester = checkMonotoneColumnBreakable(occ, off); //Exp
			ColState state = tester.isMonotoneColumn();
			if (useDebugPP) Log.println(" [closur] @colstate="+state);

			//ある入力文書の端にぶつかった
			if (state == ColState.BOUNDARY) {
				break; //exit the loop
			}
			//少なくとも一つ単色コラムが見つかった
			else if (state == ColState.ONE) {
				int newchar = tester.getLastLetter();
				if (useDebugPP) Log.println(
				  String.format(" [closur] @subst idx=%d newchar=%c",idx,newchar));
				pattern.subst(idx, newchar);
				lastIdx = idx; 
				continue; 
			}
			else { //state == ColState.MANY
				continue;
			}
		}
		return pattern;
	}

	Occurrence shiftOcc(Occurrence occ, int offset) {
		ShiftVisitor visitor = new ShiftVisitor(occ);
		occ.traverseStrict(visitor, offset);
		return visitor.getOcc();
	}

	/** 出現配分技法．
	 *  与えられた出現集合に対して，オフセット分だけ加算した位置に出現する
	 *	文字をキーとし，対応する元の出現集合を値として格納した出現
	 *	プールを返す．
	 *  新しい位置の文字をキーとして，元の位置を分類することに注意
	 *  （MotFinderOD.occDeliver()とはことなる）．
	 *  @param oldocc	元の出現集合
	 *  @param offset	位置の増分．正（右方）または負（左方）の値を許す．
	 */
	public OccPool occDeliverOrigOcc(Occurrence occ, int offset) {
		OrigOccDeliverVisitor visitor = new OrigOccDeliverVisitor(docSet);
		occ.traverseStrict(visitor, offset);
		return visitor.getOccPool();
	}

}

/** LD=LetterDict */
class ColumnVisitor extends OccVisitor {

	HashSet<Integer> letterDict = null;
	boolean isBoundary = false;
	int lastLetter = -1;

	ColumnVisitor() {
		this.letterDict = new HashSet<Integer>();
	}

	private String repoNewChar(int sid, int newpos, int newchar) {
		if (newchar < 0)
			return (" [column] "+String.format("%2d %3d %3d", newchar, sid, newpos));
		else 
			return (" [column] "+String.format("%2c %3d %3d", newchar, sid, newpos));
	}

	public void visit(int sid, int oldpos, int newpos, int newchar) {
		if (newchar > 0) { //Inside boundary 
		if (MotFinderCL.useDebugOD) Log.println(repoNewChar(sid, newpos, newchar));
			this.letterDict.add(newchar);
			this.lastLetter = newchar;
		}
		else {
		if (MotFinderCL.useDebugOD) Log.println(repoNewChar(sid, newpos, newchar));
			this.letterDict.add(newchar); //negative and non-monotone
			isBoundary = true;
		}
	}

	//Experimental: Closure走査専用：単色でなければ走査の途中でも脱出
	//EXP: C_elegans_Cromosome_V.dna1K.txt: 147sec => 138sec
	boolean visitBreakable(int sid, int oldpos, int newpos, int newchar) {
		if (newchar > 0) { //Inside boundary 
			if (MotFinderCL.useDebugOD) Log.println(repoNewChar(sid, newpos, newchar));
			this.letterDict.add(newchar);
			this.lastLetter = newchar;

			//Experimental: Closure走査専用：単色でなければ脱出
			if (this.getLetterDict().size() > 1)
				return false;
		}
		else {
		if (MotFinderCL.useDebugOD) Log.println(repoNewChar(sid, newpos, newchar));
			this.letterDict.add(newchar); //negative and non-monotone
			isBoundary = true;
		}
		return true;
	}

	HashSet<Integer> getLetterDict() { return letterDict; }

	boolean getIsBoundary() { return isBoundary; }

	int getLastLetter() { return lastLetter; }

	MotFinderCL.ColState isMonotoneColumn() {
		if (this.getIsBoundary()) {
			return MotFinderCL.ColState.BOUNDARY;
		}
		else if (this.getLetterDict().size() <= 1) {
			return MotFinderCL.ColState.ONE;
		}
		else {
			return MotFinderCL.ColState.MANY;
		}
	}
}

class OrigOccDeliverVisitor extends OccVisitor {

	OccPool occPool = null;
	boolean isBoundary = false;

	OrigOccDeliverVisitor(DocSet docSet) {
		this.occPool = new OccPool(docSet);
	}

	public void visit(int sid, int oldpos, int newpos, int newchar) {
		if (newchar > 0) //Inside boundary
			this.occPool.putPair(newchar, sid, oldpos);
		else
			isBoundary = true;
	}

	OccPool getOccPool() {

		//空エントリや，長さゼロのリストを含むエントリを除去する．
		for (int keychar : this.occPool.keyList()) {
			Occurrence newocc = occPool.get(keychar);
			if (newocc == null)
				occPool.remove(keychar);
			else if (newocc.size() == 0)
				occPool.remove(keychar);
			else
				; //do nothing
		} //for keychar
		return occPool;
	}

	boolean getIsBoundary() { return isBoundary; }

}


class ShiftVisitor extends OccVisitor {
	Occurrence newocc = null;
	boolean isBoundary = false;

	ShiftVisitor(Occurrence occ) {
		this.newocc = new Occurrence(Occurrence.docSet);
	}

	public void visit(int sid, int oldpos, int newpos, int newchar) {
		if (newchar > 0) //Inside boundary
			this.newocc.add(sid, newpos);
		else
			isBoundary = true;
	}

	Occurrence getOcc() { return newocc; }

	boolean getIsBoundary() { return isBoundary; }

}


/* EOF */
