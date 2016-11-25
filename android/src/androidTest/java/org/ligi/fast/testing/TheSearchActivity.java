package org.ligi.fast.testing;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.fast.App;
import org.ligi.fast.ui.SearchActivity;
import org.ligi.trulesk.TruleskActivityRule;

public class TheSearchActivity {

    @Rule
    public TruleskActivityRule<SearchActivity> rule = new TruleskActivityRule<>(SearchActivity.class, false);

    AndroidTestMutableFastSettings newSettings = new AndroidTestMutableFastSettings();

    @Before
    public void setUp() {
        App.injectSettingsForTesting(newSettings);
    }

    @Test
    public void testShouldWorkWithLightTheme() {
        newSettings.theme = "light";
        rule.launchActivity(null);

        rule.screenShot("theme_" + newSettings.theme);
    }

    @Test
    public void testShouldWorkWithDarkTheme() {
        newSettings.theme = "dark";
        rule.launchActivity(null);

        rule.screenShot("theme_" + newSettings.theme);
    }


    @Test
    public void testShouldWorkWithTransparentLightTheme() {
        newSettings.theme = "transparent_light";
        rule.launchActivity(null);

        rule.screenShot("theme_" + newSettings.theme);
    }


    @Test
    public void testShouldWorkWithTransparentTheme() {
        newSettings.theme = "transparent";
        rule.launchActivity(null);

        rule.screenShot("theme_" + newSettings.theme);
    }
}
