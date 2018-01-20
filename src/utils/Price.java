package utils;

public class Price implements Comparable<Price>{
	
	
	public int tokens = 0;
	public int epsilons = 0;
	public static int maxEps = 100;
	
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
	
	/**/
	public boolean isGreatter(Price p) {
		int compare = this.compareTo(p);
		if(compare == 1) return true;
		else return false;
	}


	public Price(int tokens, int epsilons) {
		
		this.tokens = tokens;
		this.epsilons = epsilons;
	}
	
	public Price()
	{
		
	}
	
	@Override
	public String toString() {
		
		return Integer.toString(this.tokens) +"_"+ this.epsilons;
	}
	
	public void normalizeEpsilons() {
		while(epsilons >= maxEps)
		{
			epsilons = epsilons - maxEps;
			tokens++;
		}
	}

	
	
	
	
	
	

}
