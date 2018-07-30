package pjatk;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class KuebikoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuebikoApplication.class, args);
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(taskExecutor());
	}

	@Bean
	public ScheduledExecutorService taskExecutor() {
		return Executors.newScheduledThreadPool(10);
	}
}
