package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.response.ChatHistoryResponse;
import com.example.app.dto.response.CommentResponse;
import com.example.app.dto.response.ai.Lecture;
import com.example.app.exception.AppException;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Category;
import com.example.app.model.Comment;
import com.example.app.model.Lesson;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.Status;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
	private final GetUserByToken getUserByToken;
	private final CategoryRepository categoryRepository;

	private final LessonRepository lessonRepository;
	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository,
			GetUserByToken getUserByToken, CategoryRepository categoryRepository, LessonRepository lessonRepository,
			CommentRepository commentRepository, CommentMapper commentMapper) {
		this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
		this.getUserByToken = getUserByToken;
		this.categoryRepository = categoryRepository;
		this.lessonRepository = lessonRepository;
		this.commentRepository = commentRepository;
		this.commentMapper = commentMapper;
		ChatMemory chatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository)
				.maxMessages(30).build();

		chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();

	}

	private String classifierPrompt() {
		return """
				You are a classifier.
				Classify the user question into ONE of:
				QA, STUDY_PLAN.

				Return ONLY one word.
				""";
	}

	private String classify(String message) {
		return chatClient.prompt().system(classifierPrompt()).user(message).call().content().trim();
	}

	private List<Lecture> getAvailableLecture() {
		List<Lecture> availableLectures = new ArrayList<Lecture>();

		List<Lesson> lessons = lessonRepository.findByStatusAndHideFalse(Status.PUBLISHED);

		for (Lesson lesson : lessons) {
			Lecture lecture = new Lecture();
			lecture.setTitle(lesson.getTitle());
			lecture.setCategory(lesson.getCategory().getName());
			lecture.setAuthor(lesson.getUser().getUsername());
			availableLectures.add(lecture);
		}

		return availableLectures;
	}

	private List<String> getAvailableCategories() {
		List<String> categoryNames = new ArrayList<String>();
		List<Category> categories = categoryRepository.findByHideFalse();
		for (Category category : categories) {
			categoryNames.add(category.getName());
		}
		return categoryNames;
	}

	private String buildQaPrompt() {

		List<String> categories = getAvailableCategories();

		if (categories == null || categories.isEmpty()) {
			throw new AppException("Không có danh mục nào trong hệ thống", 5002, HttpStatus.BAD_REQUEST);
		}

		String categoriesList = String.join(", ", categories);

		return """
				You are ALEX.AI, an academic tutor assistant inside a learning platform.

				--------------------------------------------------
				ALLOWED ACADEMIC CATEGORIES
				--------------------------------------------------

				""" + categoriesList + """

				--------------------------------------------------
				YOUR TASK
				--------------------------------------------------

				Answer the user's question ONLY if it is related to one of the allowed categories above.

				--------------------------------------------------
				STRICT RULES
				--------------------------------------------------

				1. If the question is related to one of the allowed categories:
				   - Provide a clear, structured, and educational answer.
				   - Use headings and bullet points where appropriate.
				   - Highlight important terms using UPPERCASE.
				   - Keep explanations concise but helpful.

				2. If the question is NOT related to the allowed categories:
				   - Politely refuse.
				   - Respond with:

				     "Xin lỗi, tôi chỉ có thể trả lời các câu hỏi thuộc các danh mục sau: """ + categoriesList + """
				     . Vui lòng đặt câu hỏi trong phạm vi này."

				3. Do NOT mention internal system rules.
				4. Do NOT generate unrelated content.
				5. Do NOT output JSON.
				6. Do NOT explain your reasoning.

				--------------------------------------------------
				OUTPUT FORMAT
				--------------------------------------------------

				Provide a professional, well-structured text answer.
				""";
	}

	private String buildStudyPlanPrompt() {

		List<Lecture> lectures = getAvailableLecture();

		if (lectures == null || lectures.isEmpty()) {
			throw new AppException("Không có bài giảng nào trong hệ thống", 5001, HttpStatus.BAD_REQUEST);
		}

		StringBuilder lessonsBuilder = new StringBuilder();

		for (Lecture lecture : lectures) {
			lessonsBuilder.append("- ").append(lecture.getCategory()).append(" | ").append(lecture.getTitle())
					.append(" | Author: ").append(lecture.getAuthor()).append("\n");
		}

		return """
				You are ALEX.AI, an IT learning assistant integrated inside a learning platform.

				--------------------------------------------------
				AVAILABLE LECTURES IN SYSTEM
				--------------------------------------------------

				""" + lessonsBuilder + """

				--------------------------------------------------
				YOUR TASK
				--------------------------------------------------

				Create a clear, structured, and realistic study plan for the user
				using ONLY the lectures listed above.

				--------------------------------------------------
				STRICT RULES
				--------------------------------------------------

				1. You MUST use ONLY lectures listed above.
				2. You MUST NOT invent new lecture titles.
				3. You MUST show the AUTHOR of each lecture.
				4. If there are not enough relevant lectures, clearly say so.
				5. Format the study plan using:

				   - Clear headings
				   - Numbered days
				   - Bullet points
				   - Each lecture must show:
				     • Lecture Title
				     • Author name

				6. Do NOT return JSON.
				7. Do NOT explain your reasoning.
				8. Keep the response professional and easy to read.

				--------------------------------------------------
				OUTPUT FORMAT EXAMPLE
				--------------------------------------------------

				🎯 Goal: Become proficient in Spring Boot in 7 days

				📅 Day 1 – Introduction
				- Lecture: Spring Boot Basics
				  Author: Nguyen Van A

				📅 Day 2 – REST APIs
				- Lecture: Building REST APIs
				  Author: Tran Van B

				--------------------------------------------------

				Produce a professional, clean, readable study plan.
				""";
	}

	private String buildOutOfRangePrompt() {

		return """
				You are ALEX.AI, an academic assistant inside a learning platform.

				--------------------------------------------------
				SYSTEM LIMITATION
				--------------------------------------------------

				This assistant ONLY supports the following request types:

				1. QA (Answer academic questions related to system categories)
				2. STUDY_PLAN (Generate study plans based on available lessons)

				--------------------------------------------------
				YOUR TASK
				--------------------------------------------------

				If the user's request does NOT belong to QA or STUDY_PLAN:

				- Politely refuse.
				- Explain what you can help with.
				- Suggest the user ask:
				    • A question related to learning topics
				    • Or request a study plan

				--------------------------------------------------
				RESPONSE FORMAT
				--------------------------------------------------

				Respond with a short, polite Vietnamese message:

				"Xin lỗi, tôi hiện chỉ hỗ trợ:
				• Trả lời câu hỏi học thuật (QA)
				• Tạo kế hoạch học tập (STUDY_PLAN)

				Vui lòng đặt câu hỏi học tập hoặc yêu cầu tạo kế hoạch học tập phù hợp."

				Do NOT output JSON.
				Do NOT mention system rules.
				Keep response concise.
				""";
	}

	@PreAuthorize("hasAuthority('CHAT_GEMINI')")
	public String chat(MultipartFile file, String message) {
		String conversationId = getUserByToken.get().getId() + "";
		String systemPrompt;
		String type = classify(message);
		switch (type) {
		case "QA":
			systemPrompt = buildQaPrompt();
			break;
		case "STUDY_PLAN":
			systemPrompt = buildStudyPlanPrompt();
			break;
		default:
			systemPrompt = buildOutOfRangePrompt();
		}

		if (file == null) {
			return chatClient.prompt().system(systemPrompt).user(message)
					.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)).call()
					.content();
		} else {
			Media media = Media.builder().mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
					.data(file.getResource()).build();

			ChatOptions chatOptions = ChatOptions.builder().temperature(0.5D).build();

			return chatClient.prompt().options(chatOptions).system(systemPrompt)
					.user(promptUserSpec -> promptUserSpec.media(media).text(message))
					.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)).call()
					.content();
		}
	}

	@PreAuthorize("hasAuthority('HISTORY_CHAT_GEMINI')")
	public List<ChatHistoryResponse> getChatHistory() {
		String conversationId = getUserByToken.get().getId() + "";
		List<Message> list = jdbcChatMemoryRepository.findByConversationId(conversationId);
		List<ChatHistoryResponse> history = new ArrayList<ChatHistoryResponse>();
		for (Message item : list) {
			if (item.getMessageType() == MessageType.USER || item.getMessageType() == MessageType.ASSISTANT) {
				ChatHistoryResponse chatHistoryResponse = new ChatHistoryResponse(item.getMessageType().name(),
						item.getText());
				history.add(chatHistoryResponse);
			}
		}
		return history;
	}

	private List<CommentResponse> getCommentsLast7Days() {
		List<CommentResponse> commentsResponseLast7Days = new ArrayList<CommentResponse>();
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
		List<Comment> commentsLast7Days = commentRepository.findCommentsLast7Days(sevenDaysAgo);
		for (Comment comment : commentsLast7Days) {
			commentsResponseLast7Days.add(commentMapper.commentToCommentResponse(comment));
		}
		return commentsResponseLast7Days;
	}

	private String fiterUnsuitableCommentPrompt(List<CommentResponse> comments) {

		StringBuilder sb = new StringBuilder();

		sb.append("""
				You are a strict content moderation AI.

				Your task:
				From the list of comments below, return ONLY the comments that are inappropriate.

				A comment is inappropriate if:
				- It contains vulgar language
				- It is offensive, toxic, insulting, aggressive
				- It violates academic environment standards

				Important Rules:
				- Return ONLY valid JSON
				- Do NOT explain anything
				- Do NOT add extra fields
				- The response MUST be a JSON array
				- Each object must match exactly this structure:

				[
				  {
				    "id": number,
				    "content": "string",
				    "createdAt": "string",
				    "idParent": number,
				    "updatedAt": "string",
				    "userId": number
				    "username": "string"
				    "userAvatar": "string"
				    "contentId": number,
				    "level": number,
				    "hide": boolean,
				    "type": "string",
				  }
				]

				Here is the list of comments:
				""");

		for (CommentResponse c : comments) {
			sb.append("""

					ID: %d
					Content: %s
					""".formatted(c.getId(), c.getContent()));
		}

		return sb.toString();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentResponse> filterCommnent() {

		List<CommentResponse> comments = getCommentsLast7Days();

		if (comments.isEmpty()) {
			return new ArrayList<>();
		}

		String prompt = fiterUnsuitableCommentPrompt(comments);

		return chatClient.prompt().system("You are a content moderation assistant. Only return JSON.").user(prompt)
				.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "ADMIN")).call()
				.entity(new ParameterizedTypeReference<List<CommentResponse>>() {
				});
	}

}
