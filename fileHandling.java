import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.regex.Pattern;

class fileHandling {
    private String fileName;
    private File file;
    //Logger Initialization.
    private final Logger log = Logger.getLogger("fileHandling");

    /**
     * This construction will receive a {File} type object, serves as an entry point for further analysis.
     * @param file {File} -> file used for this function.
     */
    fileHandling(File file){
        fileName = file.getName().toLowerCase();
        this.file = file;
    }

    /**
     * This method will analyze the file name, if the file's extension is legal, then start analyzing \
     * by its extension. If the extension is not legal, throws IllegalFileName Exception. \
     * If the extension is ".csv", then it will go through csvHandler() to get grades \
     * If the extension is ".txt", then it will go through txtHandler() to get grades.
     * @return {ArrayList<Double>} -> return data from the chosen file.
     * @throws IllegalFileName -> throws when the file name's extension is neither .txt nor .csv
     * @throws IOException -> throws when I/O function goes wrong.
     */
    ArrayList<Double> getNumberList() throws IllegalFileName, IOException {
        //Regex matches all legal file names such as "grade.txt", and "grade.csv"
        if (!Pattern.matches(".*?\\.(csv|txt)$", fileName)) {
            log.log(Level.WARNING, "File \"" + fileName + "\"'s extension is illegal");
            throw new IllegalFileName("Imported file name is illegal.");
        }

        //if the file has an extension of csv, use method csvHandler()
        if (Pattern.matches(".*?\\.csv$", fileName)) {
            return csvHandler();
        }

        //if the file has an extension of txt, use method txtHandler()
        if (Pattern.matches(".*?\\.txt$", fileName)) {
            return txtHandler();
        }

        return new ArrayList<>();
    }

    /**
     * This method is a helper method for getting data from csv files.
     * @return {ArrayList<Double>} -> return the data got from a csv file.
     * @throws IOException -> throws when buffer reader counters error.
     */
    private ArrayList<Double> csvHandler() throws IOException {
        ArrayList<Double> grade = new ArrayList<>();
        BufferedReader csv;
        try {
            csv = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException err){
            log.log(Level.WARNING, "File \"" + fileName + "\" was not found.");
            throw new FileNotFoundException();
        }

        String line;
        while((line = csv.readLine()) != null){

            String[] result = line.split(",");
            for (String element : result){
                try {
                    double value = Double.parseDouble(element);
                    grade.add(value);
                    log.log(Level.INFO, "Data: " + value + " added to the grade list.");

                } catch (NumberFormatException ignored){

                }
            }
        }

        if (grade.size() == 0){
            log.log(Level.WARNING, "No data found in file");
        }

        csv.close();
        return grade;
    }

    /**
     * This method is a helper method for getting data from txt files.
     * @return {ArrayList<Double>} -> return the data got from a txt file.
     * @throws IOException -> throws when buffer reader counters error.
     */
    private ArrayList<Double> txtHandler() throws IOException{
        ArrayList<Double> grade = new ArrayList<>();
        BufferedReader txt;
        try{
            txt = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException err){
            log.log(Level.WARNING, "File \"" + fileName + "\" Not Found.");
            throw new FileNotFoundException("File \"" + fileName + "\" Not Found.");
        }

        String value;
        while ((value = txt.readLine()) != null){
            try{
                grade.add(Double.parseDouble(value));
                log.log(Level.INFO, "Data: " + value + " added to the grade list.");

            } catch (NumberFormatException err){
                log.log(Level.WARNING, "Non-numerical value detected. Aborting...");
                throw new NumberFormatException("Found non-numerical value. Loading aborted.");
            }
        }

        if (grade.size() == 0){
            log.log(Level.WARNING, "No data found in file");
        }

        txt.close();
        return grade;
    }

}

/**
 * This class is made for a customized exception.
 */
class IllegalFileName extends Exception{
    /**
     * Customized exception called "IllegalFileName"
     * @param errorMessage {String} -> Message displayed in trace back.
     */
    IllegalFileName(String errorMessage){
        super(errorMessage);
    }
}
