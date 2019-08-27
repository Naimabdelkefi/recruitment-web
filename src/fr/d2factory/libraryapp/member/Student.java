package d2factory.libraryapp.member;

import d2factory.libraryapp.library.NotEnoughMoneyException;

public class Student extends Member {
	private static final int daysBeforeLate=60;
	private static final int priceBeforeLate=10;
	private static final int priceAfterLate=15;
	private static final int freeFirstYear=15;
	
	private boolean firstYear;

	public Student(float wallet, boolean firstyear) {
		super(wallet,daysBeforeLate);
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
		
		if (numberOfDays<=daysBeforeLate) {
			bill=numberOfDays*priceBeforeLate;
		}
		else
		{
			bill=(numberOfDays-daysBeforeLate)*priceAfterLate+daysBeforeLate*priceAfterLate;
		}
		if (this.isFirstYear()) {
			 bill -= ((numberOfDays > freeFirstYear) ? freeFirstYear : numberOfDays)*priceBeforeLate;
		}
        if(this.getWallet() > bill) {
            this.setWallet(this.getWallet() - bill);
        } else {
            throw new NotEnoughMoneyException();
        }
		
	}

}
