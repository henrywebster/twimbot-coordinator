package info.hwebs.twimbot.coordinator;

public class ExecutionHistory {

    // TODO change to Enum?
    private final String status;

    private final String description;

    public ExecutionHistory(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
