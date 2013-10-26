package org.ligi.fast.util;

import java.util.HashMap;
import java.util.Map;

public class UmlautConverter {

    private final static Map<String, String> replacementMap = new HashMap<String, String>() {{

        // german
        put("ue", "ü");
        put("oe", "ö");
        put("ae", "ä");
        put("ss", "ß");

        // greek
        put("α", "ά");
        put("ε", "έ");
        put("η", "ή");
        put("ι", "ί");
        put("ο", "ό");
        put("υ", "ύ");
        put("Α", "Α");
        put("Ε", "Έ");
        put("Η", "Ή");
        put("Ι", "Ί");
        put("Ο", "Ό");
        put("Υ", "Ύ");
        put("Ω", "Ώ");
    }};


    public static String replaceAllUmlauts(String input) {
        String output = input;

        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            /*
             * fun fact when there are 2 methods replace(..) and replaceAll(..)
             * and you want to replace all occurrences you would take the later
             * which is not totally wrong ( is doing the right thing )
             * but replace(..) is also doing the right thing but way faster ;-)
             */
            output = output.replace(entry.getValue(), entry.getKey());
        }

        return output;
    }

    public static String replaceAllUmlautsReturnNullIfEqual(String input) {
        String output = replaceAllUmlauts(input);

        if (output.equals(input)) {
            return null;
        }

        return output;
    }

}
