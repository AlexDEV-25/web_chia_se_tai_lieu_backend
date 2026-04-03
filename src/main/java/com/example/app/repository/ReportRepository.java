package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.Report;
import com.example.app.model.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByDocument_Id(Long documentId);

	List<Report> findByLesson_Id(Long lessonId);

	boolean existsByUserAndDocument(User user, Document document);

	boolean existsByUserAndLesson(User user, Lesson lesson);

	@Query("""
			    SELECT new com.example.app.dto.response.report.ReportAdminResponse(
			        d.id,
			        d.title,
			        COUNT(r),
			        com.example.app.share.Type.DOCUMENT
			    )
			    FROM Report r
			    JOIN r.document d
			    WHERE r.type = com.example.app.share.Type.DOCUMENT
			      AND d.status = com.example.app.share.Status.PUBLISHED
			    GROUP BY d.id, d.title
			    ORDER BY COUNT(r) DESC
			""")
	List<ReportAdminResponse> getAllDocumentReportSummary();

	@Query("""
			    SELECT new com.example.app.dto.response.report.ReportAdminResponse(
			        l.id,
			        l.title,
			        COUNT(r),
			        com.example.app.share.Type.LESSON
			    )
			    FROM Report r
			    JOIN r.lesson l
			    WHERE r.type = com.example.app.share.Type.LESSON
			      AND l.status = com.example.app.share.Status.PUBLISHED
			    GROUP BY l.id, l.title
			    ORDER BY COUNT(r) DESC
			""")
	List<ReportAdminResponse> getAllLessonReportSummary();
}
