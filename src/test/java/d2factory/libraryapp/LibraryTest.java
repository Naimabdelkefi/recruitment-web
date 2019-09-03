package d2factory.libraryapp;

import d2factory.libraryapp.book.Book;
import d2factory.libraryapp.book.BookRepository;
import d2factory.libraryapp.book.ISBN;
import d2factory.libraryapp.library.*;
import d2factory.libraryapp.member.Member;
import d2factory.libraryapp.member.Resident;
import d2factory.libraryapp.member.Student;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


import static org.junit.Assert.fail;

public class LibraryTest {
    private Library library;
    private BookRepository bookRepository;
    private List<Book> bookList;

    private Collection<Book> deserialize() {
        JSONParser jsonParser = new JSONParser();
        try {
            Object obj = jsonParser.parse((new FileReader("src\\test\\resources\\books.json")));
            JSONArray bookList = (JSONArray) obj;
            List<Book> books = new ArrayList<>();
            for (Object bookObject : bookList) {
                JSONObject book = (JSONObject) bookObject;
                String Title = (String) book.get("title");
                String Author = (String) book.get("author");
                long Isbn = (long) ((JSONObject) book.get("isbn")).get("isbnCode");
                books.add(new Book(Title, Author, new ISBN(Isbn)));
            }
            return Collections.unmodifiableList(books);
        } catch (FileNotFoundException e) {
            fail("file not found");
        } catch (IOException | ParseException e) {
            fail("error while parsing the file");
        }
        return null;
    }

    @Before
    public void setup() {
        if (bookList == null) {
            bookList = (List<Book>) deserialize();
        }

        bookRepository = new BookRepository();
        bookRepository.addBooks(bookList);
        library = new LibraryImpl(bookRepository);

    }

    @Test(expected = Test.None.class)
    public void memberCanBorrowABookIfBookIsAvailable() {
        Member resident = new Resident(0);
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), resident, LocalDate.now());

        Member student = new Resident(0);
        library.borrowBook(bookList.get(1).getIsbn().getIsbnCode(), student, LocalDate.now());
    }

    @Test(expected = NoSuchBookException.class)
    public void borrowedBookIsNoLongerAvailable() {
        int wallet = 100;
        int days = 10;
        Book book = bookList.get(0);
        Member student1 = new Student(wallet, true);
        Member student2 = new Student(wallet, false);
        library.borrowBook(book.getIsbn().getIsbnCode(), student1, LocalDate.now().minusDays(days));
        library.borrowBook(book.getIsbn().getIsbnCode(), student2, LocalDate.now());
    }

    @Test
    public void residentsAreTaxed10CentsForEachDayTheyKeepABookBeforeTheInitial60Days() {
        int wallet = 100;
        int days = 5;
        Book book = bookList.get(0);
        Member resident = new Resident(wallet);
        library.borrowBook(book.getIsbn().getIsbnCode(), resident, LocalDate.now().minusDays(days));
        library.returnBook(book, resident);
        int prix = Resident.PRICE_BEFORE_LATE * days;
        Assert.assertEquals(wallet - prix, resident.getWallet(), 0);
    }

    @Test
    public void studentsPay10CentsTheFirst30Days() {
        int wallet = 300;
        int days = 30;
        Member student1 = new Student(wallet, true);
        Book book1 = bookList.get(0);
        library.borrowBook(book1.getIsbn().getIsbnCode(), student1, LocalDate.now().minusDays(days));
        library.returnBook(book1,student1);
        int prix = (days-Student.FREE_FIRST_YEAR)* Student.PRICE_BEFORE_LATE;
        Assert.assertEquals(wallet-prix, student1.getWallet(), 0);

        Member student2 = new Student(wallet, false);
        Book book2 = bookList.get(1);
        library.borrowBook(book2.getIsbn().getIsbnCode(), student2, LocalDate.now().minusDays(days));
        library.returnBook(book2, student2);
        prix = days* Student.PRICE_BEFORE_LATE;
        Assert.assertEquals(wallet-prix, student2.getWallet(), 0);
    }

    @Test
    public void studentsIn1StYearAreNotTaxedForTheFirst15Days() {
        int wallet = 300;
        Book book = bookList.get(0);
        Member student = new Student(wallet, true);
        library.borrowBook(book.getIsbn().getIsbnCode(), student, LocalDate.now().minusDays(15));
        library.returnBook(book, student);
        Assert.assertEquals(wallet, student.getWallet(), 0);
    }

    @Test
    public void studentsNotIn1StYearAreTaxedAfterTheInitial30Days() {
        int wallet=1000;
        int days=35;
        Member student = new Student(wallet, false);
        Book book = bookList.get(0);
        library.borrowBook(book.getIsbn().getIsbnCode(), student, LocalDate.now().minusDays(days));
        library.returnBook(book, student);
        int prix = ((Student.DAYS_BEFORE_LATE) * Student.PRICE_BEFORE_LATE) + ((days- Student.DAYS_BEFORE_LATE) * Student.PRICE_AFTER_LATE);
        Assert.assertEquals(wallet - prix, student.getWallet(), 0);
    }

    @Test
    public void studentsTaxedForEachDayTheyKeepABookAfterTheInitial30Days() {
        Book book = bookList.get(0);
        int wallet=1000;
        int days=45;
        Member student = new Student(1000, true);
        library.borrowBook(book.getIsbn().getIsbnCode(), student, LocalDate.now().minusDays(days));
        library.returnBook(book, student);
        int prix = ((Student.DAYS_BEFORE_LATE - Student.FREE_FIRST_YEAR) * Student.PRICE_BEFORE_LATE) + ((days - Student.DAYS_BEFORE_LATE) * Student.PRICE_AFTER_LATE);
        Assert.assertEquals(wallet - prix, student.getWallet(), 0);
    }

    @Test
    public void residentsTaxedForEachDayTheyKeepABookAfterTheInitial60Days() {
        Book book = bookList.get(0);
        int wallet=1000;
        int days=72;
        Member resident = new Resident(wallet);
        library.borrowBook(book.getIsbn().getIsbnCode(), resident, LocalDate.now().minusDays(days));
        library.returnBook(book, resident);
        int prix = (Resident.DAYS_BEFORE_LATE* Resident.PRICE_BEFORE_LATE) + ((days - Resident.DAYS_BEFORE_LATE) * Resident.PRICE_AFTER_LATE);
        Assert.assertEquals(wallet- prix, resident.getWallet(), 0);
    }

    @Test(expected = HasLateBooksException.class)
    public void residentCannotBorrowBookIfTheyHaveLateBooks() {
        int wallet=2000;
        int days=70;
        Member resident = new Resident(wallet);
        Book book1 = bookList.get(0);
        Book book2 = bookList.get(1);
        library.borrowBook(book2.getIsbn().getIsbnCode(), resident, LocalDate.now().minusDays(days));
        library.borrowBook(book1.getIsbn().getIsbnCode(), resident, LocalDate.now());
    }

    @Test(expected = HasLateBooksException.class)
    public void studentCannotBorrowBookIfTheyHaveLateBooks() {
        int wallet=2000;
        int days=70;
        Member student = new Student(wallet,true);
        Book book1 = bookList.get(0);
        Book book2 = bookList.get(1);
        library.borrowBook(book1.getIsbn().getIsbnCode(), student, LocalDate.now().minusDays(days));
        library.borrowBook(book2.getIsbn().getIsbnCode(), student, LocalDate.now());
    }


    @Test(expected = NotEnoughMoneyException.class)
    public void residentCannotBorrowBookIfTheyDontHaveEnoughMoney() {
        int wallet=0;
        int days=10;
        Book book=bookList.get(0);
        Member resident = new Resident(wallet);
        library.borrowBook(book.getIsbn().getIsbnCode(), resident, LocalDate.now().minusDays(days));
        library.returnBook(book, resident);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void studentCannotBorrowBookIfTheyDontHaveEnoughMoney() {
        int wallet=0;
        int days=16;
        Book book=bookList.get(0);
        Member student = new Student(wallet,true);
        library.borrowBook(book.getIsbn().getIsbnCode(), student, LocalDate.now().minusDays(days));
        library.returnBook(book, student);
    }
}