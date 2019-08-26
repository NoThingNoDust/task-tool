package com.nothing.mytask.Service;

import com.nothing.mytask.entity.Work;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkService {


    public List<Work> findCycleTask() {
        //select form your db
        List<Work> list = new ArrayList<>();
        long randomNum = (long) (Math.random() * 1000);
        Work work = new Work();
        work.setId(randomNum);
        work.setCycle(10 * 1000L);
        work.setUpdateTime(System.currentTimeMillis());
        list.add(work);
        return list;
    }


    public void doSomeThing(Long taskId, Long updateTime) {
        System.out.println("id : " + taskId + "======= updateTime : " + updateTime);
    }

}
