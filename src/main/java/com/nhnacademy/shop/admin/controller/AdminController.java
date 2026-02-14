package com.nhnacademy.shop.admin.controller;

import com.nhnacademy.shop.admin.dto.DashboardHomeResponse;
import com.nhnacademy.shop.admin.service.AdminService;
import com.nhnacademy.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardHomeResponse>> dashboard() {
        DashboardHomeResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
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
