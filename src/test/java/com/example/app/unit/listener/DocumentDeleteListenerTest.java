package com.example.app.unit.listener;

import com.example.app.event.DocumentDeleteEvent;
import com.example.app.helper.FileManager;
import com.example.app.listener.DocumentDeleteListener;
import com.example.app.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentDeleteListener Tests")
class DocumentDeleteListenerTest {

    @Mock
    private FileManager fileManager;

    @InjectMocks
    private DocumentDeleteListener listener;

    private Document document;

    @BeforeEach
    void setUp() {
        document = Document.builder().id(1L).title("Doc").
                fileUrl("https://example.com/file.pdf").
                thumbnailUrl("https://example.com/thumb.jpg").build();
    }

    @Test
    @DisplayName("Should delete document files on delete event")
    void testHandleDocumentDeleteEvent() throws Exception {
        DocumentDeleteEvent event = new DocumentDeleteEvent(document);

        listener.handle(event);

        verify(fileManager, times(1)).deleteFile(document.getFileUrl(), "image");
        verify(fileManager, times(1)).deleteFile(document.getThumbnailUrl(), "image");
    }

    @Test
    @DisplayName("Should handle exceptions during file deletion gracefully")
    void testHandleDocumentDeleteEventWithException() throws Exception {
        doThrow(new Exception("fail")).when(fileManager).deleteFile(anyString(), anyString());

        DocumentDeleteEvent event = new DocumentDeleteEvent(document);
        listener.handle(event);

        verify(fileManager, atLeastOnce()).deleteFile(anyString(), anyString());
    }
}

