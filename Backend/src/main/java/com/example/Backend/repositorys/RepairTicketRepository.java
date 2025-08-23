package com.example.Backend.repositorys;

import com.example.Backend.models.RepairTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTicketRepository extends JpaRepository<RepairTicket, Long> {

    Optional<RepairTicket> findByTicketNumber(String ticketNumber);

    List<RepairTicket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<RepairTicket> findByStatusOrderByCreatedAtDesc(RepairTicket.RepairStatus status);
}

