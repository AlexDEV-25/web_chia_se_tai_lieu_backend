package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.app.dto.response.ai.AiResponse;
import com.example.app.dto.response.ai.AvailableValue;
import com.example.app.exception.AppException;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Category;
import com.example.app.model.Comment;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.Status;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
	private final GetUserByToken getUserByToken;
	private final CategoryRepository categoryRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository,
			GetUserByToken getUserByToken, CategoryRepository categoryRepository, DocumentRepository documentRepository,
			LessonRepository lessonRepository, CommentRepository commentRepository, CommentMapper commentMapper) {
		this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
		this.getUserByToken = getUserByToken;
		this.categoryRepository = categoryRepository;
		this.documentRepository = documentRepository;
		this.lessonRepository = lessonRepository;
		this.commentRepository = commentRepository;
		this.commentMapper = commentMapper;
		ChatMemory chatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository)
				.maxMessages(30).build();

		chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();

	}

	private List<String> getAvailableCategories() {
		List<String> categoryNames = new ArrayList<String>();
		List<Category> categories = categoryRepository.findByHideFalse();
		for (Category category : categories) {
			categoryNames.add(category.getName());
		}
		return categoryNames;
	}

	private String buildSystemPrompt() {
		List<String> categories = getAvailableCategories();

		if (categories.isEmpty()) {
			throw new AppException("Không có danh mục nào trong hệ thống", 5000, HttpStatus.BAD_REQUEST);
		}

		String categoriesList = String.join(", ", categories);

		return """
				You are ALEX.AI, an academic tutor assistant.

				You ONLY answer questions related to these academic subjects:
				""" + categoriesList + """

				Important Rules:
				1. Only answer academic questions within the listed categories
				2. If a question is not academic or not within these categories, politely decline
				3. Provide helpful, clear, and educational responses
				4. Format your answers like:
				   - Use clear structure with bullet points or numbered lists
				   - Use **BOLD** for important terms and **UPPERCASE** for key concepts
				   - Keep explanations concise but comprehensive

				If someone asks about topics outside these categories, respond with:
				"Xin lỗi, Tôi chỉ có thể trả lời các câu hỏi về: """ + categoriesList
				+ ". Xin hãy hỏi về các chủ đề này.";
	}

	@PreAuthorize("hasAuthority('CHAT_GEMINI')")
	public String chat(MultipartFile file, String message) {
		String conversationId = getUserByToken.get().getId() + "";
		String systemPrompt = buildSystemPrompt();

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

	private String checkTypeQuestionPrompt() {

		return """
				Classify the following question into ONE of these types:
				- QA
				- STUDY_PLAN
				- COMPARISON

				Definitions:
				- QA: Asking for information, explanation, definition, or how something works.
				- STUDY_PLAN: Asking for a learning plan, roadmap, schedule, or step-by-step study guidance.
				- COMPARISON: Asking to compare two or more things, including pros and cons.

				Question:
				"{question}"

				Return ONLY one value: QA, STUDY_PLAN, or COMPARISON.
				""";
	}

	public String test(String message) {
		String systemPrompt = checkTypeQuestionPrompt();
		return chatClient.prompt().system(systemPrompt).user(message).call().content();

	}

	private List<AvailableValue> getAvailableDocumentAndLessonByCategory() {
		List<AvailableValue> availableValues = new ArrayList<AvailableValue>();

		List<Category> categories = categoryRepository.findByHideFalse();

		for (Category category : categories) {
			AvailableValue availableValue = new AvailableValue();
			availableValue.setCategoryName(category.getName());

			List<Document> documents = documentRepository.findByCategoryAndStatusAndHideFalse(category,
					Status.PUBLISHED);
			List<String> documentNames = new ArrayList<String>();
			for (Document document : documents) {
				documentNames.add(document.getTitle());
			}
			availableValue.setDocumentNames(documentNames);

			List<Lesson> lessons = lessonRepository.findByCategoryAndStatusAndHideFalse(category, Status.PUBLISHED);
			List<String> lessonNames = new ArrayList<String>();
			for (Lesson lesson : lessons) {
				lessonNames.add(lesson.getTitle());
			}
			availableValue.setLessonNames(lessonNames);
			availableValues.add(availableValue);

		}
		return availableValues;
	}

	private String newPrompt() {

		List<AvailableValue> availableValues = getAvailableDocumentAndLessonByCategory();

		if (availableValues == null || availableValues.isEmpty()) {
			throw new AppException("Không có danh mục nào trong hệ thống", 5000, HttpStatus.BAD_REQUEST);
		}

		// Build category list
		String categoriesList = availableValues.stream().map(AvailableValue::getCategoryName).distinct().sorted()
				.collect(Collectors.joining(", "));

		// Build documents & lessons list
		StringBuilder documentsBuilder = new StringBuilder();
		StringBuilder lessonsBuilder = new StringBuilder();

		for (AvailableValue value : availableValues) {

			String category = value.getCategoryName();

			if (value.getDocumentNames() != null && !value.getDocumentNames().isEmpty()) {
				documentsBuilder.append("Category: ").append(category).append("\n");
				for (String doc : value.getDocumentNames()) {
					documentsBuilder.append("- ").append(doc).append("\n");
				}
				documentsBuilder.append("\n");
			}

			if (value.getLessonNames() != null && !value.getLessonNames().isEmpty()) {
				lessonsBuilder.append("Category: ").append(category).append("\n");
				for (String lesson : value.getLessonNames()) {
					lessonsBuilder.append("- ").append(lesson).append("\n");
				}
				lessonsBuilder.append("\n");
			}
		}

		return """
				You are ALEX.AI, an academic IT tutor integrated inside a learning platform.

				--------------------------------------------------
				SYSTEM CATEGORIES
				--------------------------------------------------
				""" + categoriesList + """

				--------------------------------------------------
				AVAILABLE SYSTEM CONTENT
				--------------------------------------------------

				Documents in the system:
				""" + documentsBuilder + """

				Lessons in the system:
				""" + lessonsBuilder + """

				--------------------------------------------------
				BEHAVIOR RULES
				--------------------------------------------------

				1. You are allowed to answer ANY question related to Information Technology (IT).

				2. If the user question matches or is closely related to the system documents or lessons:
				   - Prioritize and align your answer with the system content.
				   - Do NOT invent internal lessons or documents.

				3. If the question is IT-related but not covered in the system content:
				   - You may answer using general IT knowledge.
				   - Do NOT imply that the content exists in the system.

				4. If the question is NOT related to IT:
				   - Set status to "OUT_OF_SCOPE".

				5. Only set status to "ERROR" when:
				   - The user requests specific internal lesson content that does not exist.
				   - Or the request is technically invalid.

				6. Do NOT generate URLs.
				7. Do NOT generate IDs.
				8. Do NOT include explanations outside the JSON.
				9. Always return VALID JSON only.
				10. All unused content fields must be null.

				--------------------------------------------------
				RESPONSE FORMAT (STRICT)
				--------------------------------------------------

				{
				  "type": "QA | STUDY_PLAN | COMPARISON",
				  "status": "SUCCESS | OUT_OF_SCOPE | ERROR",
				  "conclusion": "string or null",

				  "comparisonContent": {
				    "subjects": ["string"],
				    "rows": [
				      {
				        "aspect": "string",
				        "values": {
				          "subjectName": "string"
				        }
				      }
				    ]
				  },

				  "qaContent": {
				    "answer": "string",
				    "sections": [
				      {
				        "title": "string",
				        "contents": ["string"]
				      }
				    ],
				    "followUpQuestions": ["string"]
				  },

				  "studyPlanContent": {
				    "goal": "string",
				    "totalDays": number,
				    "schedule": [
				      {
				        "day": number,
				        "topic": "string",
				        "suggestedLessons": ["string"]
				      }
				    ]
				  }
				}

				--------------------------------------------------
				TYPE SELECTION RULES
				--------------------------------------------------

				- If the user asks a normal IT question → type = "QA"
				- If the user asks to compare one or more concepts → type = "COMPARISON"
				- If the user asks for a roadmap, learning plan, or schedule → type = "STUDY_PLAN"

				--------------------------------------------------
				COMPARISON RULES
				--------------------------------------------------

				- "subjects" must contain ALL compared items.
				- Each row must contain values for ALL subjects.
				- Provide at least 3 comparison aspects.
				- If the user asks which option is better, provide the final recommendation inside "conclusion".
				- When recommendation is requested, "conclusion" must NOT be null.

				--------------------------------------------------
				FIELD RULES
				--------------------------------------------------

				If type = QA:
				- Fill qaContent
				- Set comparisonContent = null
				- Set studyPlanContent = null
				- Set conclusion = null (unless user explicitly asks for recommendation)

				If type = COMPARISON:
				- Fill comparisonContent
				- Set qaContent = null
				- Set studyPlanContent = null
				- Use conclusion if a final decision or recommendation is requested

				If type = STUDY_PLAN:
				- Fill studyPlanContent
				- Set qaContent = null
				- Set comparisonContent = null
				- Set conclusion = null

				For STUDY_PLAN:
				- totalDays must match schedule length
				- suggestedLessons must match EXACT lesson titles from the system list
				- Do NOT invent lesson titles

				--------------------------------------------------
				FINAL INSTRUCTION
				--------------------------------------------------

				Return ONLY valid JSON.
				Do not include markdown.
				Do not include commentary.
				Do not include backticks.
				""";
	}

	public AiResponse newChat(MultipartFile file, String message) {
		String systemPrompt = newPrompt();

		if (file == null) {
			return chatClient.prompt().system(systemPrompt).user(message).call().entity(AiResponse.class);
		} else {
			Media media = Media.builder().mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
					.data(file.getResource()).build();

			ChatOptions chatOptions = ChatOptions.builder().temperature(0.5D).build();

			return chatClient.prompt().options(chatOptions).system(systemPrompt)
					.user(promptUserSpec -> promptUserSpec.media(media).text(message)).call().entity(AiResponse.class);
		}
	}

}
