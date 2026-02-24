package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
