package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class VMachine {
	
	private Scanner scanner;
	public HashMap<Integer,Product> stock; //hashmap with codes for each product
	public HashMap<String, Integer> moneyChange; // hashmap of each coin and the number of them
	public Map<Double, String> moneyMap; // map from numbers to strings for coin denominations
	public double moneyTotal; 
	public double amountPaid; // money introduced in the vending machine as the product is being paid for
	private Product inWorkProduct; // product that is asked for
	public int productsTotal; 	// total number of products
	
	public VMachine() {
		this.stock = new HashMap<Integer, Product>();
		this.moneyChange = new HashMap<String, Integer>();
		this.moneyMap = new HashMap<Double, String>();
		moneyMap.put(0.01, "1p");
		moneyMap.put(0.02, "2p");
		moneyMap.put(0.05, "5p");
		moneyMap.put(0.1, "10p");
		moneyMap.put(0.2, "20p");
		moneyMap.put(0.5, "50p");
		moneyMap.put((double) (1), "1P");
		moneyMap.put((double) (2), "2P");
		this.amountPaid = 0;
		this.scanner = new Scanner(System.in);
	}
	
	public void initialise() {
		//adding products and initialising their counts
		Product Coke = new Product("Coke", 1, 10);
		Product Snickers = new Product("Snickers", 0.65, 20);
		Product MM = new Product ("M&M", 0.80, 15);
		Product PomBears = new Product("PomBears", 0.43, 17);
		this.productsTotal = 62;
		stock.put(1, Coke); 
		stock.put(2, Snickers);
		stock.put(3, MM);
		stock.put(4, PomBears);
		//adding money and adding the coins in
		this.moneyTotal = 100;	
		reloadMoney();		
	}
		
	public void presentSelection() {
		//displaying the menu
		System.out.println ("TOTAL MONEY " + this.moneyTotal);
		Set<Integer> keys = stock.keySet();
		for (Integer key:keys) {
			System.out.println(key.toString() + " " + stock.get(key).name + " " + stock.get(key).price);
		}		
		System.out.println("0 EXIT/CANCEL");
		handleSelection();
	}
	
	public void handleSelection() {
		System.out.println("Choose item");				
		if (scanner.hasNextInt()) { 
			int itemCode = scanner.nextInt();
			if (itemCode == 0) {
				System.out.println("You cancelled the selection. Try again."); 
				handleSelection();
			}
			if (stock.containsKey(itemCode)) {
				Product product = stock.get(itemCode);
				if (product.count==0) 
					{
						System.out.println("Out of stock");
						return;
					}
					else {
						inWorkProduct = product;
					}
					System.out.println("Insert " + product.price + " in coins");		
					handleMoney();
			}
			else{
				System.err.println("Wrong input. Please try again"); //if not a valid selection, but still a number
			}				
		}
		else {
			System.err.println("Wrong input. Please try again"); //not a valid selection, but random input (not int)
			scanner.next();
		}
		
	}
	
	private void handleMoney() {
		//there is a product that is being taken from the machine, and coins are inserted
		while (inWorkProduct!=null && scanner.hasNextDouble()) { 
			double p = scanner.nextDouble();
			if (p==0) {cancel(); break;}
			Set<Double> moneySet = moneyMap.keySet();
			if (!moneySet.contains(p)) { //check to see if valid coin
				System.out.println("Not the right coin denomination. Please take back and retry with different coin ");
				continue;
			}
			int count = moneyChange.get(moneyMap.get(p));
			moneyChange.put(moneyMap.get(p), count++); //adding coin to the count of money 
			p=p*100;
			amountPaid+=p;
			giveChange(amountPaid-inWorkProduct.price*100); //calculate change
		}
		if (inWorkProduct!=null && scanner.hasNext()) { //wrong input for coins, not a number (example 0,2 instead of 0.2)
			scanner.next();
			System.out.println("Wrong input. Please input more money or cancel."); 
			handleMoney();
		}
		
	}

	private void giveChange(double change) {
		// *100, and then /100 is used since total and prices are calculated in pence and then displayed as total in pounds and pence. 
		// This was done because of double to properly calculate money change and totals using just the 0.00 format 
		if (change < 0) { //not paid yet
			System.out.println("Not enough money. " + Math.abs(change/100) + " more needed. ");
			return;
		}
		else {
			if (change!=0)  //paid, but change needed 
			{
				System.out.println("Change of " + change/100 + " required");
				Set<Double> moneyTypes = moneyMap.keySet();
				ArrayList<Double> list = new ArrayList<Double>(moneyTypes);     
				Collections.sort(list);
				Collections.reverse(list);
				for (double moneyType:list) { //greedy alg for giving back change, going from highest coin to lowest 
					if ((change/100)/moneyType>=1) {
						int numberOfCoins =  (int) ((change/100)/moneyType);
						String moneyName = moneyMap.get(moneyType);
						int num = moneyChange.get(moneyName);
						moneyChange.replace(moneyName, num, num-Math.min(numberOfCoins, num)); //checking to see if enough coins of this denomination to pay, and taken out
						change = change - moneyType*100*Math.min(numberOfCoins, num);
						System.out.println(Math.min(numberOfCoins, num) + " " + moneyName +  " coins");
					}
				}
			}
		}
		if (change!=0) {
			System.out.println("Not enough coins for change, please take the returned money and start again \n");
			cancel();
		}
		else {
			if (inWorkProduct!=null)
			{
				System.out.println("Transaction done");
				inWorkProduct.count--;
				productsTotal--;
				moneyTotal=moneyTotal*100 + inWorkProduct.price*100; //successful, total added to money. 
				moneyTotal=moneyTotal/100;
			}
		}
		
		inWorkProduct = null;	
		amountPaid = 0;	

	}
	
	public void cancel() { 
		System.out.println("You cancelled the selection. Try again. \n");
		inWorkProduct=null;	//no working product
		giveChange(amountPaid); //give money back if any introduced
		amountPaid=0; // reset to 0
		System.out.println();
		
	}
	
	public void reloadMoney() { //reloading

		moneyChange.put("1p", 100);
		moneyChange.put("2p", 200); 
		moneyChange.put("5p", 100);
		moneyChange.put("10p", 50);
		moneyChange.put("20p", 50);
		moneyChange.put("50p", 10);
		moneyChange.put("1P", 20);
		moneyChange.put("2P", 25);	
		
	}
	
	public void reloadProducts() { //reloading products
		Collection<Product> products = stock.values();
		for (Product product:products) {
			product.count=20;
		}	
		
	}


}
