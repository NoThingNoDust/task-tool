package com.nothing.mytask.test;


import com.nothing.mytask.Service.TriggerService;
import com.nothing.mytask.config.TaskConfigurer;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Lazy(false)
public class MinutePollingTest {

    private final TriggerService triggerService;

    private final TaskConfigurer taskConfigurer;

    public MinutePollingTest(TriggerService triggerService, TaskConfigurer taskConfigurer) {
        this.triggerService = triggerService;
        this.taskConfigurer = taskConfigurer;
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void flushTaskTest() {
        Map<Long, TriggerTask> triggerTaskMap = triggerService.getTriggerTaskMap();
        taskConfigurer.flushTasks(triggerTaskMap, false);
    }

}



















