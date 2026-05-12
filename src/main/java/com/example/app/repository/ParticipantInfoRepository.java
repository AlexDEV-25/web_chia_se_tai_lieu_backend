package com.example.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.ParticipantInfo;

@Repository
public interface ParticipantInfoRepository extends JpaRepository<ParticipantInfo, Long> {
	boolean existsByConversation_IdAndUser_Id(Long conversationId, Long UserId);

	Optional<ParticipantInfo> findByIdAndUser_Id(Long id, Long userId);

	Optional<ParticipantInfo> findByUser_IdAndConversation_Id(Long userId, Long conversationId);

}
