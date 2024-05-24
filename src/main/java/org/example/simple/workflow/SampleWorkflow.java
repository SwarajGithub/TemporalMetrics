package org.example.simple.workflow;

import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SampleWorkflow {
    @WorkflowMethod
    String sayHello(String name);
}
