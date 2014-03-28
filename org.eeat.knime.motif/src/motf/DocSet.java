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
 * DocSet.java
 * Created: 08 June 2005
 *
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.7 $
 */
@SuppressWarnings("unused")
public abstract class DocSet {
	/*
	 * 定数宣言
	 */

    /** Java 定数 DEBUG の値． */
	static boolean useDebug  	= false;

	/** 標準入力を表わすコマンド引数の文字列 */
	public static final String STDIN_STRING = "-";

    /** EOF文字の値． */
	static int EOF 	= -1;
	static int NL  	= '\n';

	/*
	 * メンバーフィールド
	 */

	ArrayList<Sequence> docList = new ArrayList<Sequence>();

	DocStat docStat = new DocStat(this);

	/*
	 * メンバーフィールド：オプショナル
	 */

    /** スイッチ：頻度として文書頻度を用いるかどうかを示す． */
	boolean useDocCount = false;

	public void setUseDocCount(boolean flag) { this.useDocCount = flag; }
	/*
	 * 手続き
	 */

	public int size() {
		return docList.size();
	}

	public Sequence get(int sid) {
		return docList.get(sid);
	}

	/*
	 * ファイル入力
	 */

	@SuppressWarnings("unchecked")
	public void readFiles(List files)
	{
		for (int i = 0; i < files.size(); i++) {
			String filename = (String) files.get(i);
			if (filename.equals(DocSet.STDIN_STRING)) {
				readFile(System.in);
				return; //読み込んだら残りの引数は捨てる．
			} else {
				readFile(filename);
			}
		}
	}

	/** ラッパー：ファイルから読み込む．*/
	public void readFile(String filename)
	{
		Log.log("@log Read_File: " + filename + "\n");
		try {
			// ars: encoding
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader fr = new InputStreamReader(fis, "UTF8");
			//FileReader fr = new FileReader(filename); //for debug
			Log.println("@log Encoding: " + fr.getEncoding());
			readFile(fr);
			this.docStat.isUptoDate = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** ラッパー：ファイルストリームから読み込む．*/
	public void readFile(InputStream is)
	{
		Log.log("@log Read_File: InputStream("+is+")\n");
		try {
			// ars: add utf-8
			readFile(new BufferedReader(new InputStreamReader(is, "UTF-8")));
			this.docStat.isUptoDate = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 指定されたストリームから読み込む */
	public abstract void readFile(Reader in);


	int getTotalNumOfLetters() {
		return docStat.getTotalNumOfLetters();
	}

    /**
     * 最小サポートをパーセント表示から絶対出現数に変換する．
     */
    public int convertSupRatioToAbsolute(double ratio)
    {
		int totalNumber;

		docStat.ensureUptoDate();
		if (this.useDocCount)
			totalNumber = this.docList.size();
		else
			totalNumber = this.docStat.getTotalNumOfLetters();

        int minsup = (int) Math.ceil(totalNumber * ratio);
		if (minsup <= 1) {
			minsup = Math.max(minsup, 2);
			Log.print("@log Warning: MinSupCount must be >= 2. "+
					  "Automatically changed to: " + minsup + "\n");
		}
        return minsup;
    }

	public void printStat(PrintStream out) {
		this.docStat.print(out);
	}

	/** 指定されたストリームに出力する */
	public void writeFile(PrintStream out) {
		this.docStat.ensureUptoDate();
		out.println("@docset"+
					" "+docList.size()+
					" "+this.docStat.getTotalNumOfLetters());
		for (int i = 0; i < docList.size(); i++) {
			Sequence doc = docList.get(i);
			out.println(i+": "+doc.toString());
		}
	}

	/***************************************************
	 *
	 ***************************************************/
}


/**文書集合の統計を管理する */
class DocStat {

	DocSet docSet = null;
	public boolean isUptoDate = false; //the status of update

	/* 統計パラメータ */
	int num = 0;
	int lenAve = 0;	//文書の長さの平均値
	int lenMax = 0;	//文書の長さの最大値
	int lenMin = -1;//文書の長さの最小値
	int lenVar = 0;	//文書の長さの分散値
	int lenSum = 0; //必須：文書の長さの総和　＝　総文字数
	int lenSumSqr = 0;

	DocStat(DocSet docSet) {
		this.docSet = docSet;
	}

	public int getTotalNumOfLetters() {
		this.ensureUptoDate();
		return lenSum;
	}

	public void setTouched() { isUptoDate = false; }

	public void ensureUptoDate() {
		if (!this.isUptoDate)
			update();
	}

	/** 文書集合の統計を求める */
	public void update() {

		ArrayList<Sequence> docList = docSet.docList;

		if (docList.size() == 0) {
			return;
		}

		//scan lines
		for (Sequence doc : docList) {
			int len = doc.size();
			lenMax = Math.max(lenMax, len);
			if (lenMin < 0) lenMin = len;
			else    		lenMin = Math.min(lenMin, len);
			lenSum = lenSum + len;
			lenSumSqr = lenSumSqr + len*len;
			num++;
		}
		//post
		lenAve = lenSum / num;

		/* Variance of random variable Xs = (X_1,...,X_n):
		 * VAR[X] = (1/n)* SUM[(X - (1/n)*SUM[X])^2]
		 * = (1/n)*{ SUM[X*X] - (1/n)*SUM[X]*SUM[X]} */

		lenVar = ((lenSumSqr * num) - (lenSum * lenSum)) / (num * num);

		this.isUptoDate = true;
	}

	/** 文書集合の統計を求める */
	public void print(PrintStream out) {

		if (this.docSet.docList.size() == 0) {
			out.println("@stat numOfDocs: "+0);
			return;
		}

		ensureUptoDate();

		//print
		out.println("@stat numberOfDocumns: "+num);
		out.println("@stat numberOfletters: "+lenSum+" (chars)"+"\t"+
					String.format("%.3f", ((double)lenSum/1000000.0)) +" (MB)");
		out.println("@stat on sequences...");
		out.println("@stat minLength: "+lenMin);
		out.println("@stat maxLength: "+lenMax);
		out.println("@stat aveLength: "+lenAve);
		out.println("@stat varLength: "+
					String.format("%g", Math.sqrt(lenVar)));
	}
}


/* EOF */
