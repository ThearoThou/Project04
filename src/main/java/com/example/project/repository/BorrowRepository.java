package com.example.project.repository;

import com.example.project.entity.BorrowRecord;
import com.example.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUser(User user);
    List<BorrowRecord> findByStatus(String status);
    List<BorrowRecord> findByUserOrderByBorrowDateDesc(User user);
}