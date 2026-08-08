import attributes.Function;

import java.time.LocalDate;

public class Test {
    @Function
    public String dateFromTodayWithFormat(String days,String format) {
        return LocalDate.now().plusDays(Integer.parseInt(days)).format(java.time.format.DateTimeFormatter.ofPattern(format));
    }
}
