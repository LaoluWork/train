package com.laolu.train.batch.job;

import com.laolu.train.batch.controller.Single;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class TestJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("TestJob TEST开始");
        Single.getInstance().addCount("11");
//         try {
//             Thread.sleep(3000);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
        System.out.println("TestJob TEST结束");
    }
}
