package behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Price;
import agents.DeskState;

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
		
		if (msg != null	) 
		{
			content = msg.getContent();
			performative = msg.getPerformative();
			sender = msg.getSender();
			
			/*Funkcje 1,2,3,4 wykonywane są nie zależnie od stanu agenta Desk.*/
			
			/*	1.	- Odpowiedz na pytanie o aktualną cene.
			 * 		- Sprawdzenie czy ten EmployeeAgent jest już znany.*/
			if(performative == ACLMessage.INFORM_REF && content.equals("priceQuestion") )
			{

				System.out.println(agentName + " Obecna cena to: " + myDeskAgent.getCurrentPrice().tokens +", "+myDeskAgent.getCurrentPrice().epsilons);
									
				sendMessage(sender,
						    "price:"+Integer.toString(myDeskAgent.getCurrentPrice().tokens)+":"+Integer.toString(myDeskAgent.getCurrentPrice().epsilons),
						    ACLMessage.INFORM);
				
				isEmployeeUnknown(myDeskAgent, sender);
			}
			
			/*2. Zapisanie inforamcji o tym, ze jedno z biorek zostalo zajete.*/
			else if(performative == ACLMessage.INFORM && content.equals("idDeskTaken") ) {
				System.out.println(agentName + ": Otrzymał informacje o zmianie stanu DeskAgent: " + sender);
				incrementDesksTaken(myDeskAgent);
			}
			
			/*3. Obsługa sygnału zakończenia aukcji.*/
			else if(performative == ACLMessage.INFORM && content.equals("End")) {
				//TODO	
			}
			
			/*	4.	Zapisanie inforamcji o nowym Employee, który dołączył do licytacji.
			 * 		Odpowiedź IdACK do nadawcy.*/
			else if(performative == ACLMessage.INFORM) {
				String[] parts = content.split(":");
				switch (parts[0]) {
					case "employeeId":{
						AID employeeAID = new AID();
						employeeAID.setName(parts[1]);
						
						addEmployeeToList(employeeAID, myDeskAgent);	//dodanie Employee do listy agenta Desk
						sendMessage(sender, "IdACK", ACLMessage.CONFIRM);	//potwierdzenie  odebrania inforamcji o nowym employee
					}
				}	
			}
			
			/*5. Zachowanie zależne od stanu agenta Desk.*/
			else{
				switch (agentState){
				/*5.1 Aukcja w stanie FREE*/
					case FREE:{
						if(performative == ACLMessage.PROPOSE ) {
							Price price = getPrice(content);
							boolean win = auction(myDeskAgent, sender, price);
							System.out.println("Biurko otrzymalo wiadomosc ze ktos chce bidowac je za: " + price);
							if(win) {
								myDeskAgent.setDeskState(DeskState.TAKEN);	//zmiana stanu
								incrementDesksTaken(myDeskAgent);	// inkrementacja #desksTaken
								sendMessageToList(myDeskAgent.getAllDesk(),"idDeskTaken", ACLMessage.INFORM); //informuje innych agentów Desk o zmianie stanu.
							}
						}
						
					}
					/*5.2 Aukcja w stanie TAKEN*/
					case TAKEN:
					{
						if(performative == ACLMessage.PROPOSE ) {
							Price price = getPrice(content);
							boolean win = auction(myDeskAgent, sender, price);
							if(win) {
								myDeskAgent.setDeskState(DeskState.TAKEN);	//zmiana stanu
								incrementDesksTaken(myDeskAgent);	// inkrementacja #desksTaken
								sendMessageToList(myDeskAgent.getAllDesk(),"idDeskTaken", ACLMessage.INFORM); //informuje innych agentów Desk o zmianie stanu.
							}
						}						
					}		
				};
			}
		}
		
		/*6. Sprawdzenie warunku zakonczenia aukcji.
		 * 	 Jesli warunek został spełniony aukcja zostaje zakończona*/
		if(shouldAuctionEnd(myDeskAgent))
			endAuction(myDeskAgent);
		
	}
	
	/**Z otrzymanej wiadmości zwraca obiekt Prices
	 * @param content odebrana wiadomośc
	 * @return price obiekt z porponowaną ceną*/
	private Price getPrice(String content) {
		String[] parts = content.split(":");
		return new Price (Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));
		
	}

	/**Funkcja sprawdza czy ten EmployyeAgent jest juz znany danemu agnetowi Desk.
	 * Jesli nie to:
	 * - zapisuje info o agencie do listy #employeeList		
	 * - propaguje informacje o do pozostąłych agentów Desk
     * */
	private void isEmployeeUnknown(DeskAgent deskAgent, AID employeeAID) {
		
		if(! deskAgent.isEmployeeOnList(employeeAID)) {
			deskAgent.addEmployeeToList(employeeAID);
			sendMessageToList(deskAgent.getAllDesk(),"EmployeeID:"+employeeAID.toString(), ACLMessage.INFORM); //informuje innych agentów Desk o zmianie stanu.
		}
	}

	/**
	 * Funkcja przeprowadzająca aukcje.
	 * @param deskAgent - agent przeprowadzajacy aukcje
	 * @param employeeAID - identyfikator AID agenta employee
	 * @param price - cena zaproponowana przez agenta employee
	 * @return true-wygrał; false-przegrał.
	 * 
	 * W wypadku wygranej: 
	 * - zapisuje employeeAID jako WinningEmployee
	 * - zapisuje price jako currentPrice
	 * - odsyła agentowi employee informacje o tym, że wygrał.
	 * 	"isBidAccepted", ACLMessage.ACCEPT_PROPOSAL
	 * 
	 * W przypadku przegranej:
	 * - odsyła agentowi eployee informacje o przegranej. 
	 * 	"isBidAccepted", ACLMessage.REJECT_PROPOSAL
	 * */
	private boolean auction (DeskAgent deskAgent, AID employeeAID, Price price) {
			
			if(price.isGreatter(deskAgent.getCurrentPrice())) {
				deskAgent.setCurrentPrice(price);	//ustaw nowa najwyższa cenę
				deskAgent.setWinningEmployee(employeeAID);	//ustaw wygrywającego pracownika
				sendMessage(employeeAID, "isBidAccepted", ACLMessage.ACCEPT_PROPOSAL);	//odeśli info o wygranej
				return true;	
			}
			else {
				sendMessage(employeeAID, "isBidAccepted", ACLMessage.REJECT_PROPOSAL);
				return false;
			}						
	}
	 
	/**Wysyłanie wiadomości do jednego agenta*/
	private void sendMessage(AID receiver, String content, int performative)
	{
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.addReceiver(receiver);
		messageToBeSent.setContent(content);		
		myAgent.send(messageToBeSent);
	}
	
	/**Wysyłanie wiadomości do listy agentów*/
	private void sendMessageToList(AID[] agentList, String content, int performative) {
		
		ACLMessage messageToBeSent = new ACLMessage(performative);
		messageToBeSent.setContent(content);		
	
		for(AID receiver: agentList) {
			messageToBeSent.addReceiver(receiver);	//Czy ta funkcja nadpisuje?
			myAgent.send(messageToBeSent);
		}
		
	}
	
	private void incrementDesksTaken(DeskAgent deskAgent){
		deskAgent.setDesksTaken(deskAgent.getDesksTaken() + 1);
	}
	
	private void addEmployeeToList(AID employee, DeskAgent deskAgent){
		deskAgent.addEmployeeToList(employee);
	}
	
	private boolean shouldAuctionEnd(DeskAgent deskAgent){
		return deskAgent.getAmountOfEmployees() <= deskAgent.getDesksTaken() && deskAgent.minTimeElapsed();
	}
	
	private void endAuction(DeskAgent deskAgent){
		
		System.out.println("End Auction!");
		sendMessage(deskAgent.getWinningEmployee(),
			    "End",
			    ACLMessage.INFORM);
		
		//TODO: Co ma zrobić agent, który sam zakończył aukcje?
	}
	
}
