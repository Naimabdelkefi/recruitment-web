package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.NotEnoughMoneyException;

public class Student extends Member {
	public static final int DAYS_BEFORE_LATE =30;
	public static final int PRICE_BEFORE_LATE =10;
	public static final int PRICE_AFTER_LATE =15;
	public static final int FREE_FIRST_YEAR =15;
	
	private boolean firstYear;

	public Student(float wallet, boolean firstyear) {
		super(wallet, DAYS_BEFORE_LATE);
		this.firstYear=firstyear;
	}

	public boolean isFirstYear() {
		return firstYear;
	}

	public void setFirstYear(boolean firstYear) {
		this.firstYear = firstYear;
	}

	@Override
	public void payBook(int numberOfDays) throws NotEnoughMoneyException {
		int bill;
		
		if (numberOfDays<= DAYS_BEFORE_LATE) {
			bill=numberOfDays* PRICE_BEFORE_LATE;
		}
		else
		{
			bill=((numberOfDays- DAYS_BEFORE_LATE)* PRICE_AFTER_LATE)+ DAYS_BEFORE_LATE * PRICE_BEFORE_LATE;
		}
		if (this.isFirstYear()) {
			 bill -= ((numberOfDays > FREE_FIRST_YEAR) ? FREE_FIRST_YEAR : numberOfDays)* PRICE_BEFORE_LATE;
		}
        if(this.getWallet() >= bill) {
            this.setWallet(this.getWallet() - bill);
        } else {
            throw new NotEnoughMoneyException();
        }
	}

}
