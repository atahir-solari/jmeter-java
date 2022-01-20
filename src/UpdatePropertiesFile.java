import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

public class UpdatePropertiesFile {

    public static String propertiesFilePath = "";

    public static void updatePropertiesFile(String format) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(propertiesFilePath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                if (line.contains("jmeter.save.saveservice.output_format")) {
                    if (format == "xml" || format == "csv")
                        line = "jmeter.save.saveservice.output_format=" + format;
                    else
                        line = "#" + line;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(propertiesFilePath);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
