package com.nhnacademy.shop.admin.controller;

import com.nhnacademy.shop.admin.dto.AdminDashboardResponse;
import com.nhnacademy.shop.admin.service.AdminService;
import com.nhnacademy.shop.delivery.dto.DeliveryRegistrationRequest;
import com.nhnacademy.shop.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class AdminController {
    private final AdminService adminService;
    private final DeliveryService deliveryService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> dashboard() {
        AdminDashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }



//    @GetMapping("/author")
//    public ResponseEntity<?> getAuthor(@RequestParam String name) {
//
//    }
//
//    @GetMapping("/publisher")
//    public ResponseEntity<?> getPublisher(@RequestParam String name) {
//
//    }
}
