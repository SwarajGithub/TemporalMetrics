package org.example.simple.configuration;

import com.uber.m3.tally.Scope;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.example.simple.workflow.SampleWorkflowImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfiguration {

    @Value("${temporalHost}")
    private String temporalHost;

    @Value("${namespace}")
    private String temporalNamespace;

    @Value("${taskqueue}")
    private String temporalTaskQueue;

    @Autowired
    Scope metricsScope;

    @Bean()
    WorkflowClient temporalWorkflowClient() {
        WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder().setMetricsScope(metricsScope)
                .setTarget(temporalHost).build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(options);

        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(temporalTaskQueue);
        worker.registerWorkflowImplementationTypes(SampleWorkflowImpl.class);
        factory.start();
        return client;
    }
}
