import java.time.LocalDate;

public class Member {
    private final int memberId;
    private String name;
    private final LocalDate joinedOn;

    public Member(int memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.joinedOn = LocalDate.now();
    }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getJoinedOn() { return joinedOn; }

    @Override
    public String toString() {
        return String.format("Member[%d] %s (joined %s)", memberId, name, joinedOn);
    }
}

