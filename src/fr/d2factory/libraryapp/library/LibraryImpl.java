package d2factory.libraryapp.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import d2factory.libraryapp.book.Book;
import d2factory.libraryapp.book.BookRepository;
import d2factory.libraryapp.member.Member;

public class LibraryImpl implements Library {
	private BookRepository bookRepository;
	public LibraryImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException, NoSuchBookException {
		Book borrowedBook = bookRepository.findBook(isbnCode);
		if (borrowedBook==null) {
			throw new NoSuchBookException();
		}
		for (Book book : member.getBrrowedBook()) {
			LocalDate borrowDate= bookRepository.findBorrowedBookDate(book);
			long daysBetween = ChronoUnit.DAYS.between(borrowDate, LocalDate.now());
			if (daysBetween>member.getDaysBeforeLate()) {
			 throw new HasLateBooksException();	
			}
		}
		bookRepository.saveBookBorrow(borrowedBook, borrowedAt);
		member.getBrrowedBook().add(borrowedBook);
		return borrowedBook;
	}

	@Override
	public void returnBook(Book book, Member member) {
		LocalDate borrowDate= bookRepository.findBorrowedBookDate(book);
		int daysBetween = (int) ChronoUnit.DAYS.between(borrowDate, LocalDate.now());
		member.payBook(daysBetween);
	}

}
