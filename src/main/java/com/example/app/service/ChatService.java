package com.example.app.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.response.ChatHistoryResponse;
import com.example.app.exception.AppException;
import com.example.app.model.Category;
import com.example.app.repository.CategoryRepository;
import com.example.app.share.GetUserByToken;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
	private final GetUserByToken getUserByToken;
	private final CategoryRepository categoryRepository;

	public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository,
			GetUserByToken getUserByToken, CategoryRepository categoryRepository) {
		this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
		this.getUserByToken = getUserByToken;
		this.categoryRepository = categoryRepository;
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
}
