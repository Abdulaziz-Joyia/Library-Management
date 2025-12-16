
public class TextBook extends PrintedBook {
    private  final String subject;

    public TextBook(int id, String isbn, String pages, double title, String author, double price, String subject) {
        super( id,isbn,pages,title, author, (int) price);
        this.subject = subject;
    }



    @Override
    public String getInfo() {
        return String.format("TextBook(ID:%d) \"%s\" by %s | Subject:%s | ISBN:%s | ₹%.2f | borrowed:%s",
                getId(), getTitle(), getAuthor(), subject, getIsbn(), getPrice(), isBorrowed());
    }

    @Override
    public boolean isBorrowed() {
        return false;
    }
}


