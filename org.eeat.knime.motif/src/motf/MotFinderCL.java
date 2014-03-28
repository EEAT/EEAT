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

    /** Motf�ᥤ��ץ�����¹Ԥ��롥�����ϺǾ������ͤ������͡�
	 *  MotFinder���饹�μ�³�����񤭤��롥
     */
    public PatSet run(int minSupCount)
    {
		//�ȥ졼���ΰ���������
		if (MotFinder.usePrintTrace) {
			useDebugOD = useDebugPP = true;
		}

        Log.println("@log Minimum_Support_Absolute: "+minSupCount);

		if (useDebugOD) Log.println(" [run   ] start mining...");

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

			//õ���Τ���κƵ��ƤӽФ�����<keychar, coreidx>���³�����Ϥ����Ȥǡ�
			//�ѥ������������ٱ䤷�Ƥ��롥
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

	/** ����ͥ��õ���ˤ��ѥ������õ�����롥�ѥ������������ٱ�򤷤ơ�
		�Ƶ��򤷤Ƥ��롥 
		����Ū��դȤ��ơ��ѽХѥ�����ȯ����³���ȼ�³���θ����˸ߴ�����������뤿��ˡ��Ƶ��ΰ��֤���ʸ�Ȥ���Ƥ��뤳�Ȥ���ա��ѥ����������ϡ�����׻��ˤ��Ԥ���
		@param prePattern	�ĥѥ�����˼�Ȥʤ����������Ĳä�����ѥ�����
		@param preOcc    	��ѥ�����νи�������Ū�ˤϷ׻�����Ƥ��ʤ���
		@param keychar   	�ɲä����������
		@param coreidx   	�ɲä����������ΰ���
		@param minSupCount	�Ǿ������ͤ������͡��ޥ��˥󥰥ѥ�᡼����
		@param depth     	�Ƶ��ο�����ɽ�魯�����������ȥåץ�٥뤬0.
	*/
	void expand(Pattern prePattern, Occurrence preOcc, int keychar, int coreidx, 
				int minSupCount, int depth)
	{
		/**********************************************
         * ���ƥåס���ѥ����󤫤��ĥѥ���������
         **********************************************/

		//��ѥ�������ĥѥ�����˼�Ȥʤ����������Ĳä�����Ρ�
		//�Ǥ���prePattern��������

		numberOfExpansion++;
		if (useDebugOD) Log.println("\n"+ repoCall(depth));
		if (useDebugOD) Log.println(" [expand] "+
	       "@process "+repoPatt(prePattern)+
		   " with"+repoSeed(keychar, coreidx)+" a new occ...");
			
		//��ѥ��������Ƭ����¸����ƥ���
		//coreidx ���麸������
		if (useDebugOD) Log.println(" [expand] "+"@prefixcheck for"+repoExpd(prePattern, keychar, coreidx));
		if (isPrefixPreserving(prePattern, preOcc, coreidx) == false) {
			return; //��ѥ�����η׻�����ߡ�
		}

		//��ѥ����������������ˤ���ĥѥ�����Ȥ��νи���׻���
		//coreidx ���鱦������
		if (useDebugOD) Log.println(" [expand] "+"@take closure of"+
			  repoExpd(prePattern, keychar, coreidx));
		Pattern pattern = closure(prePattern.copy(), preOcc, coreidx);
		Occurrence occ = shiftOcc(preOcc, pattern.size() - prePattern.size());
		if (useDebugOD) Log.println(" [expand] "+"@closed "+
		   repoPatt(pattern));

		/**********************************************
         * ���ƥåס��ĥѥ�����θ����Ƚ���
         **********************************************/

		//�ѥ�����θ�����������������
		if (MotFinder.useMaxPatSize && depth >= this.maxPatSize) {
			if (useDebugOD) Log.println("@pruned by maxexpand: "+
										" depth="+depth+
										": "+repoPatt(pattern));
			return;
		}

		//�ѥ�����θ������Ǿ�����
		if (scorer.count(occ) < minSupCount) {
			if (useDebugOD) Log.println("@pruned by minsup:"+
										" score="+scorer.count(occ)+
										": "+repoPatt(pattern));
			return; //���λޤ�õ������ߤ���
		}

		//���Ĥ��ä��ѥ������PatSet����Ͽ���롥
		if (useDebugOD) Log.println(" [expand] "+"@register pattern: "+
									repoPatt(pattern));
		registerPattern(pattern, occ);

		/**********************************************
         * ���ƥåס���¹�Υѥ������Ƶ�Ū�˳�ĥ���Ѥ���õ�����롥		
         **********************************************/

		//For all left part common to the positions of the pattern: 
		if (useDebugOD) Log.println(" [expand] "+"@iterate for children..."); 
		for (int idx = coreidx + 1; idx < this.maxPatSize; idx++) {

			//�������ΰ��֤򥹥��åפ���
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; //skip a constant position
			}

			//�и�ʬ�۵�ˡ (OccurrenceDeliver technique)
			//oldOcc��ΰ��֤�ʬ�ह�롥
			int off = idx - pattern.size() + 1;
			OccPool occPool = occDeliverOrigOcc(occ, off);

			if (useDebugOD) Log.println(" [expand] "+"@collect symbols: "+
			   "  keychars="+OccPool.keyListString(occPool.keyList())); 

			//ʬ�व�줿���ֽ��礴�Ȥ˽���
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
				//�ҤȤʤ��ѥ�����ؤκƵ��ƤӽФ�
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
			//���祮��å״ֳ֤��Υ�줿���֤�����ϳ�ĥ���ʤ�
			if (idx < 0 && this.maxGapLen > -1 && //�ѥ�����ü���麸���ΰ���
				-1*(idx + 1) > this.maxGapLen) {  //����å�Ĺ��maxGapLen��Ķ���Ƥ���
				if (useDebugOD) Log.println(" [prefix] @stop scanning by maxGapLen: "+this.maxGapLen);
				return true; //ü���ˤ֤Ĥ��ä�
			}

			//�������ΰ��֤򥹥��åפ���
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; //skip a constant position
			}

			if (useDebugPP) Log.println(" [prefix] @column check for"+repoPPC(idx,off));

			//�������ľ�������ơ����ΰ��֤�ʸ����Ĵ�٤�
			ColumnVisitor tester = checkMonotoneColumn(occ, off); 
			ColState state = tester.isMonotoneColumn();
			if (useDebugPP) Log.println(" [column] @colstate="+state);

			//��������ʸ���ü�ˤ֤Ĥ��ä�
			if (state == ColState.BOUNDARY) {
				if (useDebugPP) Log.println(
				" [prefix] @result: prefix-preserving YES!");
				return true;
			}
			//���ʤ��Ȥ���ñ������ब���Ĥ��ä�
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
		int lastIdx = newIdx; //��˸��Ĥ���ñ����������

		//����ʸ�����������֤Ȥ����걦���Τ��٤Ƥΰ��֤ˤĤ��������Ȥ롥
		//��ա��ٱ�׻��ˤ�ꡤ����ʸ���������򤳤�����׻��ǹԤ�����ˡ�
		//����ʸ���ΰ���newIdx��ޤ�Ƥ��뤳�Ȥ���ա�
		for (int idx = newIdx; true; idx++) {
			//Note: It is critical to start from newIdx but from newIdx+1.
			//For the lazy evaluation of inserting keychar.
			int off = idx - len + 1; 

			//�������ΰ��֤򥹥��åפ���
			if (isIdxOfConstantSymbol(pattern, idx)) {
				continue; 
			}

			//Experimental: GapLen-Bounded Rigid Patterns
			//���祮��å״ֳ֤��Υ�줿���֤�����ϳ�ĥ���ʤ�
			if (this.maxGapLen > -1 && idx - lastIdx > this.maxGapLen + 1) {
				if (useDebugOD) Log.println(" [closur] @stop scanning by maxGapLen: "+this.maxGapLen);
				break; //�Ĥ�ΰ��֤򥹥��åפ���
			}

			if (useDebugOD) Log.println(" [closur] @check column"+repoPPC(idx,off));

			//�������ľ�������ơ����ΰ��֤�ʸ����Ĵ�٤�
			//ColumnVisitor tester = checkMonotoneColumn(occ, off); 
			ColumnVisitor tester = checkMonotoneColumnBreakable(occ, off); //Exp
			ColState state = tester.isMonotoneColumn();
			if (useDebugPP) Log.println(" [closur] @colstate="+state);

			//��������ʸ���ü�ˤ֤Ĥ��ä�
			if (state == ColState.BOUNDARY) {
				break; //exit the loop
			}
			//���ʤ��Ȥ���ñ������ब���Ĥ��ä�
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

	/** �и���ʬ��ˡ��
	 *  Ϳ����줿�и�������Ф��ơ����ե��å�ʬ�����û��������֤˽и�����
	 *	ʸ���򥭡��Ȥ����б����븵�νи�������ͤȤ��Ƴ�Ǽ�����и�
	 *	�ס�����֤���
	 *  ���������֤�ʸ���򥭡��Ȥ��ơ����ΰ��֤�ʬ�ह�뤳�Ȥ����
	 *  ��MotFinderOD.occDeliver()�ȤϤ��Ȥʤ�ˡ�
	 *  @param oldocc	���νи�����
	 *  @param offset	���֤���ʬ�����ʱ����ˤޤ�����ʺ����ˤ��ͤ������
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

	//Experimental: Closure�������ѡ�ñ���Ǥʤ��������������Ǥ�æ��
	//EXP: C_elegans_Cromosome_V.dna1K.txt: 147sec => 138sec
	boolean visitBreakable(int sid, int oldpos, int newpos, int newchar) {
		if (newchar > 0) { //Inside boundary 
			if (MotFinderCL.useDebugOD) Log.println(repoNewChar(sid, newpos, newchar));
			this.letterDict.add(newchar);
			this.lastLetter = newchar;

			//Experimental: Closure�������ѡ�ñ���Ǥʤ����æ��
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

		//������ȥ�䡤Ĺ������Υꥹ�Ȥ�ޤ२��ȥ�����롥
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
