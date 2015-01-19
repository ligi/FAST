package org.ligi.fast.testing;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.fast.util.StringUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class TheStringUtils extends AppInfoTestBase {

    @SmallTest
    public void testShouldMatchWithGapSearch() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fb", 4)).isEqualTo(4);
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fba", 3)).isEqualTo(3);
    }

    @SmallTest
    public void testShouldReturnMinusOne() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "bf", 4)).isEqualTo(-1);
    }

    @SmallTest
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
