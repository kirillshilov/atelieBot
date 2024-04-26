package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.AdminLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminLoginRepository adminLoginRepository;
    public AdminEntity updateAdmin (AdminEntity adminEntity){
        return adminLoginRepository.save(adminEntity);
    }
    public Optional<AdminEntity> getAdminByLogin(Long chatID) {
        return adminLoginRepository.getAdminEntityByLogin(chatID);
    }

}
