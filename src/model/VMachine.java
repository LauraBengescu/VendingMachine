package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class VMachine {
	
	private Scanner scanner;
	public HashMap<Integer,Product> stock;
	public HashMap<String, Integer> moneyChange;
	public Map<Double, String> moneyMap;
	public double moneyTotal;
	public double amountPaid;
	private Product inWorkProduct;
	public String state; 	
	
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
		Product Coke = new Product("Coke", 1, 10);
		Product Snickers = new Product("Snickers", 0.65, 20);
		Product MM = new Product ("M&M", 0.80, 15);
		Product PomBears = new Product("PomBears", 0.43, 17);
		stock.put(1,Coke); 
		stock.put(2, Snickers);
		stock.put(3,MM);
		stock.put(4, PomBears);
		moneyTotal = 100;
		moneyChange.put("1p", 100);
		moneyChange.put("2p", 200); 
		moneyChange.put("5p", 100);
		moneyChange.put("10p", 50);
		moneyChange.put("20p", 50);
		moneyChange.put("50p", 10);
		moneyChange.put("1P", 20);
		moneyChange.put("2P", 25);				
		
	}
		
	public void presentSelection() {
		Set<Integer> keys = stock.keySet();
		for (Integer key:keys) {
			System.out.println(key.toString() + " " + stock.get(key).name + " " + stock.get(key).price);
		}		
		System.out.println("# EXIT/CANCEL");
		handleSelection();
	}
	
	public void handleSelection() {
		System.out.println("Choose item");
		
		try {
			int itemCode = scanner.nextInt();
			Product product = stock.get(itemCode);
			if (product.count==0) 
			{
				System.out.println("Out of stock");
				return;
			}
			else {
				inWorkProduct = product;
				state = "HANDLING REQUEST";
			}
			System.out.println("Insert " + product.price);		
			handleMoney();
		}
		catch (NullPointerException e) {
		    System.err.println("Wrong input. Please try again");
		}
		catch (InputMismatchException e) {			
		    System.err.println("Session cancelled");		    
		}
		
	}
	
	private void handleMoney() {
		while (scanner.hasNextDouble()) {
			double p = scanner.nextDouble();
			if (p==0) {state = "CANCELLED"; cancel(); return;}
			amountPaid+=p;
			if (amountPaid < inWorkProduct.price) {
				System.out.println("Not enough money. " + (inWorkProduct.price-amountPaid) + " needed. ");
			}
			else {
				if (amountPaid == inWorkProduct.price) {
					inWorkProduct.count--;
					System.out.println(" Transaction done \n");
					break;
				}
				else {
					giveChange(amountPaid-inWorkProduct.price);	
					break;
				}
			}
		}
		
	}

	private void giveChange(double change) {		
		System.out.println("Change of " + change + " required");
		Set<Double> moneyTypes = moneyMap.keySet();
		ArrayList<Double> list = new ArrayList(moneyTypes);     
		Collections.sort(list);
		Collections.reverse(list);
		for (double moneyType:list) {
			if (change/moneyType>=1) {
				int numberOfCoins =  (int) (change/moneyType);
				String moneyName = moneyMap.get(moneyType);
				int num = moneyChange.get(moneyName);
				moneyChange.replace(moneyName, num, num-Math.min(numberOfCoins, num));
				change = change - moneyType*Math.min(numberOfCoins, num);
				System.out.println(Math.min(numberOfCoins, num) + " " + moneyName +  " coins");
			}
		}
		if (change!=0) {
			System.out.println("Not enough coins for change, please take the returned money and start again");
		}
		else {
			System.out.println("Transaction done");
			inWorkProduct.count--;
		}
		inWorkProduct = null;	
		amountPaid = 0;		
		
	}
	
	public void cancel() {
		giveChange(amountPaid);
		amountPaid=0;
		inWorkProduct=null;		
	}
	
	public void reloadMoney() {
		
	}
	
	public void reloadProducts() {
		
	}


}
