import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;

public class CharIncrementer{
	List<char[]> possibleChars;
	int[] currentPosition;
	int[] maxPosition;

	public CharIncrementer(List<char[]> possibleChars){
		this.possibleChars=possibleChars;
		currentPosition=new int[possibleChars.size()];
		maxPosition=new int[possibleChars.size()];
		for(int i=0;i<possibleChars.size();i++){
			currentPosition[i]=0;
			maxPosition[i]=possibleChars.get(i).length;
		}
	}
	public String getCurrentString(){
		String s="";
		for(int i=0;i<currentPosition.length;i++){
			s+=possibleChars.get(i)[currentPosition[i]];
		}
		return s;
	}
	public boolean increment(){
		return incrementFromPosition(maxPosition.length-1);
	}
	private boolean incrementFromPosition(int position){
		currentPosition[position]++;

		//check to see if we need to increment the digit to the left (i.e. like incrementing 9 to 10)
		if(currentPosition[position]==maxPosition[position]){
			currentPosition[position]=0;
			if(position>0){
				return incrementFromPosition(position-1);
			}else{
				//can't increment anymore
				for(int i=0;i<currentPosition.length;i++){
					currentPosition[i]=maxPosition[i]-1;
				}
				return false;
			}

		}
		return true;
	}

	@Override
	public String toString() {
		String cPos="currentPosition="+ArrayUtils.toString(currentPosition);
		String mPos="maxPosition="+ArrayUtils.toString(maxPosition);
		return "CharIncrementer["+cPos+"; "+mPos+"]";
	}
}