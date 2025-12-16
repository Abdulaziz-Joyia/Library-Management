import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String BOOKS_FILE = "books.csv";
    private static final String MEMBERS_FILE = "members.csv";

    public static void main(String[] args) {
        Library lib = new Library();

        // try load existing data
        try {
            lib.loadBooksFromFile(BOOKS_FILE);
            lib.loadMembersFromFile(MEMBERS_FILE);
        } catch (IOException e) {
            System.out.println("No saved data found or error reading files.");
        }

        // seed if empty
        if (lib.listAllBooksSortedByTitle().isEmpty()) {
            //lib.seedSampleData(); //
        }

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": // list
                        lib.listAllBooksSortedByTitle().forEach(b -> System.out.println(b.getInfo()));
                        break;
                    case "2": // search
                        System.out.print("Enter title to search: ");
                        String q = sc.nextLine();
                        List<Book> results = lib.searchByTitle(q);
                        if (results.isEmpty()){
                            System.out.println("No results.");
                        }
                        else {
                            results.forEach(b -> System.out.println(b.getInfo()));
                        }
                        break;
                    case "3": // add novel
                        System.out.print("Title: "); String t = sc.nextLine();
                        System.out.print("Author: "); String a = sc.nextLine();
                        System.out.print("Price: "); int p = Integer.parseInt(sc.nextLine());
                        System.out.print("ISBN: "); String isbn = sc.nextLine();
                        System.out.print("Pages: "); int pages = Integer.parseInt(sc.nextLine());
                        System.out.print("Genre: "); String genre = sc.nextLine();
                        int newId = lib.addNewNovel(t,a,p,isbn,pages,genre);
                        System.out.println("Added Novel with ID: " + newId);
                        break;
                    case "4": // add textbook
                        System.out.print("Title: "); String tt = sc.nextLine();
                        System.out.print("Author: "); String aa = sc.nextLine();
                        System.out.print("Price: "); double pp = Double.parseDouble(sc.nextLine());
                        System.out.print("ISBN: "); String isb = sc.nextLine();
                        System.out.print("Pages: "); int pg = Integer.parseInt(sc.nextLine());
                        System.out.print("Subject: "); String subj = sc.nextLine();
                        int nt = lib.addNewTextBook(tt, aa, pp, isb, pg, subj);
                        System.out.println("Added TextBook with ID: " + nt);
                        break;
                    case "5": // add member
                        System.out.print("Member name: ");
                        String name = sc.nextLine();
                        int mid = lib.addMember(name);
                        System.out.println("Member added ID: " + mid);
                        break;
                    case "6": // borrow
                        System.out.print("Member ID: "); int mem = Integer.parseInt(sc.nextLine());
                        System.out.print("Book ID: "); int bid = Integer.parseInt(sc.nextLine());
                        System.out.print("Days to borrow (e.g. 14): "); int days = Integer.parseInt(sc.nextLine());
                        try {
                            lib.borrowBook(mem, bid, days);
                            System.out.println("Borrowed successfully. Due in " + days + " days.");
                        } catch (BookNotAvailableException ex) {
                            System.out.println("Cannot borrow: " + ex.getMessage());
                        }
                        break;
                    case "7": // return
                        System.out.print("Member ID: "); int rmem = Integer.parseInt(sc.nextLine());
                        System.out.print("Book ID: "); int rbid = Integer.parseInt(sc.nextLine());
                        double fine = lib.returnBook(rmem, rbid);
                        if (fine > 0) System.out.println("Book returned. Fine due: ₹" + fine);
                        else System.out.println("Book returned. No fine.");
                        break;
                    case "8": // show member borrow records
                        System.out.print("Member ID: "); int mm = Integer.parseInt(sc.nextLine());
                        lib.getBorrowRecordsForMember(mm).forEach(System.out::println);
                        break;
                    case "9": // save
                        try {
                            lib.saveBooksToFile(BOOKS_FILE);
                            lib.saveMembersToFile(MEMBERS_FILE);
                            System.out.println("Saved.");
                        } catch (IOException e) {
                            System.out.println("Save failed: " + e.getMessage());
                        }
                        break;
                    case "0":
                        // save on exit
                        try {
                            lib.saveBooksToFile(BOOKS_FILE);
                            lib.saveMembersToFile(MEMBERS_FILE);
                        }
                        catch (IOException e) { /* ignore */ }
                        running = false;
                        System.out.println("Bye.");
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            System.out.println();
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("===== Advanced Console Library =====");
        System.out.println("1) List all books");
        System.out.println("2) Search books by title");
        System.out.println("3) Add Novel");
        System.out.println("4) Add TextBook");
        System.out.println("5) Add Member");
        System.out.println("6) Borrow Book");
        System.out.println("7) Return Book");
        System.out.println("8) Show Member Borrow Records");
        System.out.println("9) Save data now");
        System.out.println("0) Exit (will save)");
        System.out.print("Choose: ");
    }
}
