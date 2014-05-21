package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.util.UmlautConverter;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@Config(emulateSdk = 18) // robolectric cannot deal with 19 and i do not want to targetSDK--
@RunWith(RobolectricTestRunner.class)
public class TheUmlautConverter extends AppInfoTestBase {

    @Test
    public void should_convert_all_the_umlauts() {
        assertThat(UmlautConverter.replaceAllUmlauts("üüä")).isEqualTo("ueueae");
    }

    @Test
    public void should_return_null_when_no_umlaut() {
        assertThat(UmlautConverter.replaceAllUmlautsReturnNullIfEqual("abcfoo")).isEqualTo(null);
    }
    @Test
    public void should_contain_17_chars() {
        // need a test for that as I really have problems to differentiate these chars visually ;-)
        // as it is a hashmap I will see that some are double in there ..
        assertThat(UmlautConverter.REPLACEMENT_MAP.size()).isEqualTo(17);
    }



}
