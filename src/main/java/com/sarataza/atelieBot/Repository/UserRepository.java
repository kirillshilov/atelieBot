package com.sarataza.atelieBot.Repository;

import com.sarataza.atelieBot.Model.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<AppUserEntity, Long> {
    Optional<AppUserEntity> getAppUserEntitiesByLogin(Long login);
    Optional<AppUserEntity> getAppUserEntitiesByPhone(String phone);
}
