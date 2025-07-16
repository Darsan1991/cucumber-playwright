package utils;

public class StringUtils {

    private StringUtils() {
    }

    @SuppressWarnings("null")
    public static String toUpperSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("([a-z])(\\d+)", "$1_$2")
                .replaceAll("([A-Z])(\\d+)", "$1_$2")
                .replaceAll("-","_")
                .replaceAll("\\s+", "_")
                .replaceAll("_+", "_")
                .replaceAll("[^a-zA-Z0-9_]", "")
                .toUpperCase();
    }
    
    public static String compactCompare(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();
    }


    public static String removeQuotes(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("^\"|\"$", "");
    }

    
    
    public static String getTextByRegex(String input, String pattern) {
        if (input == null || input.isEmpty() || pattern == null || pattern.isEmpty()) {
            return null;
        }
        
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(input);
        return matcher.find() ? matcher.group() : null;
    }

    public static java.util.List<String> getAllTextByRegex(String input, String pattern) {
        if (input == null || input.isEmpty() || pattern == null || pattern.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<String> matches = new java.util.ArrayList<>();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }


    public static void main(String[] args) {
        System.out.println(toUpperSnakeCase("Hello--_World123"));
        System.out.println(compactCompare("Hello_World- \n  1"));
    }
}
