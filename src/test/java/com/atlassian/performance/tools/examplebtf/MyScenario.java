package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.jiraactions.api.SeededRandom;
import com.atlassian.performance.tools.jiraactions.api.WebJira;
import com.atlassian.performance.tools.jiraactions.api.action.Action;
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter;
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory;
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario;
import com.atlassian.performance.tools.jiraactions.api.w3c.JavascriptW3cPerformanceTimeline;
import com.atlassian.performance.tools.jirasoftwareactions.api.JiraSoftwareScenario;
import org.openqa.selenium.JavascriptExecutor;

import java.util.List;

public class MyScenario implements Scenario {

    @Override
    public Action getLogInAction(WebJira jira, ActionMeter meter, UserMemory userMemory) {
        return new MyLogInAction(jira.getDriver(), meter, userMemory);
    }

    @Override
    public List<Action> getActions(WebJira webJira, SeededRandom seededRandom, ActionMeter actionMeter) {
        Scenario scenario = new JiraSoftwareScenario();
        ActionMeter meter = actionMeter.withW3cPerformanceTimeline(
            new JavascriptW3cPerformanceTimeline((JavascriptExecutor) webJira.getDriver())
        );
        return scenario.getActions(webJira, seededRandom, meter);
    }
}
