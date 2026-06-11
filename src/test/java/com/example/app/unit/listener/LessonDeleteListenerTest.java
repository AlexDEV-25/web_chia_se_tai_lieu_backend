package com.example.app.unit.listener;

import com.example.app.event.LessonDeleteEvent;
import com.example.app.helper.FileManager;
import com.example.app.listener.LessonDeleteListener;
import com.example.app.model.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonDeleteListener Tests")
class LessonDeleteListenerTest {

    @Mock
    private FileManager fileManager;

    @InjectMocks
    private LessonDeleteListener listener;

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lesson = Lesson.builder().id(1L).title("L").lessonUrl("https://example.com/video.mp4").
                documentUrl("https://example.com/doc.pdf").thumbnailUrl("https://example.com/thumb.jpg").
                subFileUrl("https://example.com/sub.zip").build();
    }

    @Test
    @DisplayName("Should delete lesson files on delete event")
    void testHandleLessonDeleteEvent() throws Exception {
        LessonDeleteEvent event = new LessonDeleteEvent(lesson);

        listener.handle(event);

        verify(fileManager, times(1)).deleteFile(lesson.getLessonUrl(), "video");
        verify(fileManager, times(1)).deleteFile(lesson.getDocumentUrl(), "image");
        verify(fileManager, times(1)).deleteFile(lesson.getThumbnailUrl(), "image");
        verify(fileManager, times(1)).deleteFile(lesson.getSubFileUrl(), "raw");
    }

    @Test
    @DisplayName("Should handle exceptions during file deletion gracefully")
    void testHandleLessonDeleteEventWithException() throws Exception {
        doThrow(new Exception("fail")).when(fileManager).deleteFile(anyString(), anyString());

        LessonDeleteEvent event = new LessonDeleteEvent(lesson);
        listener.handle(event);

        verify(fileManager, atLeastOnce()).deleteFile(anyString(), anyString());
    }
}

