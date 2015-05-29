package buy;

import model.*;

public class Buy {
	
	public static void main(String[] args) {
		VMachine vendingMachine = new VMachine();
		System.out.println("Vending Machine ");
		vendingMachine.initialise();
		while (true) {
			vendingMachine.presentSelection();
			System.out.println("done");
		}
		
	}

}
