package part1;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    public static String getPdfText(File file){
        String text = "";
        try {
            PDDocument document = PDDocument.load(file);
            AccessPermission ap = document.getCurrentAccessPermission();

            if(!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);

            document.close();

        } catch(IOException ignored) {}
        return text;
    }

    /**
     * @param folderPath contains the path of the folder
     * @param extension contains the extension of the desired files
     * @return path list of files with a choosen extension in the selected folder
     */
    public static ArrayList<String> getFilesInFolder(String folderPath, String extension){

        File folder = new File(folderPath);
        File file;
        ArrayList<String> pdfList = new ArrayList<>();
        String[] fileNameList = folder.list();

        if(fileNameList != null) {

            for(String fileName: fileNameList) {
                file = new File(folder, fileName);
                if(file.isFile() && file.getName().toLowerCase().endsWith("." + extension)) {
                    pdfList.add(file.getPath());
                }
            }

        }

        return pdfList;

    }

    public static List<String> getStopWords(String file) {
        try {
            return Arrays.asList(new String(Files.readAllBytes(Paths.get(file))).split("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
