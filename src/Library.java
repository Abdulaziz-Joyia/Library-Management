import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

public class Library {
    private final Map<Integer, Book> books = new HashMap<>();        // id -> Book
    private final Map<Integer, Member> members = new HashMap<>();    // id -> Member
    private final List<BorrowRecord> borrowRecords = new ArrayList<>();
    private int nextBookId = 1;
    private int nextMemberId = 1001;
    private static final double FINE_PER_DAY = 10.0; // rupees per day

    // ----- Book management -----
    

    public int addNewTextBook(String title, String author, double price, String isbn, int pages, String subject) {
        int id = nextBookId++;
        Book b = new TextBook(id, title, author, price, isbn, pages, subject);
        books.put(id, b);
        return id;
    }

    public Optional<Book> getBookById(int id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> listAllBooksSortedByTitle() {
        return books.values().stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Book> searchByTitle(String query) {
        String q = query.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ----- Member management -----
    public int addMember(String name) {
        int id = nextMemberId++;
        members.put(id, new Member(id, name));
        return id;
    }

    public Optional<Member> getMemberById(int id) {
        return Optional.ofNullable(members.get(id));
    }

    // ----- Borrow/Return -----
    // borrowDays defines how many days until due (e.g., 14)
    public void borrowBook(int memberId, int bookId, int borrowDays) throws BookNotAvailableException {
        Member m = members.get(memberId);
        Book b = books.get(bookId);
        if (m == null) throw new IllegalArgumentException("No member: " + memberId);
        if (b == null) throw new IllegalArgumentException("No book: " + bookId);

        if (b instanceof Borrowable) {
            Borrowable br = (Borrowable) b;
            br.borrow(); // may throw BookNotAvailableException
            LocalDate borrowDate = LocalDate.now();
            LocalDate due = borrowDate.plusDays(borrowDays);
            borrowRecords.add(new BorrowRecord(memberId, bookId, borrowDate, due));
        } else {
            throw new BookNotAvailableException("This book cannot be borrowed (digital/reference): " + b.getTitle());
        }
    }

    public double returnBook(int memberId, int bookId) {
        Member m = members.get(memberId);
        Book b = books.get(bookId);
        if (m == null) throw new IllegalArgumentException("No member: " + memberId);
        if (b == null) throw new IllegalArgumentException("No book: " + bookId);

        // remove borrow record (assume only one active borrow per book)
        BorrowRecord found = null;
        for (BorrowRecord r : borrowRecords) {
            if (r.getBookId() == bookId && r.getMemberId() == memberId) {
                found = r;
                break;
            }
        }
        if (found == null) {
            // nothing to return (could be eBook)
            if (b instanceof Borrowable) {
                // just allow return
                ((Borrowable) b).returnItem();
            }
            return 0.0;
        }

        borrowRecords.remove(found);
        if (b instanceof Borrowable) ((Borrowable) b).returnItem();

        // fine calculation
        LocalDate today = LocalDate.now();
        if (today.isAfter(found.getDueDate())) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(found.getDueDate(), today);
            return daysLate * FINE_PER_DAY;
        }
        return 0.0;
    }

    public List<BorrowRecord> getBorrowRecordsForMember(int memberId) {
        return borrowRecords.stream()
                .filter(r -> r.getMemberId() == memberId)
                .collect(Collectors.toList());
    }

    // ----- Simple Persistence (CSV) -----
    public void saveBooksToFile(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Book b : books.values()) {
                String type = b.getClass().getSimpleName();
                // minimal CSV: id,type,title,author,price,extra1,extra2
                if (b instanceof Novel) {
                    Novel n = (Novel) b;
                    pw.printf("%d,%s,%s,%s,%.2f,%s,%d,%s%n",
                            n.getId(), "Novel", escape(n.getTitle()), escape(n.getAuthor()), n.getPrice(),
                            n.getIsbn(), n.getPages(), escape(n.getInfo())); // info saved for debug
                } else if (b instanceof TextBook) {
                    TextBook t = (TextBook) b;
                    pw.printf("%d,%s,%s,%s,%.2f,%s,%d,%s%n",
                            t.getId(), "TextBook", escape(t.getTitle()), escape(t.getAuthor()), t.getPrice(),
                            t.getIsbn(), t.getPages(), escape(t.getInfo()));
                } else if (b instanceof PrintedBook) {
                    PrintedBook p = (PrintedBook) b;
                    pw.printf("%d,%s,%s,%s,%.2f,%s,%d,%s%n",
                            p.getId(), p.getClass().getSimpleName(), escape(p.getTitle()), escape(p.getAuthor()),
                            p.getPrice(), p.getIsbn(), p.getPages(), escape(p.getInfo()));
                }
            }
        }
    }

    public void loadBooksFromFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            books.clear();
            int maxId = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 8);
                if (parts.length < 8) continue;
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String title = unescape(parts[2]);
                String author = unescape(parts[3]);
                double price = Double.parseDouble(parts[4]);
                String isbn = parts[5];
                int pages = Integer.parseInt(parts[6]);
                // ignore last field (info)
                if ("Novel".equals(type)) {
                    // for simplicity set genre unknown
                    books.put(id, new Novel(id, title, author, price, isbn, pages, "Jasoosi"));
                } else if ("TextBook".equals(type)) {
                    books.put(id, new TextBook(id, title, author, price, isbn, pages, "Unknown"));
                } else {
                    books.put(id, new PrintedBook(id, title, author, price, isbn, pages));
                }
                if (id > maxId) maxId = id;
            }
            nextBookId = maxId + 1;
        }
    }

    private String escape(String s) {
        return s.replace(",", " "); // simple escape
    }

    private String unescape(String s) {
        return s;
    }

    // save/load members (simple)
    public void saveMembersToFile(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Member m : members.values()) {
                pw.printf("%d,%s,%s%n", m.getMemberId(), escape(m.getName()), m.getJoinedOn());
            }
        }
    }

    public void loadMembersFromFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            members.clear();
            int maxId = 1000;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                if (p.length < 3) continue;
                int id = Integer.parseInt(p[0]);
                String name = unescape(p[1]);
                members.put(id, new Member(id, name));
                if (id > maxId) maxId = id;
            }
            nextMemberId = maxId + 1;
        }
    }

    // ----- Utility for quick demo -----
    public void seedSampleData() {
        addNewNovel("Harry Potter", "J.K. Rowling", 1500, "ISBN-HP-001", 320, "Fantasy");
        addNewTextBook("Java Basics", "John Doe", 1200, "ISBN-JB-002", 400, "Programming");
        addMember("Alice");
        addMember("Bob");
    }

    public int addNewNovel(String title, String author, int price, String isbn, int pages, String genre)
    {
        int novelId = nextBookId++;
        Novel novel = new Novel(novelId, title, author, price, isbn, pages, genre);
        // save new novel into in-memory
        books.put(novel.getId(), novel);

        return novel.getId();
    }
}
