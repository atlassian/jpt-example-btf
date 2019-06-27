package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.dockerinfrastructure.api.jira.JiraCoreFormula;
import com.atlassian.performance.tools.infrastructure.api.virtualusers.LocalVirtualUsers;
import com.atlassian.performance.tools.report.api.FullReport;
import com.atlassian.performance.tools.report.api.FullTimeline;
import com.atlassian.performance.tools.report.api.result.EdibleResult;
import com.atlassian.performance.tools.report.api.result.RawCohortResult;
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions;
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior;
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget;
import com.atlassian.performance.tools.workspace.api.RootWorkspace;
import com.atlassian.performance.tools.workspace.api.TaskWorkspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

class BtfBenchmark {

    private static final Logger LOG = LogManager.getLogger(BtfBenchmark.class);
    private final VirtualUserTarget target;
    private final VirtualUserBehavior behavior;
    private final TaskWorkspace workspace = new RootWorkspace().getCurrentTask();

    BtfBenchmark(
        VirtualUserTarget target,
        VirtualUserBehavior behavior
    ) {
        this.target = target;
        this.behavior = behavior;
    }

    void run() {
        RawCohortResult result = applyLoad();
        report(result);
    }

    private RawCohortResult applyLoad() {
        VirtualUserOptions options = new VirtualUserOptions(target(), behavior);
        LOG.info("Benchmarking your Jira...");
        LOG.info("When this is finished, look for results in " + workspace.getDirectory());
        LocalVirtualUsers virtualUsers = new LocalVirtualUsers(workspace.getDirectory());
        try {
            virtualUsers.applyLoad(options);
        } finally {
            virtualUsers.gatherResults();
        }
        return new RawCohortResult.Factory().fullResult(
            "my-jira",
            workspace.getDirectory()
        );
    }

    private VirtualUserTarget target() {
        if (Objects.equals(System.getProperty("jpt.btf.provision"), "true")) {
            return provision();
        } else {
            return target;
        }
    }

    private VirtualUserTarget provision() {
        LOG.info("Provisioning a Jira in Docker...");
        URI uri = new JiraCoreFormula.Builder()
            .inDockerNetwork(false)
            .build()
            .provision()
            .getUri();
        return new VirtualUserTarget(
            uri,
            "admin",
            "admin"
        );
    }

    private void report(
        RawCohortResult rawResult
    ) {
        EdibleResult result = rawResult.prepareForJudgement(new FullTimeline());
        List<String> labels = new ArrayList<>(result.getActionLabels());
        Collections.sort(labels);
        new FullReport().dump(
            singletonList(result),
            workspace.isolateTest("btf-benchmark"),
            labels
        );
        LOG.info("Benchmark complete! Look for results in " + workspace.getDirectory());
    }
}
