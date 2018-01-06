package utils;

public class Price implements Comparable<Price>{
	
	public int tokens = 0;
	public int epsilons = 0;
	
	
	@Override
	public int compareTo(Price o) {
		if(this.tokens > o.tokens)
			return 1;
		if(this.tokens == o.tokens)
		{
			if(this.epsilons > o.epsilons)
				return 1;
			if(this.epsilons == o.epsilons)
				return 0;
		}
				
		return -1;
	}
	
	

}
