package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.NotEnoughMoneyException;

public class Resident extends Member {
	private static final int priceBeforeLate=10;
	private static final int priceAfterLate=20;
	private static final int daysBeforeLate=60;


	public Resident(float wallet) {
		super(wallet);
			}

	@Override
	public void payBook(int numberOfDays) {
		int bill;
		if (numberOfDays<=daysBeforeLate) {
			bill=numberOfDays*priceBeforeLate;
		}
		else {
			this.setLate(true);
			bill=(numberOfDays-daysBeforeLate)*priceAfterLate+daysBeforeLate*priceAfterLate;
		}
        if(this.getWallet() > bill) {
            this.setWallet(this.getWallet() - bill);
        } else {
            throw new NotEnoughMoneyException();
        }
	}

}
