package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	@Query("""
			    SELECT DISTINCT c FROM Conversation c
			    JOIN c.participantInfos p
			    WHERE p.user.id = :userId
			""")
	List<Conversation> findMyConversations(Long userId);

	@Query("""
			    SELECT c FROM Conversation c
			    JOIN c.participantInfos p
			    GROUP BY c.id
			    HAVING COUNT(DISTINCT p.user.id) = :size
			       AND COUNT(DISTINCT CASE
			            WHEN p.user.id IN :userIds THEN p.user.id
			       END) = :size
			""")
	Optional<Conversation> findExactConversation(List<Long> userIds, long size);

	@Query("""
			  SELECT DISTINCT c
			  FROM Conversation c
			  JOIN c.participantInfos myP
			  LEFT JOIN c.participantInfos otherP
			  WHERE myP.user.id = :myId
			    AND (
			          c.groupName LIKE CONCAT('%', :keyword, '%')
			          OR
			          otherP.user.username LIKE CONCAT('%', :keyword, '%')
			    	)
			""")
	List<Conversation> search(Long myId, String keyword);
}
