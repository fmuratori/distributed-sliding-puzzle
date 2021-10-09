package part1;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileReader {

    /**
     *
     * @param path contains the path of the file
     * @return list of words contained in the choosen file
     */
    public ArrayList<String> getWordsFromPdf(String path){

        //le parole estratte dai pdf vengono messe in this.wordList
        ArrayList<String> wordList = new ArrayList<String>();

        try {

            //legge il file dal file system, lo spezzetta in stringhe
            //e manda la lista ottenuta al monitor per la conta
            PDDocument document = PDDocument.load(new File(path));
            AccessPermission ap = document.getCurrentAccessPermission();

            if(!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            // This example uses sorting, but in some cases it is more useful to switch it off,
            // e.g. in some files with columns where the PDF content stream respects the
            // column order.
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);
            wordList.addAll(Arrays.asList(text.trim().replaceAll("[^a-zA-Zςΰωθιμ ]", " ").split("\\s+")));

            document.close();

        } catch(IOException ex) {}

        return wordList;

    }

    /**
     * @param folderPath contains the path of the folder
     * @param extension contains the extension of the desired files
     * @return path list of files with a choosen extension in the selected folder
     */
    public ArrayList<String> getFilesInFolder(String folderPath, String extension){

        File folder = new File(folderPath);
        File file;
        ArrayList<String> pdfList = new ArrayList<String>();
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

}
