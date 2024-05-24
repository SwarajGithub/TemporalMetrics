package org.example.simple.workflow;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class SampleWorkflowImpl implements SampleWorkflow {
    @Override
    public String sayHello(String name) {
        Workflow.sleep(Duration.ofSeconds(1));
        return "Hello " + name;
    }
}
