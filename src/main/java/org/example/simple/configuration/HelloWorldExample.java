package org.example.simple.configuration;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.example.simple.workflow.SampleWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class HelloWorldExample {

    @Autowired
    WorkflowClient workflowClient;

    @Value("${taskqueue}")
    private String temporalTaskQueue;

    @EventListener(ApplicationReadyEvent.class)
    public void startThread(){
        try {
            System.out.println("In here");
            // Below line is to keep application running
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.equals("start")) {
                SampleWorkflow workflow = workflowClient.newWorkflowStub(
                        SampleWorkflow.class, WorkflowOptions.newBuilder()
                                        .setTaskQueue(temporalTaskQueue)
                                        .setWorkflowId("test-workflow")
                                .build()
                );
                String result = workflow.sayHello("test name");
                System.out.println("Result: " + result);
            } else {
                System.out.println("type 'start' to start workflow");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
