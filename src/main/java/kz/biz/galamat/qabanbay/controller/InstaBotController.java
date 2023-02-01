package kz.biz.galamat.qabanbay.controller;


import kz.biz.galamat.qabanbay.models.*;
import kz.biz.galamat.qabanbay.services.InstaBotService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/instabots")
public class InstaBotController {

    Logger logger = Logger.getLogger(String.valueOf(InstaBotController.class));

    @Autowired
    InstaBotService instaBotService;
//
//    @PostMapping()
//    public InstaBot createFinancePlan(@RequestParam String username,
//                                      @RequestParam String password) {
//        InstaBot instaBot = instaBotService.findByUsernameAndPassword(username, password);
//        if (instaBot != null) {
//            return instaBot;
//        }
//        String sessionId = instaBotService.login(username, password);
//        instaBot = InstaBot.builder()
//                .sessionId(URLDecoder.decode(sessionId, StandardCharsets.UTF_8))
//                .username(username)
//                .password(password)
//                .lastSessionIdUpdate(new Date(Calendar.getInstance().getTimeInMillis()))
//                .challenged(false)
//                .build();
//        return instaBotService.save(instaBot);
//    }

    @PostMapping("/upload")
    public InstaBot createInstaBots(@RequestParam String usernamesAndPasswords) {
        List<String> usernamePassword = List.of(usernamesAndPasswords.split("\n"));
        usernamePassword.forEach(up -> {
            List<String> loginPass = List.of(up.split(":"));
            InstaBot instaBot = instaBotService.findByUsernameAndPassword(loginPass.get(0), loginPass.get(1));
            if (instaBot == null) {
                String sessionId = instaBotService.login(loginPass.get(0), loginPass.get(1));
                if (sessionId != null) {
                    instaBot = InstaBot.builder()
                            .sessionId(URLDecoder.decode(sessionId, StandardCharsets.UTF_8))
                            .username(loginPass.get(0))
                            .password(loginPass.get(1))
                            .lastSessionIdUpdate(new Date(Calendar.getInstance().getTimeInMillis()))
                            .challenged(false)
                            .build();
                    instaBotService.save(instaBot);
                } else {
                    System.out.println("Couldn't login with: " + loginPass.get(0) + ":" + loginPass.get(1));
                }
            } else {
                System.out.println("Login already in DB " + instaBot.getUsername() + ":" + instaBot.getPassword());
            }
        });
        return null;
    }

    @PostMapping("/account_picture")
    public InstaBot parsePicture(
            @RequestParam String usernamesParam,
            @RequestParam String donorsParam

    ) {
        List<String> usernames = List.of(usernamesParam.split("\n"));
        List<String> donors = List.of(donorsParam.split("\n"));
        AtomicInteger i = new AtomicInteger();
        usernames.forEach(username -> {
            InstaBot instaBot = instaBotService.findByUsername(username);

            if (instaBot != null) {
                String photoUrl = null;
                while (photoUrl == null || photoUrl.isBlank()) {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8000/user/by/username?" +
                                "sessionid=" + instaBot.getSessionId() +
                                "&username=" + donors.get(i.get()), String.class);
                        response.getBody();
                        photoUrl = ((JSONObject) new JSONParser().parse(response.getBody())).get("profile_pic_url_hd").toString();
                    } catch (HttpServerErrorException | ParseException e) {
                        e.printStackTrace();
                        System.out.println("smth wring with donor " + donors.get(i.get()));
                    }
                    i.incrementAndGet();
                }

                RestTemplate restTemplate = new RestTemplate();
                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("photo_url", photoUrl);
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
                try {
                    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/account/upload_profile_picture?sessionid=" + instaBot.getSessionId(), request, String.class);
                } catch (Exception e) {
                    System.out.println("Upload error bot: " + username + ", photo_url: " + photoUrl);
                }
            } else {
                System.out.println("Login already in DB " + instaBot.getUsername() + ":" + instaBot.getPassword());
            }
        });
        return null;
    }

    @PostMapping("/account_username")
    public InstaBot parseUsernames(
            @RequestParam String botsParam,
            @RequestParam String donorsParam
    ) {
        List<String> bots = List.of(botsParam.split("\n"));
        List<String> donors = List.of(donorsParam.split("\n"));
        AtomicInteger i = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();


        bots.forEach(bot -> {
            InstaBot instaBot = instaBotService.findByUsername(bot);

            int usernameIncrementor = 0;

            if (instaBot != null) {
                String newUsername = null;
                while (newUsername == null || newUsername.isBlank()) {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                        while(donors.get(i.get()).isBlank()) {
                            i.incrementAndGet();
                        }
                        map.add("username", donors.get(i.get()) + usernameIncrementor);
                        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
                        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/account/edit?sessionid=" + instaBot.getSessionId(), request, String.class);
                        newUsername = ((JSONObject) new JSONParser().parse(response.getBody())).get("username").toString();
                    } catch (HttpServerErrorException | ParseException e) {
                        if (e.getMessage().contains("user with that username")) {
                            System.out.println("Username: " + donors.get(i.get()) + usernameIncrementor + " is not available");
                            usernameIncrementor++;
                        } else if (e.getMessage().contains("email or")) {
                            System.out.println("Bot: " + instaBot.getUsername() + " cannot update username. Requires confirmation");
                            break;
                        } else if (e.getMessage().contains("Please wait a few minutes before you try again.")) {
                            System.out.println("Bot: " + instaBot.getUsername() + " to many request. Go to next bot ");
                            break;
                        } else if (e.getMessage().contains("challenge_required")) {
                            System.out.println("Bot: " + instaBot.getUsername() + " Challenge required.");
                            instaBot.setChallenged(true);
                            instaBotService.save(instaBot);
                            break;
                        } else {
                            e.printStackTrace();
                            System.out.println("Unexpected error for: " + instaBot.getUsername());
                        }
                        newUsername = null;
                    }
                }
                i.incrementAndGet();
                if (newUsername != null) {
                    System.out.println("Successfully updated username from " + instaBot.getUsername() + " to " + newUsername + ". Count: " + successCount.incrementAndGet());
                    instaBot.setUsername(newUsername);
                    instaBotService.save(instaBot);
                }
            }
        });
        return null;
    }
}
