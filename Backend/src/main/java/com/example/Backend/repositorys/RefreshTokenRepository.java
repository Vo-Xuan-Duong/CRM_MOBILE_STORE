package com.example.Backend.repositorys;

import com.example.Backend.models.RefreshToken;
import com.example.Backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUser_Id(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
