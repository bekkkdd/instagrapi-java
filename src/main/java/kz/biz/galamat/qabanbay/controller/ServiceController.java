package kz.biz.galamat.qabanbay.controller;

import kz.biz.galamat.qabanbay.models.*;
import kz.biz.galamat.qabanbay.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class ServiceController {

    @Autowired
    TaskService<Task> abstractTaskService;
    @Autowired
    TaskService<LikePostTask> likePostTaskService;

    @PostMapping("/like")
    public String createLikeTask(@RequestParam Integer count,
                                    @RequestParam String postCode) {
        LikePostTask likePostTask = new LikePostTask(postCode, count, 0);
        likePostTask.setTaskType(TaskType.LIKE);
        likePostTask.setTaskState(TaskState.CREATED);

        abstractTaskService.save(likePostTask);
        return "Hello world!";
    }

    @PostMapping("/comment")
    public String createCommentTask(@RequestParam String textWithSeparator,
                                    @RequestParam String postCode) {
        CommentPostTask commentPostTask = new CommentPostTask(postCode, textWithSeparator, 0);
        commentPostTask.setTaskType(TaskType.COMMENT);
        commentPostTask.setTaskState(TaskState.CREATED);
        abstractTaskService.save(commentPostTask);
        return "Hello world!";
    }

    @PostMapping("/likeComment")
    public String createLikeCommentTask(@RequestParam Integer count,
                                        @RequestParam String postCode) {
        LikeCommentTask likeCommentTask = new LikeCommentTask(postCode, count, 0);
        likeCommentTask.setTaskType(TaskType.LIKE_COMMENT);
        likeCommentTask.setTaskState(TaskState.CREATED);
        abstractTaskService.save(likeCommentTask);
        return "Hello world!";
    }
}
