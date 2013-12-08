package mainCity.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mainCity.PersonAgent;

public class BankAccounts {

	public List<BankAccount> accounts = Collections.synchronizedList(new ArrayList<BankAccount>());
	public double FDICfund = 250000;
	
	public class BankAccount{
		
	    public String name;
	    public int creditScore;
	    public double balance;
	    public double debt;
	    public double accountNumber;
	    public PersonAgent p;
	    
	    public BankAccount(String n, double a, PersonAgent pa, double newaccnum){
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
	public void addAccount(String cname, double amount, PersonAgent pa, double newaccnum){
		accounts.add(new BankAccount(cname, amount, pa, newaccnum));
		System.out.println("Account added");
		
	}
	
	
	
}
