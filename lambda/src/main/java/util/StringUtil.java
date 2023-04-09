package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // TODO: Refactor direction to enum
    public static Map<String, String> getNeighborQuad(String quad) {
        int orgLength = quad.length();
        // Convert back to a String and preserve leading zeros
        String formatSpecifier = String.format("%%0%dd", orgLength);

        int num = Integer.parseInt(quad);


        Map<String, String> neighbors = new HashMap<>();

        // Find West & East Neighbors
        if(quad.charAt(orgLength - 1) == '3' || quad.charAt(orgLength - 1) == '1') {
            int westNeighbor = num + 9;
            neighbors.put("west", String.format(formatSpecifier, westNeighbor));

            int eastNeighbor = num - 1;
            neighbors.put("east", String.format(formatSpecifier, eastNeighbor));
        }
        else {
            int westNeighbor = num - 9;
            neighbors.put("west", String.format(formatSpecifier, westNeighbor));

            int eastNeighbor = num + 1;
            neighbors.put("east", String.format(formatSpecifier, eastNeighbor));
        }

        // Find North & South Neighbors
        if(quad.charAt(orgLength - 1) == '2' || quad.charAt(orgLength -1 ) == '3'){
            int northNeighbor = num - 2;
            neighbors.put("north", String.format(formatSpecifier, northNeighbor));

            int southNeighbor = num - 18;
            neighbors.put("south", String.format(formatSpecifier, southNeighbor));
        }
        else {
            int northNeighbor = num - 18;
            neighbors.put("north", String.format(formatSpecifier, northNeighbor));

            int southNeighbor = num + 2;
            neighbors.put("south", String.format(formatSpecifier, southNeighbor));
        }

        return neighbors;
    }
}
