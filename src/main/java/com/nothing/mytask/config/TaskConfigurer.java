package com.nothing.mytask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

@EnableScheduling
@Component
public class TaskConfigurer implements SchedulingConfigurer {


    private volatile ScheduledTaskRegistrar taskRegistrar;
    private Map<Long, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() / 2 + 1);
        scheduler.setThreadNamePrefix("task-tool-thread");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        /**
         * if u just wang to add task
         * just here
         * scheduledTaskRegistrar.addTriggerTask(...)
         *
         *
         * the ScheduledTaskRegistrar.scheduleTasks() traverses the task list and calls TaskScheduler.schedule() to create a timed task every time
         * so if u just call ScheduledTaskRegistrar.afterPropertiesSet() to completion work
         * u will find tasks will be accumulated in debug
         */
        this.taskRegistrar = scheduledTaskRegistrar;
        this.taskRegistrar.setScheduler(this.taskScheduler());
    }

    public void flushTasks(Map<Long, TriggerTask> triggerTaskMap, boolean mayInterruptIfRunning) {
        Set<Long> taskIds = triggerTaskMap.keySet();
        Set<Long> futures = taskFutures.keySet();
        for (Map.Entry<Long, TriggerTask> entry : triggerTaskMap.entrySet()) {
            if (!futures.contains(entry.getKey())) {
                this.addTriggerTask(entry.getKey(), entry.getValue());
            }
        }

        for (Long future : futures) {
            if (!taskIds.contains(future)) {
                this.delTriggerTask(future, mayInterruptIfRunning);
            }
        }
    }


    private void addTriggerTask(Long taskId, TriggerTask triggerTask) {
        if (taskFutures.containsKey(taskId)) {
            return;
        }
        ScheduledFuture<?> future = taskRegistrar.getScheduler().schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        taskFutures.put(taskId, future);
    }

    private void delTriggerTask (Long taskId, boolean mayInterruptIfRunning) {
        ScheduledFuture<?> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
        taskFutures.remove(taskId);
    }
}
