package org.ligi.fast.testing;

import org.junit.Test;
import org.ligi.fast.util.UmlautConverter;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class TheUmlautConverter {

    @Test
    public void should_convert_all_the_umlauts() {
        assertThat(UmlautConverter.replaceAllUmlauts("üüä")).isEqualTo("ueueae");
    }

    @Test
    public void should_return_null_when_no_umlaut() {
        assertNull(UmlautConverter.replaceAllUmlautsReturnNullIfEqual("abcfoo"));
    }

    @Test
    public void should_contain_17_chars() {
        // need a test for that as I really have problems to differentiate these chars visually ;-)
        // as it is a hashmap I will see that some are double in there ..
        assertThat(UmlautConverter.REPLACEMENT_MAP.size()).isEqualTo(17);
    }
}
