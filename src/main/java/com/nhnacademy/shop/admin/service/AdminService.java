package com.nhnacademy.shop.admin.service;

import com.nhnacademy.shop.admin.dto.DashboardHomeResponse;
import com.nhnacademy.shop.admin.repository.AdminOrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminOrderQueryRepository adminOrderQueryRepository;
    @PreAuthorize(value = "hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public DashboardHomeResponse getDashboard() {
        return adminOrderQueryRepository.getAdminDashboardData();

    }
}
