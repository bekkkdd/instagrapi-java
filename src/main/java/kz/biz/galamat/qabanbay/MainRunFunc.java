package kz.biz.galamat.qabanbay;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.coyote.http11.Constants.a;

public class MainRunFunc {

    public static void main(String[] args) {
        try {
            System.out.println(((JSONObject)new JSONParser().parse(getUserInfo("dzengi555"))).get("profile_pic_url_hd"));
        } catch (ParseException e) {
            e.printStackTrace();
        };
    }

//    public static void main(String[] args) {
//        Map<String, String> activeSessionsAndUsernames = new HashMap<>();
//        try {
//            FileReader fileReader = new FileReader("usernameSessions.txt");
//            BufferedReader br = new BufferedReader(fileReader);
//            String line;
//            while ((line = br.readLine()) != null) {
//                activeSessionsAndUsernames.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            activeSessionsAndUsernames =
//                    readSessions("/Users/bekkkdd/Desktop/PycharmProjects/instaapi-rest/db.json", true);
//
//        }
//
//        Map<String, String> activeSessionsAndUsernamesCopy = new HashMap<>(activeSessionsAndUsernames);
//
//        int count = 0;
//
//        for (String username : activeSessionsAndUsernames.keySet()) {
//            if (checkIfPosted(username)) {
//                File readyUsernames = new File("readyUsernames.txt");
//                if (!readyUsernames.exists()) {
//                    try {
//                        readyUsernames.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                FileWriter fw = null;
//                try {
//                    fw = new FileWriter(readyUsernames, true);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                try (BufferedWriter writter = new BufferedWriter(fw)) {
//                    writter.write(username + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                count++;
//                activeSessionsAndUsernamesCopy.remove(username);
//            }
//        }
//        System.out.println("Parsed count: " + count);
//        count = 0;
//
//        while (true) {
//            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/donors.txt"))) {
//                String donorUsername;
//                boolean skipSession = false;
//                boolean skipDonor = false;
//                for (String username : activeSessionsAndUsernamesCopy.keySet()) {
//                    String session = activeSessionsAndUsernamesCopy.get(username);
//                    System.out.println("Processing " + username + " count:" + ++count);
//                    if (checkIfPosted(username)) {
//                        System.out.println("Skipping username " + username + " because already parsed.");
//                        continue;
//                    }
//                    if (skipSession) continue;
//                    while ((donorUsername = br.readLine()) != null) {
//                        if (skipDonor) continue;
//                        boolean uploadMethodInvocated = false;
//
//                        boolean moveToAnotherBot = false;
//                        JSONArray items = (JSONArray) parseAccountForSessionId(donorUsername);
//                        if (items != null && items.size() > 0) {
//                            int min = Math.min(items.size(), 5);
//                            int max = Math.min(items.size(), 15);
//                            int maxItem = ThreadLocalRandom.current().nextInt(min, max + 1);
//                            for (int i = 0; i < maxItem; i++) {
//                                JSONObject imageVersions2 = (JSONObject) ((JSONObject) items.get(i)).get("image_versions2");
//                                JSONObject caption = (JSONObject) ((JSONObject) items.get(i)).get("caption");
//                                if (imageVersions2 != null) {
//                                    JSONArray candidates = (JSONArray) imageVersions2.get("candidates");
//                                    JSONObject candidate = (JSONObject) Arrays.stream(candidates.toArray())
//                                            .filter(c -> ((long) ((JSONObject) c).get("width")) > 500 && ((long) ((JSONObject) c).get("height")) > 500)
//                                            .findFirst().orElse(null);
//                                    if (candidate == null) {
//                                        System.out.println("###WARNING not found more h w > 500 for @" + donorUsername);
//                                    } else {
//                                        System.out.println(donorUsername + " --->>> " + candidate.get("url"));
//                                        String captionStr =
//                                                caption != null && caption.get("text") != null && !caption.get("text").toString().isBlank()
//                                                        ? caption.get("text").toString() : "The best day!";
//                                        String jsonResult = null;
//                                        try {
//                                            jsonResult = uploadPhotoByUrl(session, candidate.get("url").toString(), captionStr);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println("###WARNING UPLOAD FAILED FOR " + username
//                                                    + " donor:" + donorUsername
//                                                    + " uploaded " + (i+1) + " from " + items.size());
//                                            moveToAnotherBot = true;
//                                            File readyUsernames = new File("challenged.txt");
//                                            if (!readyUsernames.exists()) {
//                                                try {
//                                                    readyUsernames.createNewFile();
//                                                } catch (IOException ex) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                            FileWriter fw = null;
//                                            try {
//                                                fw = new FileWriter(readyUsernames, true);
//                                            } catch (IOException ex) {
//                                                e.printStackTrace();
//                                            }
//
//                                            try (BufferedWriter writter = new BufferedWriter(fw)) {
//                                                writter.write(username + "\n");
//                                            } catch (IOException ex) {
//                                                e.printStackTrace();
//                                            }
//                                            break;
//                                        }
//                                        File lastPrasedAccInfo = new File("lastparsingacc.txt");
//                                        if (!lastPrasedAccInfo.exists()) {
//                                            lastPrasedAccInfo.createNewFile();
//                                        }
//                                        FileWriter fw = new FileWriter(lastPrasedAccInfo, false);
//
//                                        try (BufferedWriter writter = new BufferedWriter(fw)) {
//                                            writter.write(username + " parsed " + donorUsername + " {items.size() = " + items.size() + "} {int i = " + i + "}");
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        System.out.println("UPLOAD RESULT FOR " + username + ": " + jsonResult);
//                                        uploadMethodInvocated = true;
//                                        Thread.sleep(30000);
//                                    }
//                                }
//                            }
//                        }
//                        if (uploadMethodInvocated || moveToAnotherBot) {
//                            System.out.println(username + " uploadedMethodInvocated = " + uploadMethodInvocated
//                                    + " moveToAnotherBot = " + moveToAnotherBot);
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

//    public static void main(String[] args) {
//        Map<String, String> activeSessionsAndUsernames =
//                readSessions("/Users/bekkkdd/Desktop/PycharmProjects/instaapi-rest/db.json", false);
//
//        Map<String, String> alreadyParsedUserIdDonorMap = new HashMap<>();
//        Map<String, Integer> alreadyParsedUserIdCountMap = new HashMap<>();
//        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/sessionParsedDonors.txt"))) {
//            String userIdDonorCount;
//            while ((userIdDonorCount = br.readLine()) != null) {
//                String[] userIdDonorCountSplited = userIdDonorCount.split(":");
//                alreadyParsedUserIdDonorMap.put(userIdDonorCountSplited[0], userIdDonorCountSplited[1]);
//                alreadyParsedUserIdCountMap.put(userIdDonorCountSplited[0], Integer.valueOf(userIdDonorCountSplited[2]));
//            }
//        }catch (IOException e) {
//
//        }
//
//        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/donors.txt"))) {
//            String donorUsername;
//            for (String session: activeSessionsAndUsernames.keySet()) {
//                String userId = session.substring(0, session.indexOf(':'));
//                while ((donorUsername = br.readLine()) != null) {
//                    boolean uploadMethodInvocated = false;
//                    JSONArray items = (JSONArray) parseAccountForSessionId(donorUsername);
//                    if (items != null && items.size() > 0) {
//
//                        int startItem = 0;
//
//                        if (alreadyParsedUserIdDonorMap.get(userId).equals(donorUsername)) {
//                            if (items.size() <= alreadyParsedUserIdCountMap.get(userId)) {
//                                System.out.println("###WARNING for " + userId + " skipping because items.size= " + items.size()
//                                        + " and parsed=" + alreadyParsedUserIdCountMap.get(userId) + " from " + donorUsername);
//                                break;
//                            }
//                        }
//                        if (items.size() <= alreadyParsedUserIdCountMap.get(userId)) {
//                            System.out.println("###WARNING for " + userId + " skipping because items.size= " + items.size()
//                                    + " and parsed=" + alreadyParsedUserIdCountMap.get(userId));
//                            break;
//                        }
//                        else {
//                            startItem = alreadyParsedUserIdCountMap.get(userId);
//                        }
//                        for (; startItem < items.size(); startItem++) {
//                            JSONObject imageVersions2 = (JSONObject)((JSONObject) items.get(startItem)).get("image_versions2");
//                            JSONObject caption = (JSONObject)((JSONObject) items.get(startItem)).get("caption");
//                            if( imageVersions2 != null) {
//                                JSONArray candidates = (JSONArray) imageVersions2.get("candidates");
//                                JSONObject candidate = (JSONObject) Arrays.stream(candidates.toArray())
//                                        .filter(c -> ((long) ((JSONObject) c).get("width")) > 500 && ((long) ((JSONObject) c).get("height")) > 500)
//                                        .findFirst().orElse(null);
//                                if (candidate == null) {
//                                    System.out.println("###WARNING not found more h w > 500 for @" + donorUsername);
//                                } else {
//                                    System.out.println(donorUsername + " --->>> " + candidate.get("url"));
//                                    String captionStr =
//                                            caption != null && caption.get("text") != null && !caption.get("text").toString().isBlank()
//                                            ? caption.get("text").toString() : "The best day!";
//                                    String jsonResult = uploadPhotoByUrl(session, candidate.get("url").toString(), captionStr);
//                                    System.out.println("UPLOAD RESULT FOR " + session +  ": " + jsonResult);
//                                    uploadMethodInvocated = true;
//                                    Thread.sleep(30000);
//                                }
//                            }
//                        }
//                    }
//                    if(uploadMethodInvocated)
//                        break;
//                }
//            }
//        } catch (IOException | InterruptedException e) {
//
//        }
//
//    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        Map<String, String> loginPassMap = getLoginAndPasswordFromFile("src/main/resources/login.txt");
//
//        Map<String, String> activeSessionsAndUsernames = readSessions("/Users/bekkkdd/Desktop/PycharmProjects/instaapi-rest/db.json");
//        File file = new File("loginWithoutSessions.txt");
//        if(!file.exists()){
//            file.createNewFile();
//        }
//        FileWriter fw = new FileWriter(file,true);
//
//        for (String key : activeSessionsAndUsernames.keySet()) {
//            loginPassMap.remove(key);
//        }
//        try (BufferedWriter writter = new BufferedWriter(fw)) {
//            for (Map.Entry<String, String> entry: loginPassMap.entrySet()) {
//                writter.write(entry.getKey() + ":"  + entry.getValue() + "\n");
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
////        int i = 0;
////        for (Map.Entry<String, String> entry: loginPassMap.entrySet()) {
////            String token = login(entry.getKey(), entry.getValue());
////            System.out.println(++i + " Token for " + entry.getKey() + " --- " + token);
////            Thread.sleep(5000);
////        }
//    }


//    public static void main(String[] args) throws IOException, InterruptedException {
//        Map<String, String> loginPassMap = getLoginAndPasswordFromFile("loginWithoutSessions.txt");
//        Map<String, String> loginPassMapForFile = new HashMap<>(loginPassMap);
//
//
//
//        int i = 1;
//        for (Map.Entry<String, String> entry: loginPassMap.entrySet()) {
//            String token = login(entry.getKey(), entry.getValue());
//            System.out.println(++i + " Token for " + entry.getKey() + " --- " + token);
//            Thread.sleep(10000);
//            loginPassMapForFile.remove(entry.getKey());
//            //rewrite doc
//            File file = new File("loginWithoutSessions.txt");
//            FileWriter fw = new FileWriter(file,false);
//            try (BufferedWriter writter = new BufferedWriter(fw)) {
//                for (Map.Entry<String, String> writeEntry: loginPassMapForFile.entrySet()) {
//                    writter.write(writeEntry.getKey() + ":"  + writeEntry.getValue() + "\n");
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
////            if (i == 3) break;
//        }
//    }

//    public static Object registerAccount(String phoneNumber){
//        RestTemplate restTemplate = new RestTemplate();
//
//        String uri = "http://localhost:8000/auth/registration";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add("user-agent", "Mozilla/5.0 (Linux; Android 12; SM-N981U Build/SP1A.210812.016; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/99.0.4844.88 Mobile Safari/537.36 GoogleApp/13.11.10.23.arm64");
//
//        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();
//        parametersMap.add("sms_country_id", "16");
//
//        HttpEntity<Map> entity = new HttpEntity<>(parametersMap, headers);
//        RegistrationResponse result =
//                restTemplate.postForObject(uri, entity, RegistrationResponse.class);
//        return result;
//    }

    public static Object parseAccountForSessionId(String username) {
//        ParameterizedTypeReference<HashMap<String, String>> responseType =
//                new ParameterizedTypeReference<HashMap<String, String>>() {
//                };

        JSONParser parser = new JSONParser();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> request = RequestEntity.get("https://i.instagram.com/api/v1/feed/user/" + username + "/username/?count=15")
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers).build();
        try {
            String json = restTemplate.exchange(request, String.class).getBody();
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                return jsonObject.get("items");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (HttpClientErrorException e) {
            System.out.println("###WARNING " + username + " is private");
        }
        return null;
    }

    public static boolean checkIfPosted(String username) {
        JSONParser parser = new JSONParser();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> request = RequestEntity.get("https://i.instagram.com/api/v1/feed/user/" + username + "/username/?count=1")
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers).build();
        try {
            String json = restTemplate.exchange(request, String.class).getBody();
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(json);
                return ((JSONArray) jsonObject.get("items")).size() > 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (HttpClientErrorException e) {
            System.out.println("###WARNING " + username + " is private");
        }
        return true;
    }

    public static String login(String login, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", login);
        map.add("password", password);
        map.add("proxy", "http://k84312720:3021415@194.110.55.105:14458");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/auth/login", request, String.class);
        return response.getBody().substring(1, response.getBody().length() - 1);
    }

    public static String getUserInfo(String login) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8000/user/by/username?" +
                "sessionid=56239530778:TJK9kKDV15P0Vn:0:AYfVhLHCdViyGXGcd22tU7hpJ2-rG6_9-lPbJ4eXFQ" +
                "&username=asdasdasd" + login, String.class);
        return response.getBody();
    }

    public static Map<String, String> getLoginAndPasswordFromFile(String filename) {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                map.put(line.split(":")[0], line.split(":")[1]);
            }
        } catch (IOException e) {

        }

        return map;
    }

    //https://i.instagram.com/api/v1/users/56096728924/info/
    public static Map<String, String> readSessions(String filename, boolean withUsername) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<String> activeSessions = new ArrayList<>();
        Map<String, String> sessionUsernameMap = new HashMap<>();

        try (FileReader reader = new FileReader(filename)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject sessionList = (JSONObject) obj;
            for (int i = 1; i <= ((JSONObject) sessionList.get("_default")).size(); i++) {
                String session = ((JSONObject) ((JSONObject) sessionList.get("_default")).get(i + "")).get("sessionid").toString();
                activeSessions.add(session);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        for (String s :
                activeSessions) {

            if (withUsername) {
                String username = getUsernameFromUserId(s, s.substring(0, s.indexOf(':')));
                sessionUsernameMap.put(username, s);
            } else {
                sessionUsernameMap.put(s, null);
            }
        }
        return sessionUsernameMap;
    }

    public static String getUsernameFromUserId(String sessionId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sessionid", sessionId);
        map.add("user_id", userId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/user/username/from/id", request, String.class);
        return response.getBody().substring(1, response.getBody().length() - 1);
    }

    public static String getMediaInfoFrom(String sessionId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sessionid", sessionId);
        map.add("user_id", userId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/user/username/from/id", request, String.class);
        return response.getBody().substring(1, response.getBody().length() - 1);
    }

    public static String uploadPhotoByUrl(String sessionId, String url, String caption) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Instagram 219.0.0.12.117 Android");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("sessionid", sessionId);
        map.add("url", url);
        map.add("caption", caption);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8000/photo/upload/by_url", request, String.class);
        return response.getBody();
    }
}
