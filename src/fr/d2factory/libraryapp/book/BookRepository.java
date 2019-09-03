package d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<ISBN, LocalDate> borrowedBooks = new HashMap<>();

    public Map<ISBN, Book> getAvailableBooks() {
        return availableBooks;
    }

    public Map<ISBN, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void addBooks(List<Book> books){
    	for (Book book : books)
    	{
    		availableBooks.put(book.getIsbn(), book);
    	}
        
    }

    public Book findBook(long isbnCode) {
    	ISBN isbn=new ISBN(isbnCode);
        Book b=availableBooks.get(isbn);
        return b;
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
    	
    	availableBooks.remove(book.getIsbn());
        borrowedBooks.put(book.getIsbn(), borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book.getIsbn());
    }
}
