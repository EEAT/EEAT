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
 * Motf.java
 * Created: 08 June 2005
 * Based on unot.UnotLog.java by Shinji Fusanobu, Kyushu University, 2003.
 *
 * @author <a href="mailto:arim@ist.hokudai.ac.jp">Hiroki Arimura</a>
 * @version $Revision: 1.3 $
 * @see Efficient enumeration of maximal motifs in a sequence,
 * Hiroki Arimura and Takeaki Uno, Manuscript, Division of
 * Computer Science, * IST, Hokkaido University, 9 April 2005;
 * Modified 18 April 2005.  
 */
@SuppressWarnings("unused")
public class Log
{
	/** �����ϥե����� */
	static String outFile  = null; 

	/** ������Ϥ��륹�ȥ꡼�� */
	static PrintStream out = System.out; //default

	public static PrintStream getLogStream() { return Log.out; }
	
	/** �����Ϥ���ꤷ�����ȥ꡼������ꤹ�롥  */
	public static void setStream(PrintStream out) {
		if (out != null) {
			Log.outFile = null; 
			Log.out = out;
		}
	}

	/** �����Ϥ���ꤷ���ե���������ꤹ�롥  */
	public static void setFile(String outfile) {
		try {
			if (outfile != null) {
				Log.outFile = outfile; 
				Log.out = new PrintStream(new FileOutputStream(outfile));
			}
		}
		catch(IOException e){
			System.out.println("main: cannot open a logfile: "+outfile);
			System.exit(1);
		}
	}
	
    /** ��å���������Ϥ��롥
     */
    public static final void log(Object msg)
    {
		out.print(msg);
		out.flush();
    }

    public static final void println(Object msg)
    {
		out.println(msg);
		out.flush();
    }

    public static final void print(Object msg)
    {
		out.print(msg);
		out.flush();
    }

	/** ��å���������Ϥ��ư۾ｪλ���� */
	public static void exit(String msg) {
		out.println("Error: "+msg);
		System.exit(1);
	}

} // UnotLog
