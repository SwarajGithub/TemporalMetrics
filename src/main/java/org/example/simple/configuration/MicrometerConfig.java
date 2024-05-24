package org.example.simple.configuration;

import com.uber.m3.tally.RootScopeBuilder;
import com.uber.m3.tally.Scope;
import com.uber.m3.tally.StatsReporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.temporal.common.reporter.MicrometerClientStatsReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

@Configuration
public class MicrometerConfig {
    @Bean(destroyMethod = "close")
    public Scope scope(
            @Autowired(required = false) @Nullable MeterRegistry meterRegistry) {
        StatsReporter reporter = new MicrometerClientStatsReporter(meterRegistry);
        return new RootScopeBuilder()
                .reporter(reporter)
                .reportEvery(com.uber.m3.util.Duration.ofSeconds(10));
    }
}
