package reflection;

import attributes.Service;
import reflection.base.LoggerService;

@Service(type = LoggerService.class)
public class SystemLoggerService implements LoggerService {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public void logSuccess(String message) {
//        logSuccessInternal("=====================================================================================");
        logInternal(ANSI_GREEN,message);
        logInternal(ANSI_GREEN,"----------------------------------------------------------------------------------------");
    }

    private static void logInternal(String color, String message) {
        System.out.println(color+ message + ANSI_RESET);
    }

    public void logError(String message) {
        System.err.println("            ");
        System.err.println("            ");
        System.err.println("=================================ERROR=================================================");
        System.err.println("            ");
        
        System.err.println(message);
        System.err.println("            ");
        
        System.err.println("=====================================================================================");
        System.err.println("            ");
        System.err.println("            ");
        
    }
    
    public void logWarning(String message) {
        System.err.println("            ");
        System.err.println("            ");
        logInternal(ANSI_YELLOW,"=================================WARNING=================================================");
        logInternal(ANSI_YELLOW,message);
        logInternal(ANSI_YELLOW,"=====================================================================================");
        System.err.println("            ");
        System.err.println("            ");
        
    }
}
