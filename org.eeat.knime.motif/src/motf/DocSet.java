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
	 * ������
	 */

    /** Java ��� DEBUG ���͡� */
	static boolean useDebug  	= false;

	/** ɸ�����Ϥ�ɽ�魯���ޥ�ɰ�����ʸ���� */
	public static final String STDIN_STRING = "-";

    /** EOFʸ�����͡� */
	static int EOF 	= -1;
	static int NL  	= '\n';

	/*
	 * ���С��ե������
	 */

	ArrayList<Sequence> docList = new ArrayList<Sequence>();

	DocStat docStat = new DocStat(this);

	/*
	 * ���С��ե�����ɡ����ץ���ʥ�
	 */

    /** �����å������٤Ȥ���ʸ�����٤��Ѥ��뤫�ɤ����򼨤��� */
	boolean useDocCount = false;

	public void setUseDocCount(boolean flag) { this.useDocCount = flag; }
	/*
	 * ��³��
	 */

	public int size() {
		return docList.size();
	}

	public Sequence get(int sid) {
		return docList.get(sid);
	}

	/*
	 * �ե���������
	 */

	@SuppressWarnings("unchecked")
	public void readFiles(List files)
	{
		for (int i = 0; i < files.size(); i++) {
			String filename = (String) files.get(i);
			if (filename.equals(DocSet.STDIN_STRING)) {
				readFile(System.in);
				return; //�ɤ߹������Ĥ�ΰ����ϼΤƤ롥
			} else {
				readFile(filename);
			}
		}
	}

	/** ��åѡ����ե����뤫���ɤ߹��ࡥ*/
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

	/** ��åѡ����ե����륹�ȥ꡼�फ���ɤ߹��ࡥ*/
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

	/** ���ꤵ�줿���ȥ꡼�फ���ɤ߹��� */
	public abstract void readFile(Reader in);


	int getTotalNumOfLetters() {
		return docStat.getTotalNumOfLetters();
	}

    /**
     * �Ǿ����ݡ��Ȥ�ѡ������ɽ���������ни������Ѵ����롥
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

	/** ���ꤵ�줿���ȥ꡼��˽��Ϥ��� */
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


/**ʸ�񽸹�����פ�������� */
class DocStat {

	DocSet docSet = null;
	public boolean isUptoDate = false; //the status of update

	/* ���ץѥ�᡼�� */
	int num = 0;
	int lenAve = 0;	//ʸ���Ĺ����ʿ����
	int lenMax = 0;	//ʸ���Ĺ���κ�����
	int lenMin = -1;//ʸ���Ĺ���κǾ���
	int lenVar = 0;	//ʸ���Ĺ����ʬ����
	int lenSum = 0; //ɬ�ܡ�ʸ���Ĺ�������¡��ᡡ��ʸ����
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

	/** ʸ�񽸹�����פ���� */
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

	/** ʸ�񽸹�����פ���� */
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
