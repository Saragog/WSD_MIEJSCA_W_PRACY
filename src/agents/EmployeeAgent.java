package agents;

import jade.core.Agent;


public class EmployeeAgent extends Agent{
	protected void setup() {
		System.out.println("Siema tutaj agent: " + getAID().getName()+" jestem gotowy!!!");
	}
}
