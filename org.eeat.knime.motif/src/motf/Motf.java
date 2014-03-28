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
 * Motf.java
 * Created: 08 June 2005
 *
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.11 $
 * @see Efficient enumeration of maximal motifs in a sequence,
 * Hiroki Arimura and Takeaki Uno, Manuscript, Division of
 * Computer Science, * IST, Hokkaido University, 9 April 2005;
 * Modified 18 April 2005.
 */
@SuppressWarnings("unused")
public class Motf {

    static String USAGE_STRING =
	"usage: java Motf [OPTIONS] Inputfile \n"+
	"OPTIOS \n"+
	"  -r <ratio> | -minsupratio \t min freq threshold ratio (in real)\n"+
	"  -R <count> | -minsupcount \t min freq threshold count (in int)\n"+
	"  -m  <len>  | -maxpatlen   \t maximum pattern length\n"+
	"  -mg <len>  | -maxgaplen   \t maximum gap length (no less than zero)\n"+
	"  -d | -doccount      \t use document count \n"+
	"  -o <outfile> | -out \t the output file for the disocovered patterns \n"+
	"  -l <logfile> | -log \t the output file for logging \n"+
	"  -h | -help          \t show this help message \n"+
	"EXPERIMENTAL \n"+
	"  -g  | -debug      \t print debugging info \n"+
	"  -gs | -gStatDocSet \t print the statistics of a document set \n"+
	"  -gnod | -gNoOccurrenceDeliver\t No OD (No Occurrence Deliver) \n"+
	"  -god1 | -gOccurrenceDeliver1 \t use OD (Occurrence Deliver) \n"+
	"  -gclo | -gClosed   \t use MaxMotif \n"+
	"  -gfrq | -gFrequent \t use No MaxMotif \n"+
	"  -pi | -gWriteDocSet\t print the sequences in a document set \n"+
	"  -pc | -printCount \t print each pattern with its occurrence count \n"+
	"  -po | -printOcc   \t print each pattern with its occurrence list \n"+
	"  -ps | -printSorted\t print patterns in increasing order of their scores \n"+
	"  -pt | -printTrace \t print the trace of the computation \n"+
	"";


	/*
	 * 定数宣言
	 */

    /** Java 定数 DEBUG の値． */
	public static boolean useDebug  	= false;
	public static boolean useDebugOccPool 	= false;

    /** スイッチ：頻度として文書頻度を用いるかどうかを示す． */
	public static boolean useDocCount 	= false;

    /** スイッチ：各パターンと一緒に位置頻度と文書頻度を印刷するか． */
	public static boolean usePrintCount 	= true;

    /** スイッチ：各パターンと一緒に位置頻度と文書頻度を印刷するか． */
	public static boolean usePrintOcc	= false;

    /** 正常終了時の返り値 */
    public static final int EXIT_SUCCESS = 0;

    /** 非正常終了時の返り値 */
    public static final int EXIT_FAILURE = 1;

	////入出力関係 ////

    /** 入力ファイル名のリスト． */
    public static List<String> inFiles = new ArrayList<String>();

    /** 出力ファイル名．発見したパターンを出力する． */
    public static String outFile = null;

    /** ログファイル名．計算のログを出力する． */
    public static String logFile = null;

	/** 出力用ストリーム */
	private static PrintStream out = null;

	//// 発見手続き関係 ////

    /** 最小サポート値．(0 < minSup <= 1) の Double 値で与える． */
    public static double minSupRatio = -1;

    /** 最小サポート値．1以上のint値で与える． */
    public static int minSupCount = 2;

    /** 最大パターン長値．1以上のint値で与える．値0は制限なしを表わす． */
    public static int maxPatSize = 0;

    /** 最大ギャップ長値．0以上のint値で与える．値0は制限なしを表わす． */
    public static int maxGapLen = -1;

	/*
	 * 開発用機能
	 */

    /** 文書集合の出力のみを行うスイッチ． */
	public static boolean useWriteDocSet = false;

    /** 文書集合の統計情報の出力を行うスイッチ． */
	public static boolean useStatDocSet = false;

    /** パターン集合のスコアによる整列を行うスイッチ．
	 *  スコアは標準では頻度値の昇順 */
	public static boolean useSortByScore = false;

    /** パターン発見アルゴリズムの出現配分技法(OD)を用いるスイッチ．*/
	public static boolean useOD = true;
	public static boolean useOD1 = false;

    /** 閉パターン発見アルゴリズムを用いるスイッチ．*/
	public static boolean useCL = false;

    /** 計算の過程を印刷するためのスイッチ．*/
	public static boolean usePrintTrace = false;

    /** 計算の過程を印刷するためのスイッチ．*/
	//static boolean useJapaneseEncoding = false;

	/*
	 * メンバー変数
	 */

    //Handling Options and Arguments
    static int argc = 0;

	static void readOptionsAndArgs(String[] args) {

		/// Try clause
		try {

			//Error handling: Check if there is any argument
			if (args.length == 0) {
				System.out.println(USAGE_STRING);
				System.exit(Motf.EXIT_SUCCESS);
			}

			//Read Options
			for (; argc < args.length; argc++){

				//////////////// Switch with no arg ////////////////
				//With no arg
				if (args[argc].equals("-g") || args[argc].equals("-debug")){
					Motf.useDebug = true;
				}
				else if (args[argc].equals("-d") ||
						 args[argc].equals("-doccount")){
					Motf.useDocCount = true;
				}
				//////////////// With one arg  ////////////////
				//With one arg of String:
				else if (args[argc].equals("-o") ||
						 args[argc].equals("-out")){
					//usage: -o OUT_FILE
                    Motf.outFile = new String(args[++argc]);
				}
				//With one arg of String: logging file
				else if (args[argc].equals("-l") ||
						 args[argc].equals("-log")){
					//usage: -l LOG_FILE
                    Motf.logFile = new String(args[++argc]);
				}
				//With one arg of float: minimum support
				else if (args[argc].equals("-r") ||
						 args[argc].equals("-minsupratio")){
					Motf.minSupRatio = Double.parseDouble(args[++argc]);
					if (Motf.minSupRatio < 0 || Motf.minSupRatio > 1.0)
						throw new IllegalArgumentException(
						 "-r option: minsup must between zero and one: "+
						 Motf.minSupRatio);
				}
				//With one arg of int: minimum support
				else if (args[argc].equals("-R") ||
						 args[argc].equals("-minsupcount")){
					Motf.minSupCount = Integer.parseInt(args[++argc]);
				}
				//With one arg of int: minimum support
				else if (args[argc].equals("-m") ||
						 args[argc].equals("-maxpatsize")){
					Motf.maxPatSize = Integer.parseInt(args[++argc]);
				}
				//////////////////// EXPERIMENTAL ////////////////////
				else if (args[argc].equals("-gs") ||
						 args[argc].equals("-gStatDocSet")){
					Motf.useStatDocSet = true;
				}
				else if (args[argc].equals("-gnod") ||
						 args[argc].equals("-gNoOccurrenceDeliver")){
					Motf.useOD = false;
				}
				else if (args[argc].equals("-god1") ||
						 args[argc].equals("-gOccurrenceDeliver1")){
					Motf.useOD1 = true;
				}
				else if (args[argc].equals("-gfrq") ||
						 args[argc].equals("-gFrquent")){
					Motf.useCL = false;
				}
				else if (args[argc].equals("-gclo") ||
						 args[argc].equals("-gClosed")){
					Motf.useCL = true;
				}
				else if (args[argc].equals("-pi") ||
						 args[argc].equals("-printInputDocSet")){
					Motf.useWriteDocSet = true;
				}
				else if (args[argc].equals("-pc") ||
						 args[argc].equals("-printCount")){
					Motf.usePrintCount = true;
				}
				else if (args[argc].equals("-po") ||
						 args[argc].equals("-printOcc")){
					Motf.usePrintOcc = true;
				}
				else if (args[argc].equals("-ps") ||
						 args[argc].equals("-printSorted")){
					Motf.useSortByScore = true;
				}
				else if (args[argc].equals("-pt") ||
						 args[argc].equals("-printTrace")){
					Motf.usePrintTrace = true;
				}
				//With one arg of int: maximum gap length
				else if (args[argc].equals("-mg") ||
						 args[argc].equals("-maxgaplen")){
					Motf.maxGapLen = Integer.parseInt(args[++argc]);
				}
				//////////////////// end ////////////////////
				//help
				else if (args[argc].equals("-h") || args[argc].equals("-help")){
					System.err.println(USAGE_STRING);
					System.exit(0);
				}
				//Default action
				else if (args[argc].charAt(0) == '-'){
					System.out.println("Undefined option: "+args[argc]);
					System.exit(1);
				}
				//Default action
				else {
					break;
				}
			}

			//
			// Read Rest of the Arguments
			//

			// Read input filenames
			while (argc < args.length) {
				//ordinary file
				String filename = args[argc];
				File   fileObj  = new File(filename);
				if (!fileObj.exists())
					throw new FileNotFoundException(filename);
				if (!fileObj.canRead())
					throw new IOException();
				Motf.inFiles.add(filename);
				argc++;
			}

			if (inFiles.size() <= 0)
				throw new IllegalArgumentException("no input filename");

		/// Error handling
		} catch (IllegalArgumentException e) {
			System.err.println("MotfError: IllegalArgument: "+e.getMessage());
			System.out.println(USAGE_STRING);
			e.printStackTrace();
			System.exit(Motf.EXIT_FAILURE);
		} catch (Exception e) {
            System.err.println("MotfError: "+e.getMessage());
			e.printStackTrace();
			System.exit(Motf.EXIT_FAILURE);
		}

	}

    public static void main(String[] args)
    {
		long starttime = 0;

		//Reading options
		readOptionsAndArgs(args);

		//ログファイル設定
		if (Motf.logFile != null)
			Log.setFile(Motf.logFile);

		//出力ファイル設定
		try {
			if (Motf.outFile == null)
				out = System.out;
			else
				// ars: encoding
				out = new PrintStream(new FileOutputStream(Motf.outFile), true, "UTF-8");
			Log.println("@log OutputFile: "+Motf.outFile);
		}
		catch(IOException e){
			Log.exit("main: cannot open outFile: "+Motf.outFile);
		}

        // DocSetの生成
		DocSet docSet = new LetterDocSet();
		docSet.setUseDocCount(Motf.useDocCount);
		docSet.readFiles(Motf.inFiles);

		// Experimental: DocSetの統計情報
		if (useStatDocSet) {
			Log.println("@log Printing Stat: docSet");
			docSet.printStat(Log.getLogStream());
		}

		// Experimental: DocSetの印刷
		if (useWriteDocSet) {
			Log.println("@log Printing docSet");
			docSet.writeFile(Log.getLogStream());
			//return;
		}

		// Experimental:
		OccPool occPool = new OccPool(docSet);
		if (useDebugOccPool) {
			Log.println("@log Printing occPool");
			occPool.writeFile(Log.getLogStream());
		}

		// Experimental: Passing option parameters
		PatSet patSet = null;

		MotFinder finder;
		if (!Motf.useOD)
			finder = new MotFinderNOD(docSet);
		else if (Motf.useOD1)
			finder = new MotFinderOD1(docSet);
		else if (Motf.useCL)
			finder = new MotFinderCL(docSet);
		else
			finder = new MotFinderOD(docSet);
		Log.println("@log Finder: "+finder.getVersionString());

		if (Motf.maxPatSize > 0) finder.setMaxPatSize(Motf.maxPatSize);
		if (Motf.maxGapLen > -1)  finder.setMaxGapLen(Motf.maxGapLen);

		if (Motf.useDocCount)	finder.setUseDocCount(true);
		if (Motf.usePrintCount)	MotFinder.usePrintCount=true;
		if (Motf.usePrintOcc)	MotFinder.usePrintOcc=true;
		if (Motf.usePrintTrace)	MotFinder.usePrintTrace=true;

		//Experimental: 評価関数の設定. 標準は PosScorer
		if (Motf.useDocCount)
			finder.setScorer(new DocScorer(docSet));

		// Run the main program
		long start = System.currentTimeMillis();
		Log.println("@log Start Mining... ");
		if (Motf.minSupRatio > 0)
			patSet = finder.run(Motf.minSupRatio);
		else if (Motf.minSupCount > 0)
			patSet = finder.run(Motf.minSupCount);
		else
			Log.exit("@error Morf.main: no valid minsup values!");

		Log.println("@log RunTime: "+(System.currentTimeMillis()-start)+" [ms]");

		// Print discovered patterns
        Log.println("@log Printing patterns...");
		//sort
		PatternComparator comp = null;
		if (Motf.useSortByScore)
			//Sort by pattern score
			if (Motf.useDocCount)
				comp = new PatternDocCountComparator();
			else
				comp = new PatternPosCountComparator();
		else
			//Sort by pattern size
			comp = new PatternSizeComparator();
		patSet.sort(comp);
		patSet.writeFile(out);

        Log.println("@log number of patterns found: "+patSet.size());

	}//main

}//Motf

/* EOF */
