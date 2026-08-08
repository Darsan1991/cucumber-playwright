package reflection;

import reflection.base.LoggerService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//@Service(type = LoggerService.class)
public class FileLoggerService implements LoggerService {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    static {
        File file = new File("src/test/resources/logs/logs.md");
        file.mkdirs();
        
        try {
            if (file.exists()) {
                        file.delete();
            }
                file.createNewFile();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logSuccess(String message) {
//        logSuccessInternal("=====================================================================================");
        logInternal(message,"green");
        logInternal("----------------------------------------------------------------------------------------","green");
    }
    
    public void logWarning(String message) {
        logInternal("=====================================================================================","yellow");
        logInternal(message,"yellow");
        logInternal("=====================================================================================","yellow");
    }

    private static void logInternal(String message,String color) {
      appendString("<p style=\"color:%s\">".formatted(color)+message+"</p>"); 
    }

    public void logError(String message) {
        logInternal("=====================================================================================","red");
        logInternal(message,"red");
        logInternal("=====================================================================================","red");
    }

    public static void appendString(String str) {
        try (FileWriter writer = new FileWriter("src/test/resources/logs/logs.md", true)) {
            writer.write(str+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
}
