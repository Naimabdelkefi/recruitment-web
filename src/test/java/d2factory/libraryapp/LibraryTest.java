package d2factory.libraryapp;

import d2factory.libraryapp.book.Book;
import d2factory.libraryapp.book.BookRepository;
import d2factory.libraryapp.book.ISBN;
import d2factory.libraryapp.library.HasLateBooksException;
import d2factory.libraryapp.library.Library;
import d2factory.libraryapp.library.LibraryImpl;
import d2factory.libraryapp.library.NoSuchBookException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

public class LibraryTest {
    private Library library ;
    private BookRepository bookRepository;
    private List<Book> bookList;

    @Before
    public void setup(){
        bookRepository = new BookRepository();

        library = new LibraryImpl(bookRepository);
        JSONParser jsonParser = new JSONParser();

        try
        {
            Object obj = jsonParser.parse((new FileReader("src\\test\\resources\\books.json")));
            JSONArray bookList = (JSONArray) obj;
            List<Book> books = new ArrayList<>();
            for (Object b : bookList) {
                JSONObject book = (JSONObject) b;
                String Title = (String) book.get("title");
                String Author = (String) book.get("author");
                long Isbn = (long) ((JSONObject) book.get("isbn")).get("isbnCode");
                books.add(new Book(Title, Author, new ISBN(Isbn)));
            }
            this.bookList = Collections.unmodifiableList(books);

           // bookList.addAll(books);
        }
        catch (FileNotFoundException e){
            fail("file not found");
        }
        catch (IOException |  ParseException  e) {
            fail("error while parsing the file");
        }
        bookRepository.addBooks(bookList);
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available(){
        fail("Implement me");
    }

    @Test
    public void borrowed_book_is_no_longer_available(){
    	 Member m1 = new Student(1000,true);
    	 Member m2 = new Student(1000,false);
        System.out.println(bookList.get(0).getIsbn().getIsbnCode());
    	 library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(),m1,LocalDate.now().minusDays(10));
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m2, LocalDate.now());
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
        Member m1 = new Resident(100);
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(5));
        library.returnBook(bookList.get(0), m1);
        Assert.assertEquals( 50 , m1.getWallet(),0);
    }

    @Test
    public void students_pay_10_cents_the_first_30days(){
        Member m1 = new Student(300,true);

        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(30));
        library.returnBook(bookList.get(0), m1);
        Assert.assertEquals( 150, m1.getWallet(),0);
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
        Member m1 = new Student(1000,true);
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(15));
        library.returnBook(bookList.get(0), m1);
        Assert.assertEquals( 1000 , m1.getWallet(),0);
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){
        Member m1 = new Student(1000,true);
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(40));
        library.returnBook(bookList.get(0), m1);
        int prix =((30-15)*10)+((40 - 30)*15);
        Assert.assertEquals( 1000 - prix, m1.getWallet(), 0);
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
        Member m1 = new Resident(2000);

        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(70));
        library.returnBook(bookList.get(0), m1);

        int prix = (60*10) + ((70 - 60)*20);
        Assert.assertEquals( 2000 - prix, m1.getWallet(),0);
    }

    @Test(expected = HasLateBooksException.class)
    public void members_cannot_borrow_book_if_they_have_late_books(){
        Member m1 = new Resident(2000);
        library.borrowBook(bookList.get(0).getIsbn().getIsbnCode(), m1, LocalDate.now().minusDays(70));
        library.borrowBook(bookList.get(1).getIsbn().getIsbnCode(), m1, LocalDate.now());
    }
}