package org.ligi.fast.testing;

import org.junit.Test;
import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class TheStringUtils {

    @Test
    public void testShouldMatchWithGapSearch() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fb", 4)).isEqualTo(4);
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fba", 3)).isEqualTo(3);
    }

    @Test
    public void testShouldReturnMinusOne() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "bf", 4)).isEqualTo(-1);
    }

    @Test
    public void testShouldReturnMatchedIndices() {
        ArrayList<Integer> indices = StringUtils.getMatchedIndices("foobar", "fba");
        assertThat(indices.get(0)).isEqualTo(0);
        assertThat(indices.get(1)).isEqualTo(3);
        assertThat(indices.get(2)).isEqualTo(4);

        indices = StringUtils.getMatchedIndices("foobar", "foo");
        assertThat(indices.get(0)).isEqualTo(0);
        assertThat(indices.get(1)).isEqualTo(1);
        assertThat(indices.get(2)).isEqualTo(2);
    }

}
