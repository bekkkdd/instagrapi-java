package kz.biz.galamat.qabanbay.schedulers;

import kz.biz.galamat.qabanbay.models.*;
import kz.biz.galamat.qabanbay.services.InstaBotService;
import kz.biz.galamat.qabanbay.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.shuffle;

@Component
public class InstaTaskExecutor {

    @Autowired
    TaskService<LikePostTask> likePostTaskService;

    @Autowired
    TaskService<CommentPostTask> commentPostTaskService;

    @Autowired
    TaskService<LikeCommentTask> likeCommentTaskService;

    @Autowired
    InstaBotService instaBotService;

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduleLikeTask() {
        List<LikePostTask> likePostTasks = likePostTaskService.getLikePostTasksByState(TaskState.CREATED);

        likePostTasks.forEach(likePostTask -> {
            List<InstaBot> instaBots = instaBotService.findAllByChallenged(false);
            shuffle(instaBots);
            try {
                likePostTaskService.executeLikePostTask(instaBots, likePostTask);
            } catch (Exception e) {
                likePostTask.setTaskState(TaskState.FAILED);
                likePostTaskService.save(likePostTask);
                System.out.println("Smth went wrong while this task: " + likePostTask);
            }
        });
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduleCommentTask() {
        List<CommentPostTask> commentPostTasks = commentPostTaskService.getCommentPostTasksByState(TaskState.CREATED);

        commentPostTasks.forEach(commentPostTask -> {
            List<InstaBot> instaBots = instaBotService.findAllByChallenged(false);
            shuffle(instaBots);
            try {
                commentPostTaskService.executeCommentPostTask(instaBots, commentPostTask);
            } catch (Exception e) {
                commentPostTask.setTaskState(TaskState.FAILED);
                commentPostTaskService.save(commentPostTask);
                System.out.println("Smth went wrong while this task: " + commentPostTask);
            }
        });
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduleLikeCommentTask() {
        List<LikeCommentTask> likeCommentTasks = likeCommentTaskService.getLikeCommentTasksByState(TaskState.CREATED);

        likeCommentTasks.forEach(likeCommentTask -> {
            List<InstaBot> instaBots = instaBotService.findAllByChallenged(false);
            shuffle(instaBots);
            try {
                likeCommentTaskService.executeLikeCommentTask(instaBots, likeCommentTask);
            } catch (Exception e) {
                likeCommentTask.setTaskState(TaskState.FAILED);
                likeCommentTaskService.save(likeCommentTask);
                System.out.println("Smth went wrong while this task: " + likeCommentTask);
            }
        });
    }
}
