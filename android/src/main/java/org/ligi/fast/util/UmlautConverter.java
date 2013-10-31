package org.ligi.fast.util;

import java.util.HashMap;
import java.util.Map;

public class UmlautConverter {

    public final static Map<String, String> REPLACEMENT_MAP = new HashMap<String, String>() {{

        // german
        put("ü", "ue");
        put("ö", "oe");
        put("ä", "ae");
        put("ß", "ss");

        // greek
        put("ά", "α");
        put("έ", "ε");
        put("ή", "η");
        put("ί", "ι");
        put("ό", "ο");
        put("ύ", "υ");
        put("ώ", "w");
        /*
        we do not need UpperCase as for searching

        put("Α","Α");
        put("Ε", "Έ");
        put("Η", "Ή");
        put("Ι", "Ί");
        put("Ο", "Ό");
        put("Υ", "Ύ");
        put("Ω", "Ώ");groovy:000> "Ώ".toLowerCase(); ===> ώ
        */

        // spanish - credits Rubén Gómez ruben.gomez@canselleiro.org
        put("á", "a");
        put("é", "e");
        put("í", "i");
        put("ñ", "n");
        put("ó", "o");
        put("ú", "u");

    }};


    public static String replaceAllUmlauts(String input) {
        String output = input;

        for (Map.Entry<String, String> entry : REPLACEMENT_MAP.entrySet()) {
            /*
             * fun fact when there are 2 methods replace(..) and replaceAll(..)
             * and you want to replace all occurrences you would take the later
             * which is not totally wrong ( is doing the right thing )
             * but replace(..) is also doing the right thing but way faster ;-)
             */
            output = output.replace(entry.getKey(), entry.getValue());
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
