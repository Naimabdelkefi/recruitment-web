package fr.d2factory.libraryapp.member;

public class Student extends Member {
	private static final int priceBeforeLate=10;
	private static final int priceAfterLate=15;
	private static final int daysBeforeLate=30;
	private static final int freeFirstYear=15;
	
	private boolean firstYear;
	public Student(float wallet) {
		super(wallet);
		// TODO Auto-generated constructor stub
	}

	public boolean isFirstYear() {
		return firstYear;
	}

	public void setFirstYear(boolean firstYear) {
		this.firstYear = firstYear;
	}

	@Override
	public void payBook(int numberOfDays) {
		int bill;
		
		if (numberOfDays<daysBeforeLate) {
			bill=numberOfDays*priceBeforeLate;
		}
		else
		{
			this.setLate(true);
			bill=(numberOfDays-daysBeforeLate)*priceAfterLate+daysBeforeLate*priceAfterLate;
		}
		if (this.isFirstYear()) {
			 bill -= ((numberOfDays > freeFirstYear) ? freeFirstYear : numberOfDays)*priceBeforeLate;
		}
		
	}

}
