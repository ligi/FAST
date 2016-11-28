package org.ligi.fast.testing

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.ligi.fast.App
import org.ligi.fast.ui.SearchActivity
import org.ligi.trulesk.TruleskActivityRule

class TheSearchActivity {

    @get:Rule
    var rule = TruleskActivityRule(SearchActivity::class.java, false)

    internal var newSettings = AndroidTestMutableFastSettings()

    @Before
    fun setUp() {
        App.injectSettingsForTesting(newSettings)
    }

    @Test
    fun testShouldWorkWithLightTheme() {
        newSettings.theme = "light"
        rule.launchActivity(null)

        rule.screenShot("theme_" + newSettings.theme)
    }

    @Test
    fun testShouldWorkWithDarkTheme() {
        newSettings.theme = "dark"
        rule.launchActivity(null)

        rule.screenShot("theme_" + newSettings.theme)
    }


    @Test
    fun testShouldWorkWithTransparentLightTheme() {
        newSettings.theme = "transparent_light"
        rule.launchActivity(null)

        rule.screenShot("theme_" + newSettings.theme)
    }


    @Test
    fun testShouldWorkWithTransparentTheme() {
        newSettings.theme = "transparent"
        rule.launchActivity(null)

        rule.screenShot("theme_" + newSettings.theme)
    }
}
