public class Novel extends PrintedBook {

    private final String genre;

    // ONLY ONE VALID CONSTRUCTOR
    public Novel(int id, String title, String author, double price, String isbn, int pages, String genre) {
        super(id, title, author, price, isbn, pages);
        this.genre = genre;
    }






    @Override
    public String getInfo() {
        return String.format(
                "Novel (ID:%d) \"%s\" by %s | Genre:%s | ISBN:%s | ₹%.2f | Borrowed:%s",
                getId(), getTitle(), getAuthor(), genre, getIsbn(), getPrice(), isBorrowed()
        );
    }
}
//%d → book id
//
//\"%s\" → book title in quotes
//
//next %s → author
//
//Genre:%s → genre field
//
//ISBN:%s → getIsbn() (PrintedBook method)
//
//%.2f → price with 2 decimal places
//
//Borrowed:%s → isBorrowed() returns boolean (true/false)
//
//getId(), getTitle(), getAuthor() — ye methods Book/PrintedBook se aate hain (inheritance).
//
//genre — Novel ka apna field.
//
//getIsbn(), getPrice(), isBorrowed() — must be present in parent classes (PrintedBook should provide getIsbn() and isBorrowed()).