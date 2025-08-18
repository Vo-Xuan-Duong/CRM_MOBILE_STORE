package com.example.Backend.repositorys;

import com.example.Backend.models.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Interaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Interaction> findByTypeOrderByCreatedAtDesc(Interaction.InteractionType type);

    List<Interaction> findByStatusOrderByCreatedAtDesc(Interaction.InteractionStatus status);

    List<Interaction> findByPriorityOrderByCreatedAtDesc(Interaction.InteractionPriority priority);

    List<Interaction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<Interaction> findByRequiresFollowUpTrueAndFollowUpDateLessThanEqualOrderByFollowUpDateAsc(LocalDateTime date);

    @Query("SELECT i FROM Interaction i WHERE i.customer.id = :customerId AND i.type = :type")
    List<Interaction> findByCustomerIdAndType(@Param("customerId") Long customerId, @Param("type") Interaction.InteractionType type);

    @Query("SELECT i FROM Interaction i WHERE i.status = 'OPEN' AND i.priority = 'HIGH'")
    List<Interaction> findHighPriorityOpenInteractions();

    @Query("SELECT COUNT(i) FROM Interaction i WHERE i.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(i) FROM Interaction i WHERE i.user.id = :userId AND i.createdAt >= :startDate")
    long countByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT i FROM Interaction i WHERE i.content LIKE %:keyword% OR i.subject LIKE %:keyword%")
    List<Interaction> searchInteractions(@Param("keyword") String keyword);
}
