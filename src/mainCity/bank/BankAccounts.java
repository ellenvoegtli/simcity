package mainCity.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mainCity.Person;

public class BankAccounts {

	public List<BankAccount> accounts = Collections.synchronizedList(new ArrayList<BankAccount>());
	
	public class BankAccount{
		
	    String name;
	    int creditScore;
	    double balance;
	    double debt;
	    double accountNumber;
	    Person p;
	    
	    public BankAccount(String n, double a, Person pa, double newaccnum){
	    	name=n;
	    	creditScore=830;
	    	balance = a;
	    	debt=0;
	    	accountNumber=newaccnum;
	    	p=pa;
	    	
	    }
	    
	}
	
	public double getNumberOfAccounts(){
		return accounts.size();
		
	}
	public void addAccount(String cname, double amount, Person pa, double newaccnum){
		accounts.add(new BankAccount(cname, amount, pa, newaccnum));
		System.out.println("Account added");
		
	}
	
	
	
}
