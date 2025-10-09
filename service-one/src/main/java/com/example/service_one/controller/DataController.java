package com.example.service_one.controller;

import com.example.service_one.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
//подключи Lombok. тогда тут будет аннотация @RequaredArgConstructor которая под капотом сама сгенерит конструкторы и не надо будет пользоваться @Autowired
public class DataController {

    @Autowired
    //тут final поле
    private RedisService redisService;

    //это в env и доставать от туда через @Value
    private static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEQuLkfUsgdE7V1gnp60feVXgahObQsegvqK8jFKxq7QLTjj3m4bvPKxJ3O3yhRrzZjqAlx9aDn/h5zf69gqOiDg==";

    @GetMapping("/process") //обычно пишут /api/** это ж бекенд
    public ResponseEntity<ApiResponse> processData () {
        ApiResponse response = new ApiResponse(); //вообще избавиться от любых явных конструкторов, заменить на @Builder из Lombok

        try {
            int dataSize = 200 * 1024; //помоему в задании принять параметр в контроллере а не генерировать самим? не помню
            byte[] randomData = new byte[dataSize];
            new SecureRandom().nextBytes(randomData);

            redisService.saveData(randomData); //redisService должен быть интерфейсом со своей реализаций, а вы тут прям класс инжектите, чатГпт расскажет зачем так

            response.setMessage("Data saved!");
            response.setSuccess(true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Data error: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response); //так не делают, почитать про controllerAdvice
        }
    }

    public static class ApiResponse { //это должен быть отдельный класс в отдельной папочке рядом с контроллером. обычный POJO без методов с аннотаций @Data из ломбока
        private boolean success;
        private String message;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

}
