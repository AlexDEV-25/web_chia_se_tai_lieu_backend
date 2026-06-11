package com.example.app.unit.service;

import com.example.app.constant.AppError;
import com.example.app.constant.HideType;
import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.category.CategoryResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.CategoryMapper;
import com.example.app.model.Category;
import com.example.app.repository.CategoryRepository;
import com.example.app.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_shouldReturnMappedResponses() {
        Category category1 = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(false).build();
        Category category2 = Category.builder().id(2L).name("Cat 2").description("Desc 2").hide(true).build();
        CategoryResponse response1 = CategoryResponse.builder().id(1L).name("Cat 1").description("Desc 1").hide(false)
                .build();
        CategoryResponse response2 = CategoryResponse.builder().id(2L).name("Cat 2").description("Desc 2").hide(true)
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));
        when(categoryMapper.entityToResponse(category1)).thenReturn(response1);
        when(categoryMapper.entityToResponse(category2)).thenReturn(response2);

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertEquals(List.of(response1, response2), responses);
    }

    @Test
    void findById_shouldReturnMappedResponse() {
        Category category = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(false).build();
        CategoryResponse response = CategoryResponse.builder().id(1L).name("Cat 1").description("Desc 1").hide(false)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.entityToResponse(category)).thenReturn(response);

        CategoryResponse actual = categoryService.findById(1L);

        assertEquals(response, actual);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> categoryService.findById(1L));

        assertEquals(AppError.CATEGORY_NOT_FOUND, exception.getAppError());
    }

    @Test
    void save_shouldCreateNewCategoryWithHideFalse() {
        CategoryRequest request = CategoryRequest.builder().name("Cat 1").description("Desc 1").hide(true).build();
        Category savedCategory = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(false).build();
        CategoryResponse response = CategoryResponse.builder().id(1L).name("Cat 1").description("Desc 1").hide(false)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.entityToResponse(savedCategory)).thenReturn(response);

        CategoryResponse actual = categoryService.save(request);

        assertEquals(response, actual);
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Cat 1", captor.getValue().getName());
        assertEquals("Desc 1", captor.getValue().getDescription());
        assertEquals(false, captor.getValue().isHide());
    }

    @Test
    void update_shouldModifyAndReturnResponse() {
        Category existing = Category.builder().id(1L).name("Old").description("Old desc").hide(false).build();
        CategoryRequest request = CategoryRequest.builder().name("New").description("New desc").hide(true).build();
        Category saved = Category.builder().id(1L).name("New").description("New desc").hide(true).build();
        CategoryResponse response = CategoryResponse.builder().id(1L).name("New").description("New desc").hide(true)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryMapper.entityToResponse(saved)).thenReturn(response);
        when(categoryRepository.save(existing)).thenReturn(saved);

        CategoryResponse actual = categoryService.update(1L, request);

        assertEquals(response, actual);
        verify(categoryMapper).updateCategory(existing, request);
        verify(categoryRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        CategoryRequest request = CategoryRequest.builder().name("New").description("New desc").hide(true).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> categoryService.update(1L, request));

        assertEquals(AppError.CATEGORY_NOT_FOUND, exception.getAppError());
    }

    @Test
    void display_shouldUpdateHideWhenTypeIsCategory() {
        Category category = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(false).build();
        DisplayRequest request = DisplayRequest.builder().hide(true).type(HideType.CATEGORY).build();
        Category saved = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(true).build();
        CategoryResponse response = CategoryResponse.builder().id(1L).name("Cat 1").description("Desc 1").hide(true)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(saved);
        when(categoryMapper.entityToResponse(saved)).thenReturn(response);

        CategoryResponse actual = categoryService.display(1L, request);

        assertEquals(response, actual);
        assertEquals(true, category.isHide());
    }

    @Test
    void display_shouldThrowWhenTypeInvalid() {
        DisplayRequest request = DisplayRequest.builder().hide(true).type(HideType.USER).build();

        AppException exception = assertThrows(AppException.class, () -> categoryService.display(1L, request));

        assertEquals(AppError.TYPE_NOT_FOUND, exception.getAppError());
    }

    @Test
    void display_shouldThrowWhenCategoryNotFound() {
        DisplayRequest request = DisplayRequest.builder().hide(true).type(HideType.CATEGORY).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> categoryService.display(1L, request));

        assertEquals(AppError.CATEGORY_NOT_FOUND, exception.getAppError());
    }

    @Test
    void delete_shouldCallRepository() {
        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void getAllPublicCategories_shouldReturnMappedResponses() {
        Category category = Category.builder().id(1L).name("Cat 1").description("Desc 1").hide(false).build();
        CategoryResponse response = CategoryResponse.builder().id(1L).name("Cat 1").description("Desc 1").hide(false)
                .build();

        when(categoryRepository.findByHideFalse()).thenReturn(List.of(category));
        when(categoryMapper.entityToResponse(category)).thenReturn(response);

        List<CategoryResponse> responses = categoryService.getAllPublicCategories();

        assertEquals(List.of(response), responses);
    }
}
