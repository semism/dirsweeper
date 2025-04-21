public class BroomCommand {
    private CommandType type;
    private String source;
    private String destination;
    private boolean isRegex;

    public BroomCommand(CommandType type, String source, String destination, boolean isRegex) {
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.isRegex = isRegex;
    }

    public CommandType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isRegex() {
        return isRegex;
    }

    @Override
    public String toString() {
        return (isRegex ? "[REGEX] " : "") + type + " " + source +
                (destination != null ? " → " + destination : "");
    }
}
