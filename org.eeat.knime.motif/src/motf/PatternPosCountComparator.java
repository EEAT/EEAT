package motf; 
import motf.*;

@SuppressWarnings("unused")
public class PatternPosCountComparator extends PatternComparator {
	public int compare(Pattern pat1, Pattern pat2) {
		int size1 = pat1.getPosCount();
		int size2 = pat2.getPosCount();
		return size1 - size2; 
	}
}

