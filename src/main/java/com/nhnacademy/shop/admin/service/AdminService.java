package com.nhnacademy.shop.admin.service;

import com.nhnacademy.shop.admin.dto.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        return null;

    }
}
