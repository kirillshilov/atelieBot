package com.sarataza.atelieBot.Repository;

import com.sarataza.atelieBot.Model.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminLoginRepository extends JpaRepository<AdminEntity, Long> {
    Optional <AdminEntity> getAdminEntityByLogin(Long login);
}
