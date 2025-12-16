import java.time.LocalDate;

public class BorrowRecord {
    private final int memberId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    public BorrowRecord(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public int getMemberId() { return memberId; }
    public int getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    @Override
    public String toString() {
        return String.format("BorrowRecord: member:%d book:%d borrowed:%s due:%s",
                memberId, bookId, borrowDate, dueDate);
    }
}

