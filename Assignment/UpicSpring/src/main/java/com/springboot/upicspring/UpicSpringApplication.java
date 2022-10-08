package com.springboot.upicspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class UpicSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpicSpringApplication.class, args);
    }

    @PostMapping(value = "/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}")
    public ResponseEntity<Integer> skiers(@PathVariable(value = "resortID") Integer resortId,
                                          @PathVariable(value="seasonID") Integer seasonId,
                                          @PathVariable(value="dayID") Integer dayId,
                                          @PathVariable(value="skierID") Integer skierId) {

            if(seasonId != 2022 || dayId != 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1);
            }
            if (resortId > 10 || resortId < 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1);
            }
            if (skierId < 1 || skierId > 100000) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1);
            }

            return ResponseEntity.status(HttpStatus.OK).body(1);
        }



}
