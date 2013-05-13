/**
 * Simple class that defines a sender and recevier.
 */
public class Command {
    public final Host sender, receiver;
    public final int length;

    public Command(Host sender, Host receiver, int length) {
        this.sender = sender;
        this.receiver = receiver;
        this.length = length;
    }
}
