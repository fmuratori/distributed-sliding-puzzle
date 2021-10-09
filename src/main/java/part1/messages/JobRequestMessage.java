package part1.messages;

public class JobRequestMessage implements JobMessage {

    private final String filePath;

    public JobRequestMessage(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

}
