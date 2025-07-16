package reflection.base;


public interface LoggerService {
    void logSuccess(String message) ;
    void logError(String message);

    void logWarning(String message);
}
