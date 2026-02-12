package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

	List<UserNotification> findByReceiver_IdAndReadFalse(Long receiverId);

	Optional<UserNotification> findByIdAndReceiver_IdAndReadFalse(Long Id, Long receiverId);

	List<UserNotification> findByReceiver_Id(Long receiverId);
}
