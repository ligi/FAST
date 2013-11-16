package org.ligi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ligi.fast.util.StringUtils;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class TheStringUtils extends AppInfoTestBase {

    @Test
    public void should_match_with_fuzzy_search() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fb", 4)).isEqualTo(4);
        assertThat(StringUtils.getLevenshteinDistance("foobar", "fba", 3)).isEqualTo(3);
    }

    @Test
    public void should_return_minus_one() {
        assertThat(StringUtils.getLevenshteinDistance("foobar", "bf", 4)).isEqualTo(-1);
    }

    @Test
    public void should_retrun_matched_indices() {
        ArrayList<Integer> indices = StringUtils.getMatchedIndices("foobar", "fba");
        assertThat(indices.get(0)).isEqualTo(0);
        assertThat(indices.get(1)).isEqualTo(3);
        assertThat(indices.get(2)).isEqualTo(4);

        indices = StringUtils.getMatchedIndices("foobar", "foo");
        assertThat(indices.get(0)).isEqualTo(0);
        assertThat(indices.get(1)).isEqualTo(1);
        assertThat(indices.get(2)).isEqualTo(2);
    }

    @Test
    public void should_split_with_indices() {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        indices.add(0);
        indices.add(3);
        indices.add(4);

        ArrayList<String> tokens = StringUtils.splitWithIndices("foobar", indices);

        assertThat(tokens.get(0)).isEqualTo("");
        assertThat(tokens.get(1)).isEqualTo("f"); // at index
        assertThat(tokens.get(2)).isEqualTo("oo");
        assertThat(tokens.get(3)).isEqualTo("b"); // at index
        assertThat(tokens.get(4)).isEqualTo("");
        assertThat(tokens.get(5)).isEqualTo("a"); // at index
        assertThat(tokens.get(6)).isEqualTo("r");
    }
}
