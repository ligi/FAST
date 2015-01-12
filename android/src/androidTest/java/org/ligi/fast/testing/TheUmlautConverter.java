package org.ligi.fast.testing;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.util.UmlautConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class TheUmlautConverter extends AppInfoTestBase {

    @SmallTest
    public void should_convert_all_the_umlauts() {
        assertThat(UmlautConverter.replaceAllUmlauts("üüä")).isEqualTo("ueueae");
    }

    @SmallTest
    public void should_return_null_when_no_umlaut() {
        assertThat(UmlautConverter.replaceAllUmlautsReturnNullIfEqual("abcfoo")).isEqualTo(null);
    }

    @SmallTest
    public void should_contain_17_chars() {
        // need a test for that as I really have problems to differentiate these chars visually ;-)
        // as it is a hashmap I will see that some are double in there ..
        assertThat(UmlautConverter.REPLACEMENT_MAP.size()).isEqualTo(17);
        assertTrue(false);
    }


}
