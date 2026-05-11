package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.model.Document;
import com.example.app.model.DocumentReport;
import com.example.app.model.User;

@Repository
public interface DocumentReportRepository extends JpaRepository<DocumentReport, Long> {
	List<DocumentReport> findByDocument_Id(Long documentId);

	boolean existsByUserAndDocument(User user, Document document);

	void deleteByDocument_Id(Long documentId);

	@Query("""
			    SELECT new com.example.app.dto.response.report.ReportAdminResponse(
			        d.id,
			        d.title,
			        COUNT(r)

			    )
			    FROM DocumentReport r
			    JOIN r.document d
			    WHERE d.status = :status
			    GROUP BY d.id, d.title
			    ORDER BY COUNT(r) DESC
			""")
	List<ReportAdminResponse> getAllDocumentReportSummary(@Param("status") ContentStatus status);
}
