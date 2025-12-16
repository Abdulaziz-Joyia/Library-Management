public interface Borrowable {
    void borrow() throws BookNotAvailableException;
    void returnItem();

    boolean isBorrowed();
}


