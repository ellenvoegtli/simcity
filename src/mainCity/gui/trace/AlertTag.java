package mainCity.gui.trace;

/**
 * These enums represent tags that group alerts together.  <br><br>
 * 
 * This is a separate idea from the {@link AlertLevel}.
 * A tag would group all messages from a similar source.  Examples could be: BANK_TELLER, RESTAURANT_ONE_WAITER,
 * or PERSON.  This way, the trace panel can sort through and identify all of the alerts generated in a specific group.
 * The trace panel then uses this information to decide what to display, which can be toggled.  You could have all of
 * the bank tellers be tagged as a "BANK_TELLER" group so you could turn messages from tellers on and off.
 * 
 * @author Keith DeRuiter
 *
 */
public enum AlertTag {
    	GENERAL_CITY,
	
        PERSON,
        
        OCCUPANT,
        LANDLORD,
        BUS_STOP,
        
        BANK_BANKER,
        BANK_TELLER,
        BANK_CUSTOMER,
        BANK_MANAGER,
        BANK_ROBBER,
        BANK,
                
        MARKET_CUSTOMER,
        MARKET_EMPLOYEE,
        MARKET_CASHIER,
        MARKET_GREETER,
        MARKET_DELIVERYMAN,
        MARKET,
        
        //RESTAURANT,
        
        ELLEN_RESTAURANT,
        ELLEN_HOST,
        ELLEN_WAITER,
        ELLEN_CUSTOMER,
        ELLEN_CASHIER,
        ELLEN_COOK,
        
        MARCUS_RESTAURANT,
        MARCUS_HOST,
        MARCUS_WAITER,
        MARCUS_CUSTOMER,
        MARCUS_CASHIER,
        MARCUS_COOK,
        
        DAVID_RESTAURANT,
        DAVID_HOST,
        DAVID_WAITER,
        DAVID_CUSTOMER,
        DAVID_CASHIER,
        DAVID_COOK,
        
        ENA_RESTAURANT,
        ENA_HOST,
        ENA_WAITER,
        ENA_CUSTOMER,
        ENA_CASHIER,
        ENA_COOK,
        
        JEFFERSON_RESTAURANT,
        JEFFERSON_HOST,
        JEFFERSON_WAITER,
        JEFFERSON_CUSTOMER,
        JEFFERSON_CASHIER,
        JEFFERSON_COOK
}