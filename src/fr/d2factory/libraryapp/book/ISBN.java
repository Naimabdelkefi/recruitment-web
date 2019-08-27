package d2factory.libraryapp.book;

public class ISBN {
    public long getIsbnCode() {
        return isbnCode;
    }

    long isbnCode;

    public ISBN(long isbnCode) {
        this.isbnCode = isbnCode;
    }
}
