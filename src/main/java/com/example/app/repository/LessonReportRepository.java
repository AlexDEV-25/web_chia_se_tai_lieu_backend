package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.model.Lesson;
import com.example.app.model.LessonReport;
import com.example.app.model.User;

public interface LessonReportRepository extends JpaRepository<LessonReport, Long> {
	List<LessonReport> findByLesson_Id(Long lessonId);

	boolean existsByUserAndLesson(User user, Lesson lesson);

	void deleteByLesson_Id(Long lessonId);

	@Query("""
			    SELECT new com.example.app.dto.response.report.ReportAdminResponse(
			        l.id,
			        l.title,
			        COUNT(r)
			    )
			    FROM LessonReport r
			    JOIN r.lesson l
			    WHERE l.status = :status
			    GROUP BY l.id, l.title
			    ORDER BY COUNT(r) DESC
			""")
	List<ReportAdminResponse> getAllLessonReportSummary(@Param("status") ContentStatus status);

}
