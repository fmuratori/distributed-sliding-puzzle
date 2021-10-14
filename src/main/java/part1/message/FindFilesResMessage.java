package part1.message;

import java.io.File;
import java.util.List;

public class FindFilesResMessage implements Message {

    public List<File> files;

    public FindFilesResMessage(List<File> files) {
        this.files = files;
    }

}
