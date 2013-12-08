package mainCity.restaurants.marcusRestaurant.sharedData;
import java.util.Vector;

public class RevolvingStand extends Object {
    private final int N = 5;
    private int count = 0;
    private Vector<OrderTicket> theData;
    
    public RevolvingStand(){
        theData = new Vector<OrderTicket>();
    }
    
    synchronized public void insert(OrderTicket order) {
        if(count == N) {
        	System.out.println("Stand is Full");
        	return;
        }
        
    	/*
        while (count == N) {
            try{ 
                System.out.println("\tFull, waiting");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
        */
            
        insert_item(order);
        count++;
        if(count == 1) {
            //System.out.println("\tNot Empty, notify");
            notify();                               // Not empty, notify a 
                                                    // waiting consumer
        }
    }
    
    synchronized public OrderTicket remove() {
        if(count == 0) return null;
        
    	/*
    	while(count == 0)
            try{ 
                System.out.println("\tEmpty, waiting");
                wait(5000);                         // Empty, wait to consume
            } catch (InterruptedException ex) {};
         */
    	OrderTicket order = remove_item();
        count--;
        
        if(count == N-1){ 
            System.out.println("\tNot full, notify");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return order;
    }
    
    private void insert_item(OrderTicket order){
        theData.addElement(order);
    }
    
    private OrderTicket remove_item(){
    	OrderTicket data = (OrderTicket) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }
    
    public boolean isFull() {
    	return (count == N);
    }
    
    public boolean isEmpty() {
    	return theData.isEmpty();
    }
}
