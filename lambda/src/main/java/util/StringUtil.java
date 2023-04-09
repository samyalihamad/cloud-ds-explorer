package util;

public class StringUtil {

    public static String getCommonStartingCharacters(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < minLength; i++) {
            if(s1.charAt(i) != s2.charAt(i))
                break;

            builder.append(s1.charAt(i));
        }

        return builder.toString();
    }
}
