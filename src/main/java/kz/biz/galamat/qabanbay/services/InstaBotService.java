package kz.biz.galamat.qabanbay.services;

import kz.biz.galamat.qabanbay.models.InstaBot;
import kz.biz.galamat.qabanbay.models.LikePostTask;
import kz.biz.galamat.qabanbay.models.Task;
import kz.biz.galamat.qabanbay.models.TaskState;
import kz.biz.galamat.qabanbay.repositories.InstaBotRepository;
import kz.biz.galamat.qabanbay.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class InstaBotService {

    @Autowired
    InstaBotRepository instaBotRepository;

    public List<InstaBot> findAllByChallenged(Boolean challenged) {
        return instaBotRepository.findAllByChallenged(challenged);
    }

    public InstaBot findByUsername(String username) {
        return instaBotRepository.findByUsername(username);
    }

    public InstaBot findByUsernameAndPassword(String username, String password) {
        return instaBotRepository.findByUsernameAndPassword(username, password);
    }

    public String login(String login, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", login);
        map.add("password", password);
        map.add("proxy", "http://k84312720:3021415@194.110.55.105:14458");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/auth/login", request, String.class);
            return response.getBody().substring(1, response.getBody().length() - 1);
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InstaBot save(InstaBot instaBot) {
        return instaBotRepository.save(instaBot);
    }
}
