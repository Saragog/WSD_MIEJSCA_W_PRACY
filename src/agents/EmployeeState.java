package agents;

public enum EmployeeState {
	WAITING_FOR_PRICE_RESPONSES,
	HAS_NO_DESK_TAKEN,
	HAS_DESK_TAKEN,
	CALCULATING_NEW_OFFER,
	
	NOT_ENOUGH_MONEY_TO_BID_PREFERRED_DESK,	// nie mamy pieniedzy by obstawic zadne z 4 preferowanych
											// miejsc pracy, oczekuje ze miejsca pracy same przydziela
											// mi jedno miejsce
	WAITING_FOR_BID_RESPONSE,
	END
}
