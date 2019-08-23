package fr.d2factory.libraryapp.member;

import java.util.ArrayList;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.library.Library;

/**
 * A member is a person who can borrow and return books to a {@link Library}
 * A member can be either a student or a resident
 */
public abstract class Member {
    /**
     * An initial sum of money the member has
     */
	protected  boolean late=false;
    private float wallet;
    protected List<Book> brrowedBook = new ArrayList<>();
    
    /**
     * The member should pay their books when they are returned to the library
     *
     * @param numberOfDays the number of days they kept the book
     */
    public abstract void payBook(int numberOfDays);

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }
	public boolean is_Late() {
		return late;
	}

	public void setLate(boolean is_late) {
		this.late = is_late;
	}


	public List<Book> getBrrowedBook() {
		return brrowedBook;
	}

	public void setBrrowedBook(List<Book> brrowedBook) {
		this.brrowedBook = brrowedBook;
	}
	public Member(float wallet) {
		this.wallet = wallet;
	}
}
