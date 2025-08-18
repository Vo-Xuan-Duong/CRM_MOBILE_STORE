package com.example.Backend.repositorys;

import com.example.Backend.models.RepairTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTicketRepository extends JpaRepository<RepairTicket, Long> {

    Optional<RepairTicket> findByTicketNumber(String ticketNumber);

    List<RepairTicket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<RepairTicket> findByStatusOrderByCreatedAtDesc(RepairTicket.RepairStatus status);

    List<RepairTicket> findByAssignedTechnicianIdOrderByCreatedAtDesc(Long technicianId);

    List<RepairTicket> findByPriorityOrderByCreatedAtDesc(RepairTicket.RepairPriority priority);

    @Query("SELECT rt FROM RepairTicket rt WHERE rt.serialUnit.id = :serialUnitId")
    List<RepairTicket> findBySerialUnitId(@Param("serialUnitId") Long serialUnitId);

    @Query("SELECT rt FROM RepairTicket rt WHERE rt.receivedDate BETWEEN :startDate AND :endDate")
    List<RepairTicket> findByReceivedDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT rt FROM RepairTicket rt WHERE rt.status = 'IN_PROGRESS' AND rt.startDate < :thresholdDate")
    List<RepairTicket> findOverdueRepairs(@Param("thresholdDate") LocalDateTime thresholdDate);

    @Query("SELECT COUNT(rt) FROM RepairTicket rt WHERE rt.status = :status")
    long countByStatus(@Param("status") RepairTicket.RepairStatus status);

    @Query("SELECT COUNT(rt) FROM RepairTicket rt WHERE rt.assignedTechnician.id = :technicianId AND rt.status = 'IN_PROGRESS'")
    long countActiveRepairsByTechnician(@Param("technicianId") Long technicianId);

    @Query("SELECT rt FROM RepairTicket rt WHERE rt.deviceInfo LIKE %:keyword% OR rt.issueDescription LIKE %:keyword% OR rt.ticketNumber LIKE %:keyword%")
    List<RepairTicket> searchRepairTickets(@Param("keyword") String keyword);

    @Query("SELECT AVG(FUNCTION('EXTRACT', DAY, rt.completedDate - rt.startDate)) FROM RepairTicket rt WHERE rt.status = 'COMPLETED' AND rt.startDate IS NOT NULL AND rt.completedDate IS NOT NULL")
    Double getAverageRepairDuration();
}
