public class PrintedBook extends Book implements Borrowable {

   private final String isbn;
    private final int pages;
    private boolean borrowed = false;
//Ye constructor hai jisme PrintedBook ka object create hota hai.
    public PrintedBook(int id, String title, String author, double price, String isbn, int pages) {
        //Parent class <Book> ka constructor call ho raha hai.
        super(id, title, author, price );
        this.isbn = isbn;
        this.pages = pages;
    }

    // ------------------ Getters ------------------
    public String getIsbn() {
        return isbn;
    }

    public int getPages() {
        return pages;
    }

    @Override
    public String getInfo() {
        return String.format(
                "PrintedBook (ID:%d) \"%s\" by %s | ISBN:%s | %d pages | ₹%.2f | Borrowed:%s",
                getId(), getTitle(), getAuthor(), getIsbn(),  getPrice(), isBorrowed()
        );
    }

    // ------------------ Borrowable Implementations ------------------

    @Override
    public void borrow() throws BookNotAvailableException {
        if (borrowed) {
            throw new BookNotAvailableException("Already borrowed: " + getTitle());
        }
        borrowed = true;
    }

    @Override
    public void returnItem() {
        borrowed = false;
    }

    @Override
    public boolean isBorrowed() {
        return borrowed;
    }
}
