package mainCity.restaurants.restaurant_zhangdt.sharedData;

import java.util.Vector;

public class RevolvingStand extends Object {
	private final int N = 5; 
	private int count = 0; 
	private Vector<OrderTicket> orderTickets; 
	
	public RevolvingStand(){ 
		orderTickets = new Vector<OrderTicket>(); 
	}
	
	synchronized public void insert (OrderTicket order) { 
		if(count == N) { 
			System.out.println("Stand is Full"); 
			return;
		}
	
	
		insert_item(order); 
		count++; 
		if(count == 1) { 
			System.out.println("Not empty"); 
			notify(); 
		} 
	}
	
	synchronized public OrderTicket remove() { 
		if(count == 0) { 
			return null; 
		} 
		
		OrderTicket order = remove_item(); 
		count--; 
		
		if(count == N-1){ 
			System.out.println("Not full, notify"); 
			notify(); 
		} 
		return order; 
	}
	
	private void insert_item(OrderTicket order) { 
		orderTickets.addElement(order); 
	}
	
	private OrderTicket remove_item() { 
		OrderTicket data = (OrderTicket) orderTickets.firstElement(); 
		orderTickets.removeElementAt(0); 
		return data; 
	}
	
	public boolean isFull() { 
		return (count == N); 
	}
	
	public boolean isEmpty() { 
		return orderTickets.isEmpty(); 
	}
}

