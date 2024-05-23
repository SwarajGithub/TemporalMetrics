package org.example.simple.configuration;

import org.example.simple.service.HelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class HelloWorldExample {

    @Autowired
    HelloWorldService helloWorldService;

    @EventListener(ApplicationReadyEvent.class)
    public void startThread(){
        try {
            helloWorldService.startWorkers("modn-ops");
            System.out.println("In here");
            // Below line is to keep application running
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            System.out.println(line);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
