package org.ligi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.util.UmlautConverter;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

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



}