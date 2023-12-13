package com.expenseTracker.webApplication.Controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SSEController {

//    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Map<Long,SseEmitter> emitters = new HashMap<>();
    Long currentUserId;
    @GetMapping("/sse-history")
    public SseEmitter handleSseHistory(@RequestParam("userId") String userId) {
       // System.out.println("userId"+userId);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        currentUserId=Long.parseLong(userId.toString());
        emitters.put(currentUserId,emitter);
        //emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendHistoryEvent(String message, Long userID) {
       // System.out.println("userID1"+userID);
//        emitters.forEach(emitter -> {
        SseEmitter emitter1= emitters.get(userID);
//        System.out.println("emitter"+emitter1);
        if(emitter1!=null) {
            if(currentUserId!=userID) {
                try {

                    emitter1.send(SseEmitter.event().data(message));
                } catch (Exception e) {
                    emitters.remove(emitter1);// Handle exception if emitter cannot be reached
                }
            }
        }
       // });
    }
}

