package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad;
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior;
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget;
import org.junit.Test;

import java.net.URI;
import java.time.Duration;

public class MyBtfBenchmarkIT {

    @Test
    public void benchmarkMyJira() {
        new BtfBenchmark(
            new VirtualUserTarget(
                URI.create("http://3.121.217.29:8080/"), // point to your Jira
                "admin",
                "6c2860f2-0f3e-44b8-b716-0e183e0e1f5e"
            ),
            new VirtualUserBehavior.Builder(MyScenario.class)
                .browser(MyBrowser.class)
                .load(
                    new VirtualUserLoad.Builder()
                        .virtualUsers(1) // increase to apply heavier load
                        .flat(Duration.ofMinutes(5)) // increase to obtain more results
                        .build() // explore more load customizations before `build`
                )
                .build() // explore more behavior customizations before `build`
        ).run();
    }
}
