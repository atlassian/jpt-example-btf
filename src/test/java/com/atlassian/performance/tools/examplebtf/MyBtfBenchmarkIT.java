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
                URI.create("http://localhost:8090/jira/"), // point to your Jira
                "admin",
                "admin"
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
