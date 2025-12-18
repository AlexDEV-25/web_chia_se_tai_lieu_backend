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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.response.ChatHistoryResponse;
import com.example.app.share.GetUserByToken;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
	private final GetUserByToken getUserByToken;

	public ChatService(ChatClient.Builder builder, JdbcChatMemoryRepository jdbcChatMemoryRepository,
			GetUserByToken getUserByToken) {
		this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
		this.getUserByToken = getUserByToken;
		ChatMemory chatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository)
				.maxMessages(30).build();

		chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();

	}

	@PreAuthorize("hasAuthority('CHAT_GEMINI')")
	public String chat(MultipartFile file, String message) {
		String conversationId = getUserByToken.get().getId() + "";
		if (file == null) {
			return chatClient.prompt().system("""
					You are ALEX.AI.
					Always format answers like:
					- Each item on a new line
					- Use **UPPERCASE** for names
					""").user(message)
					.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)).call()
					.content();
		} else {
			Media media = Media.builder().mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
					.data(file.getResource()).build();

			ChatOptions chatOptions = ChatOptions.builder().temperature(0.5D).build();

			return chatClient.prompt().options(chatOptions).system("""
					You are ALEX.AI.
					Always format answers like:
					- Each item on a new line
					- Use **UPPERCASE** for names
					""").user(promptUserSpec -> promptUserSpec.media(media).text(message))
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
