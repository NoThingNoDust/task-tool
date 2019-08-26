package com.nothing.mytask.Service;


import com.nothing.mytask.entity.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class TriggerService {

    @Autowired
    private WorkService workService;

    public Map<Long, TriggerTask> getTriggerTaskMap() {
        //you can get tasks from your db
        List<Work> taskList = workService.findCycleTask();
        if (CollectionUtils.isEmpty(taskList)) {
            return new HashMap<>();
        }
        Map<Long, TriggerTask> triggerTaskMap = new HashMap<>();
        for (Work work : taskList) {
             // each task has a separate id to distinguish whether the task has been modified
             // so i use workId * updateTime to build taskId
            Long taskId = (work.getId() * work.getUpdateTime());
            /**
             *
             * https://docs.spring.io/spring/docs/3.2.x/javadoc-api/org/springframework/scheduling/config/ScheduledTaskRegistrar.html
             *  because v will doing something every 50 minutes
             *  cron can't do it, so v need  TriggerTask<Runnable, Trigger>
             */
            TriggerTask triggerTask = new TriggerTask(() ->
                    workService.doSomeThing(work.getId(), work.getUpdateTime()),
                    new PeriodicTrigger(work.getCycle()));
            triggerTaskMap.put(taskId, triggerTask);
        }
        return triggerTaskMap;
    }
}
