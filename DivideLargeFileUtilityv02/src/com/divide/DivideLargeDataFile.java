package com.divide;

import java.io.*;
import java.util.Properties;



        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.PrintStream;
        import java.util.Properties;

        import org.apache.commons.lang3.SerializationUtils;
        import org.apache.poi.util.SystemOutLogger;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;
        import com.jayway.jsonpath.JsonPath;

public class DivideLargeDataFile {

    static String outputFilespath = null;
    static String logfilePath = null;
    static String logfileName = null;
    static String outputLogFile = null;
    static String JSONLargeFileToSplit = null;
    static Properties prop = null;
    static String EscapeDoubleQuoteChar = "\"";
    static String EscapeSingleQuoteChar = "\'";

    public static void main(String[] args) {

        try {
            // JSONLargeFileToSplit : C:/SD/EclipseQAUtilities/WS_Java_Utilities/DivideAndCompareUtilityv02/resources/LargeFiles/sd_All_d26sep18_r10dec18_r1-standard.json
            //outputFilespath : C:/SD/EclipseQAUtilities/WS_Java_Utilities/DivideAndCompareUtilityv02/resources/LargeFileDividedOutput
            //logfilePath : C:/SD/EclipseQAUtilities/WS_Java_Utilities/DivideAndCompareUtilityv02/resources/DivideLogs
            // logfileName : dividelog
            JSONLargeFileToSplit = args[0];
            outputFilespath = args[1]+"/";
            logfilePath = args[2]+"/";
            logfileName = args[3];



            File outputFilespathdirpath = new File(outputFilespath);
            if(!outputFilespathdirpath.exists()) {
                outputFilespathdirpath.mkdirs();
            }
            File logfilePathdirpath = new File(logfilePath);
            if(!logfilePathdirpath.exists()) {
                logfilePathdirpath.mkdirs();
            }

            outputLogFile = logfilePath+logfileName;
            FileOutputStream fos = new FileOutputStream(outputLogFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);

            System.out.println("1 Json output file to split -->" + JSONLargeFileToSplit);
            System.out.println("2 Path where the  split files will be placed -->" + outputFilespath);
            System.out.println("3  Logfile name with the path  -->" + logfilePath);

            FileReader fileReader = new FileReader(JSONLargeFileToSplit);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    writetojsonfile(line);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    static int totalfiles = 0;

    private static void writetojsonfile(String line1) throws IOException {

        String documentclone = SerializationUtils.clone(line1);

        totalfiles++;
        if (documentclone.indexOf("{") == 1) {
            documentclone = documentclone.substring(1, documentclone.length() - 1);
           // System.out.println(documentclone);
        } else if (documentclone.indexOf("{") == 0
                && documentclone.substring(documentclone.length() - 1, documentclone.length()) == "]") {
            documentclone = documentclone.substring(0, documentclone.length() - 1);
            //System.out.println(documentclone);
        } else {
            documentclone = documentclone.substring(0, documentclone.length() - 1);
            //System.out.println(documentclone);
        }


        String entityOfFocusId = JsonPath.parse(documentclone).read("$.row.return.systemOutput.entityOfFocusId")
                .toString();


        //System.out.println(entityOfFocusId);
        FileWriter writer = new FileWriter(outputFilespath + totalfiles + "_" + entityOfFocusId + ".json");
        writer.write(documentclone);
        writer.close();

    }

    public static String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    private static String refinePropertyFileString(String propertyString) {

        if ((propertyString.startsWith(EscapeDoubleQuoteChar) && propertyString.endsWith(EscapeDoubleQuoteChar))
                || (propertyString.startsWith(EscapeSingleQuoteChar)
                && propertyString.endsWith(EscapeSingleQuoteChar))) {
            propertyString = propertyString.substring(1, propertyString.length() - 1);
        }
        return propertyString;

    }
}
