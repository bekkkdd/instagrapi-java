package kz.biz.galamat.qabanbay.services;

import kz.biz.galamat.qabanbay.CommentPostUtility;
import kz.biz.galamat.qabanbay.models.*;
import kz.biz.galamat.qabanbay.repositories.InstaBotRepository;
import kz.biz.galamat.qabanbay.repositories.TaskRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TaskService<T extends Task> {

    @Autowired
    TaskRepository<T> abstractTaskRepository;

    @Autowired
    TaskRepository<LikePostTask> likePostTaskRepository;

    @Autowired
    TaskRepository<CommentPostTask> commentPostTaskRepository;

    @Autowired
    TaskRepository<LikeCommentTask> likeCommentTaskRepository;

    @Autowired
    InstaBotService instaBotService;

    public T save(T task) {
        return abstractTaskRepository.save(task);
    }

    public List<CommentPostTask> getCommentPostTasksByState(TaskState taskState) {
        return commentPostTaskRepository.findAllByTaskStateAndTaskType(taskState, TaskType.COMMENT);
    }

    public List<LikePostTask> getLikePostTasksByState(TaskState taskState) {
        return likePostTaskRepository.findAllByTaskStateAndTaskType(taskState, TaskType.LIKE);
    }

    public List<LikeCommentTask> getLikeCommentTasksByState(TaskState taskState) {
        return likeCommentTaskRepository.findAllByTaskStateAndTaskType(taskState, TaskType.LIKE_COMMENT);
    }

    public LikePostTask executeLikePostTask(List<InstaBot> instaBots, LikePostTask likePostTask) {
        likePostTask.setTaskState(TaskState.IN_PROGRESS);
        likePostTaskRepository.save(likePostTask);
        String mediaPk = getPKFromCode(likePostTask.getPostCode());

        if (instaBots.size() < likePostTask.getCount()) {
            System.out.println("BOTS are not enough. Requested count: " + likePostTask.getCount() + ", Actual bots count: " + instaBots.size());
        }

        for (int i = 0; i < Math.min(instaBots.size(), likePostTask.getCount()); i++) {
            if (i >= instaBots.size()) {
                System.out.println("Not enough bots, bots: " + instaBots.size() + ", like count: " + likePostTask.getCount());
                break;
            }
            while (hasLiked(mediaPk, instaBots.get(i))) {
                System.out.println("Already Liked from " + instaBots.get(i).getUsername() + ", to pk: " + mediaPk);
                if (++i >= instaBots.size()) {
                    break;
                }
            }
            if (i >= instaBots.size()) {
                System.out.println("Not enough bots, bots: " + instaBots.size() + ", like count: " + likePostTask.getCount());
                break;
            }
            if (likeMedia(mediaPk, instaBots.get(i))) {
                likePostTask.setCompletedCount(likePostTask.getCompletedCount() + 1);
                likePostTaskRepository.save(likePostTask);
                System.out.println("Liked from " + instaBots.get(i).getUsername() + " to code: " + likePostTask.getPostCode());
            }
        }


        if (likePostTask.getCompletedCount() < likePostTask.getCount()) {
            likePostTask.setTaskState(TaskState.PARTLY_SUCCEED);
        } else {
            likePostTask.setTaskState(TaskState.SUCCEED);
        }
        likePostTaskRepository.save(likePostTask);
        return likePostTask;
    }

    public LikeCommentTask executeLikeCommentTask(List<InstaBot> instaBots, LikeCommentTask likeCommentTask) {
        likeCommentTask.setTaskState(TaskState.IN_PROGRESS);
        likeCommentTaskRepository.save(likeCommentTask);

        if (instaBots.size() < likeCommentTask.getCount()) {
            System.out.println("BOTS are not enough for commenting. Requested count: " + likeCommentTask.getCount() + ", Actual bots count: " + instaBots.size());
        }

        for (int i = 0; i < Math.min(instaBots.size(), likeCommentTask.getCount()); i++) {
//            if (i >= instaBots.size()) {
//                System.out.println("Not enough bots, bots: " + instaBots.size() + ", like count: " + likeCommentTask.getCount());
//                break;
//            }
//            while (hasLiked(likeCommentTask.getCommentPK(), instaBots.get(i))) {
//                System.out.println("Already Liked from " + instaBots.get(i).getUsername() + ", to pk: " + likeCommentTask.getCommentPK());
//                if (++i >= instaBots.size()) {
//                    break;
//                }
//            }
            if (i >= instaBots.size()) {
                System.out.println("Not enough bots, bots: " + instaBots.size() + ", like count: " + likeCommentTask.getCount());
                break;
            }
            if (likeComment(likeCommentTask.getCommentPK(), instaBots.get(i))) {
                likeCommentTask.setCompletedCount(likeCommentTask.getCompletedCount() + 1);
                likeCommentTaskRepository.save(likeCommentTask);
                System.out.println("Liked from " + instaBots.get(i).getUsername() + " to code: " + likeCommentTask.getCommentPK());
            }
        }


        if (likeCommentTask.getCompletedCount() < likeCommentTask.getCount()) {
            likeCommentTask.setTaskState(TaskState.PARTLY_SUCCEED);
        } else {
            likeCommentTask.setTaskState(TaskState.SUCCEED);
        }
        likeCommentTaskRepository.save(likeCommentTask);
        return likeCommentTask;
    }

    public CommentPostTask executeCommentPostTask(List<InstaBot> instaBots, CommentPostTask commentPostTask) {
        commentPostTask.setTaskState(TaskState.IN_PROGRESS);
        commentPostTaskRepository.save(commentPostTask);
        String mediaPk = getPKFromCode(commentPostTask.getPostCode());

        List<String> comments = List.of(commentPostTask.getTextWithSeparator().split(CommentPostUtility.COMMENT_SEPARATOR));

        if (comments.size() > instaBots.size()) {
            System.out.println("BOTS are not enough. Requested comments count: " + comments.size() + ", Actual bots count: " + instaBots.size());
        }

        for (int i = 0; i < Math.min(instaBots.size(), comments.size()); i++) {
//            if (i >= instaBots.size()) {
//                System.out.println("Not enough bots, bots: " + instaBots.size() + ", comment count: " + commentPostTask.getCount());
//                break;
//            }
//            while (hasLiked(mediaPk, instaBots.get(i))) {
//                System.out.println("Already Liked from " + instaBots.get(i).getUsername() + ", to pk: " + mediaPk);
//                if (++i >= instaBots.size()) {
//                    break;
//                }
//            }
//            if (i >= instaBots.size()) {
//                System.out.println("Not enough bots, bots: " + instaBots.size() + ", comment count: " + commentPostTask.getCount());
//                break;
//            }
            if (commentMedia(mediaPk, comments.get(i).trim(), instaBots.get(i))) {
                commentPostTask.setCompletedCount(commentPostTask.getCompletedCount() + 1);
                commentPostTaskRepository.save(commentPostTask);
                System.out.println("Comment from " + instaBots.get(i).getUsername()
                        + " to code: " + commentPostTask.getPostCode()
                        + " text: " + comments.get(i).trim()
                );
            }
        }

        if (commentPostTask.getCompletedCount() < comments.size()) {
            commentPostTask.setTaskState(TaskState.PARTLY_SUCCEED);
        } else {
            commentPostTask.setTaskState(TaskState.SUCCEED);
        }
        commentPostTaskRepository.save(commentPostTask);
        return commentPostTask;
    }

    public static String getPKFromCode(String code) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8000/media/pk/from/code?code=" + code, String.class);
        return response.getBody().substring(1, response.getBody().length() - 1);
    }

    public Boolean likeMedia(String pk, InstaBot instaBot) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sessionid", instaBot.getSessionId());
        map.add("media_id", pk);
        map.add("revert", "false");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/media/like", request, String.class);
        return Boolean.valueOf(response.getBody());
    }

    public Boolean likeComment(String pk, InstaBot instaBot) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sessionid", instaBot.getSessionId());
        map.add("comment_pk", pk);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/comment/like", request, String.class);
        return Boolean.valueOf(response.getBody());
    }

    public Boolean commentMedia(String pk, String comment, InstaBot instaBot) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("sessionid", instaBot.getSessionId());
        map.add("media_id", pk);
        map.add("text", comment);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        try {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/comment/add", request, String.class);
            String json = response.getBody();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            if (jsonObject.get("pk") != null) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            System.out.println("New Challenged bot: " + instaBot.getUsername());
            instaBot.setChallenged(true);
            instaBotService.save(instaBot);
        }
        return false;
    }

    public Boolean hasLiked(String pk, InstaBot instaBot) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8000/media/by/id?sessionid=" + instaBot.getSessionId() + "&id=" + pk, String.class);
        String json = response.getBody();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            return (Boolean) jsonObject.get("has_liked");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
