package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.jiraactions.api.SeededRandom;
import com.atlassian.performance.tools.jiraactions.api.WebJira;
import com.atlassian.performance.tools.jiraactions.api.action.Action;
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter;
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory;
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario;
import com.atlassian.performance.tools.jiraactions.api.w3c.JavascriptW3cPerformanceTimeline;
import com.atlassian.performance.tools.jiraactions.api.w3c.RecordedPerformanceEntries;
import com.atlassian.performance.tools.jiraactions.api.w3c.W3cPerformanceTimeline;
import com.atlassian.performance.tools.jirasoftwareactions.api.JiraSoftwareScenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;

public class MyScenario implements Scenario {

    @Override
    public Action getLogInAction(WebJira jira, ActionMeter meter, UserMemory userMemory) {
        return new MyLogInAction(jira, meter, userMemory);
    }

    @Override
    public List<Action> getActions(WebJira webJira, SeededRandom seededRandom, ActionMeter actionMeter) {
        Scenario scenario = new JiraSoftwareScenario();
        ActionMeter meter = actionMeter.withW3cPerformanceTimeline(
            new BestEffortW3cPerformanceTimeline(
                new JavascriptW3cPerformanceTimeline((JavascriptExecutor) webJira.getDriver())
            )
        );
        return scenario.getActions(webJira, seededRandom, meter);
    }
}

class BestEffortW3cPerformanceTimeline implements W3cPerformanceTimeline {

    private static final Logger LOG = LogManager.getLogger(BtfBenchmark.class);
    private final W3cPerformanceTimeline timeline;

    BestEffortW3cPerformanceTimeline(W3cPerformanceTimeline timeline) {
        this.timeline = timeline;
    }

    @Override
    public RecordedPerformanceEntries record() {
        try {
            RecordedPerformanceEntries entries = timeline.record();
            if (entries == null) {
                return null;
            }
            if (entries.getNavigations().isEmpty()) {
                LOG.warn("No navigations");
                return null;
            }
            return entries;
        } catch (Exception e) {
            LOG.warn("Failed to record the timeline", e);
            return null;
        }
    }
}
