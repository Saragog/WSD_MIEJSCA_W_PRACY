package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Price;
import agents.DeskState;
import agents.EmployeeAgent;
import agents.EmployeeState;

import java.security.acl.Acl;
import java.util.List;

import FIPA.stringsHelper;
import agents.DeskAgent;

public class DeskBehaviour extends CyclicBehaviour{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void action() {
		ACLMessage msg = myAgent.receive();
		String content;
		int performative;
		AID sender;	
		DeskAgent myDeskAgent = ((DeskAgent)myAgent);
		DeskState agentState = myDeskAgent.getDeskState();
			
		String agentName = myAgent.getAID().getName();
		if (agentState != DeskState.END)
		{
			if (msg != null	) 
			{
				content = msg.getContent();
				performative = msg.getPerformative();
				sender = msg.getSender();
				

				
				/*Funkcje 1,2,3 wykonywane sa niezaleznie od stanu agenta Desk.*/
				
				/*	1.	- Odpowiedz na pytanie o aktualna cene.
				 * 		- Sprawdzenie czy ten EmployeeAgent jest juz znany.*/
				if(performative == ACLMessage.INFORM_REF && content.equals("priceQuestion") )
				{
					String response;

					myDeskAgent.getCurrentPrice().normalizeEpsilons();
					response = "price:"+Integer.toString(myDeskAgent.getCurrentPrice().tokens)+":"+Integer.toString(myDeskAgent.getCurrentPrice().epsilons);
					
					sendMessage(sender,
							    response,
							    ACLMessage.INFORM);

					
					addIfIsEmployeeUnknown(myDeskAgent, sender.getName());
				}
				
				/*2. Zapisanie inforamcji o tym, ze jedno z biorek zostalo zajete.*/
				else if(performative == ACLMessage.INFORM && content.equals("idDeskTaken") && !(sender.getName().equals(myDeskAgent.getName())))
					incrementDesksTaken(myDeskAgent);				
				
				
				/*	3.	Zapisanie inforamcji o nowym Employee, ktory dolonczyl do licytacji.
				 * 		Odpowiedz IdACK do nadawcy.*/
				else if(performative == ACLMessage.INFORM) {
					String[] parts = content.split(":");
					
						if( parts[0].equals("employeeId")){
						
							
							addIfIsEmployeeUnknown(myDeskAgent, parts[1]);//dodanie Employee do listy agenta Desk
							sendMessage(sender, "IdACK", ACLMessage.CONFIRM);	//potwierdzenie  odebrania inforamcji o nowym employee
						
						}	
				}
				
				/*4. Zachowanie zalezne od stanu agenta Desk.*/
				else if(performative == ACLMessage.PROPOSE ) {
					
					switch (agentState){
					
					/* Aukcja w stanie FREE*/
						case FREE:
						{
							Price price = getPrice(content);
							System.out.println(myDeskAgent.getDesksTaken() + " FREE "+myAgent.getLocalName() + " otrzymano‚ propozycje:  " +price.tokens +", " + price.epsilons + " od " + msg.getSender().getLocalName());
							
							if(auction(myDeskAgent, sender, price)) {
								myDeskAgent.setDeskState(DeskState.TAKEN);	//zmiana stanu
								incrementDesksTaken(myDeskAgent);	// inkrementacja #desksTaken
								sendMessageToList(myDeskAgent.getAllDesk(),"idDeskTaken", ACLMessage.INFORM); //informuje innych agentÃ³w Desk o zmianie stanu.
								
							}
							
								
								
								
							break;							
						}
						/* Aukcja w stanie TAKEN*/
						case TAKEN:
						{
							Price price = getPrice(content);
							//System.out.println(myDeskAgent.getDesksTaken() +" TAKEN "+myAgent.getLocalName() + " otrzymano‚ propozycje:  " +price.tokens +", " + price.epsilons + " od " + msg.getSender().getLocalName());
							auction(myDeskAgent, sender, price);
						}
					};
				}
			}
			
			/*5. Sprawdzenie warunku zakonczenia aukcji.
			 * 	 Jesli warunek zostal spelniony aukcja zostaje zakonczona*/
			if(shouldAuctionEnd(myDeskAgent))
				endAuction(myDeskAgent);
		}
		else
			block();		
	}
	
	/**Z otrzymanej wiadmosci zwraca obiekt Prices
	 * @param content odebrana wiadomosc
	 * @return price obiekt z porponowana cena*/
	private Price getPrice(String content) {
		String[] parts = content.split(":");
		return new Price (Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));
		
	}

	/**Funkcja sprawdza czy ten EmployyeAgent jest juz znany danemu agnetowi Desk.
	 * Jesli nie to:
	 * - zapisuje info o agencie do listy #employeeList		
	 * - propaguje informacje o do pozostalych agentow Desk
     * */
	private void addIfIsEmployeeUnknown(DeskAgent deskAgent, String employee) {
		if(! deskAgent.isEmployeeOnList(employee)) {
			deskAgent.addEmployeeToList(employee);
			sendMessageToList(deskAgent.getAllDesk(),"EmployeeID:"+employee, ACLMessage.INFORM); //informuje innych agentow Desk o zmianie stanu.
		}
	}

	/**
	 * Funkcja przeprowadzajaca aukcje.
	 * @param deskAgent - agent przeprowadzajacy aukcje
	 * @param employeeAID - identyfikator AID agenta employee
	 * @param price - cena zaproponowana przez agenta employee
	 * @return true-wygral; false-przegral.
	 * 
	 * W wypadku wygranej: 
	 * - zapisuje employeeAID jako WinningEmployee
	 * - zapisuje price jako currentPrice
	 * - odsyla agentowi employee informacje o tym, ze wygral
	 * 	"isBidAccepted", ACLMessage.ACCEPT_PROPOSAL
	 * 
	 * W przypadku przegranej:
	 * - odsyla agentowi eployee informacje o przegranej. 
	 * 	"isBidAccepted", ACLMessage.REJECT_PROPOSAL
	 * */
	private boolean auction (DeskAgent deskAgent, AID employeeAID, Price price) {
			
			if(price.isGreatter(deskAgent.getCurrentPrice())) {
				
				if (deskAgent.getDeskState() == DeskState.TAKEN)
					sendMessage(deskAgent.getWinningEmployee(), "desk_overtaken", ACLMessage.INFORM);
				
				deskAgent.setCurrentPrice(price);	//ustaw nowa najwyzsza cena
				deskAgent.setWinningEmployee(employeeAID);	//ustaw wygrywajacego pracownika
				sendMessage(employeeAID, "isBidAccepted", ACLMessage.ACCEPT_PROPOSAL);	//odeslij info o wygranej
				return true;	
			}
			else {
				sendMessage(employeeAID, "isBidAccepted", ACLMessage.REJECT_PROPOSAL);
				return false;
			}						
	}
	 
	/**Wysylanie wiadomosci do jednego agenta*/
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
	
	/**Wysylanie wiadomosci do listy agentow*/
	private void sendMessageToList(AID[] agentList, String content, int performative) {
		
		ACLMessage messageToBeSent = new ACLMessage(performative);
			messageToBeSent.setContent(content);		
	
		for(AID receiver: agentList) {
			
			
			messageToBeSent.addReceiver(receiver);	
			
		}
		myAgent.send(messageToBeSent);
	}
	
	private void incrementDesksTaken(DeskAgent deskAgent){
		System.out.println(deskAgent.getDesksTaken()+deskAgent.getLocalName());
		deskAgent.setDesksTaken(deskAgent.getDesksTaken() + 1);
	}
	
	private void addEmployeeToList(String employee, DeskAgent deskAgent){
		deskAgent.addEmployeeToList(employee);
	}
	
	private boolean shouldAuctionEnd(DeskAgent deskAgent){
		return deskAgent.getAmountOfEmployees() <= deskAgent.getDesksTaken() && deskAgent.minTimeElapsed();
	}
	
	private void endAuction(DeskAgent deskAgent){
		
		sendMessage(deskAgent.getWinningEmployee(),
			    "End",
			    ACLMessage.INFORM);
		deskAgent.setDeskState(DeskState.END);

		
	}
	
}
