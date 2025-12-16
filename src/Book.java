public abstract class Book {
    private final int id;
    private final String title;
    private  final String author;
    private double price;

//Constractor ha --->Jab Novel/TextBook banti hai, sabse pehle ye constructor chalhta hai.
    public Book( int id ,String title, String author, double price) {
        //Object ka ID set kar raha hai.
        this.id =  id;
        this.title = title;
        this.author = author;
        this.price = price;
    }


//ID read karne ke liye getter method.
    public  int getId(){return id;}


    public String getTitle() {return title;}


    public String getAuthor() {return author;}


    public double getPrice() {return price;}



    public void setPrice(double price) {this.price = price;}



       public  abstract String getInfo() throws BookNotAvailableException;

}
//file k sequence ka sath read krna
//Book.java 1
//
//PrintedBook.java 2
//
//Novel.java 3
//
//TextBook.java ← Yeh next explain karna chahiye 4
//
//EBook.java (agar project me hai)
//
//Borrowable.java 5
//
//BookNotAvailableException.java 6
//
//Library.java 7
//
//Main.java (jo program run karta hai) 8



