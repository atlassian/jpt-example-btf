package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.virtualusers.api.browsers.HeadlessChromeBrowser;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class MyBrowser extends HeadlessChromeBrowser {
    @Override
    protected void configure(
            @NotNull ChromeOptions options,
            @NotNull ChromeDriverService.Builder service
    ) {
        /*
         * There are many useful Chrome flags to switch: https://peter.sh/experiments/chromium-command-line-switches
         * In this case we're ignoring certificate-related errors, in order to allow insecure connections,
         * which are a common sight in some corporate setups.
         */
        options.addArguments("--ignore-certificate-errors");
        super.configure(options, service);
    }
}
