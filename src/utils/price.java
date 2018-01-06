package utils;

public class price implements Comparable<price>{
	
	public int tokens = 0;
	public int epsilons = 0;
	
	
	@Override
	public int compareTo(price o) {
		if(this.tokens < o.tokens)
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
