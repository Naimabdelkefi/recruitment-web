package fr.d2factory.libraryapp.book;

public class ISBN {
    public long getIsbnCode() {
        return isbnCode;
    }

    private long isbnCode;

    public ISBN(long isbnCode) {
        this.isbnCode = isbnCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(isbnCode);
    }
}
