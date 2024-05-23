/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.simple.service;

import com.uber.m3.tally.RootScopeBuilder;
import com.uber.m3.tally.Scope;
import com.uber.m3.tally.ScopeCloseException;
import com.uber.m3.tally.StatsReporter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.reporter.MicrometerClientStatsReporter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldService {

	@Value("${temporalHost}")
	private String temporalHost;

	private WorkflowServiceStubs getServiceStubs(){
		MeterRegistry meterRegistry =
				new JmxMeterRegistry(
						new JmxConfig() {
							@Override
							public String get(String s) {
								return null;
							}
						},
						Clock.SYSTEM);
		StatsReporter reporter = new MicrometerClientStatsReporter(meterRegistry);
		WorkflowServiceStubsOptions options;
		try (Scope scope = new RootScopeBuilder().reporter(reporter)
				.reportEvery(com.uber.m3.util.Duration.ofSeconds(10))) {
			options = WorkflowServiceStubsOptions.newBuilder().setMetricsScope(scope)
						.setTarget(temporalHost).setEnableHttps(false).build();
			return WorkflowServiceStubs.newServiceStubs(options);
		} catch (ScopeCloseException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error");
		}
	}

	private WorkerFactory getWorkerFactory(String namespace) {
		WorkerFactory factory = null;
		if (factory == null) {
			WorkflowClient client = getWorkflowClient(namespace);
			factory = WorkerFactory.newInstance(client,
					WorkerFactoryOptions.newBuilder().setMaxWorkflowThreadCount(5000).setWorkflowHostLocalPollThreadCount(10).setWorkflowCacheSize(5000)
							.build());
		}
		return factory;
	}

	public WorkflowClient getWorkflowClient(String namespace) {
		WorkflowServiceStubs serviceStubs = getServiceStubs();
		return WorkflowClient.newInstance(serviceStubs,
				WorkflowClientOptions.newBuilder().setNamespace(namespace).build());
	}

	public void initWorker(String namespace) {
		WorkerFactory factory = getWorkerFactory(namespace);
		try {
			if (!factory.isStarted()) {
				String taskQueue = "jmxTestTaskQueue";
				Worker worker = factory.newWorker(taskQueue);
				// registry related code
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void startWorkers(String namespace) {
		initWorker(namespace);
		WorkerFactory factory = getWorkerFactory(namespace);
		if (!factory.isStarted()) {
			factory.start();
		}
	}

}
