package part1;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    /**
     * il path può essere assoluto o relativo(es. "assets/pdf" va bene)
     * @param folderPath contains the path of the folder
     * @param extension contains the extension of the desired files
     * @return path list of files with a choosen extension in the selected folder
     */
    public static List<String> getFilesInFolder(String folderPath, String extension){

        File folder = new File(folderPath);
        File file;
        List<String> pdfList = new ArrayList<>();
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

    /**
     * @param path path del file pdf da cui estrarre le parole
     * @return restituisce la lista delle parole estratte dal file pdf
     */
    public static List<String> getWordsFromPdf(String path){

        //le parole estratte dai pdf vengono messe in this.wordList
        List<String> wordList = new ArrayList<>();

        try {

            //legge il file dal file system, lo spezzetta in stringhe
            //e manda la lista ottenuta al monitor per la conta
            PDDocument document = PDDocument.load(new File(path));
            AccessPermission ap = document.getCurrentAccessPermission();

            if(!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);
            wordList.addAll(Arrays.asList(text.trim().replaceAll("[^a-zA-Zςΰωθιμ ]", " ").split("\\s+")));

            document.close();

        } catch(IOException ignored) {}

        return wordList;

    }

}
