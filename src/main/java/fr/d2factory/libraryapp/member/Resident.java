package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.NotEnoughMoneyException;

public class Resident extends Member {
	public static final int PRICE_BEFORE_LATE=10;
	public static final int PRICE_AFTER_LATE =20;
	public static final int DAYS_BEFORE_LATE =60;


	public Resident(float wallet) {
		super(wallet, DAYS_BEFORE_LATE);
			}

	@Override
	public void payBook(int numberOfDays) throws NotEnoughMoneyException {
		int bill;
		if (numberOfDays<= DAYS_BEFORE_LATE) {
			bill=numberOfDays* PRICE_BEFORE_LATE;
		}
		else {
			bill=((numberOfDays- DAYS_BEFORE_LATE)* PRICE_AFTER_LATE)+ DAYS_BEFORE_LATE * PRICE_BEFORE_LATE;
		}
        if(this.getWallet() >= bill) {
            this.setWallet(this.getWallet() - bill);
        } else {
            throw new NotEnoughMoneyException();
        }
	}

}
