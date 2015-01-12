package org.ligi.fast.testing;

import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.spoon.Spoon;

import org.ligi.fast.App;

public class TheSearchActivity extends AppInfoTestBase {

    MutableFastSettings newSettings = new MutableFastSettings();

    @Override
    public void setUp() {
        App.injectSettingsForTesting(newSettings);
    }

    @SmallTest
    public void testShouldWorkWithLightTheme() {
        newSettings.theme = "light";
        Spoon.screenshot(getActivity(), "theme_" + newSettings.theme);
    }



    @SmallTest
    public void testShouldWorkWithDarkTheme() {
        newSettings.theme = "dark";
        Spoon.screenshot(getActivity(), "theme_" + newSettings.theme);
    }


    @SmallTest
    public void testShouldWorkWithTransparentLightTheme() {
        newSettings.theme = "transparent_light";
        Spoon.screenshot(getActivity(), "theme_" + newSettings.theme);
    }


    @SmallTest
    public void testShouldWorkWithTransparentTheme() {
        newSettings.theme = "transparent";
        Spoon.screenshot(getActivity(), "theme" + newSettings.theme);
    }
}
