package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.services.MomoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final MomoService momoService;

    public TestController(MomoService momoService) {
        this.momoService = momoService;
    }

    @GetMapping("/momo_test")
    public ResponseEntity<?>  getMomoTest(@RequestParam("orderId")String orderId, @RequestParam("amount")Long amount, @RequestParam("orderInfo")String orderInfo){
        try{
            ResponseData<?> responseData = ResponseData.builder()
                    .message("Success")
                    .status(200)
                    .data(momoService.createOrder(orderId, amount, orderInfo))
                    .build();
            return ResponseEntity.ok(responseData);
        }catch(Exception e){
            ResponseData<?> responseData = ResponseData.builder()
                    .message("Success")
                    .status(200)
                    .data(momoService.createOrder(orderId, amount, orderInfo))
                    .build();
            return ResponseEntity.ok(responseData);
        }
    }
}
