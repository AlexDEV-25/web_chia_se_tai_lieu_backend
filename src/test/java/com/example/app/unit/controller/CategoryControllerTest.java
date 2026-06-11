package com.example.app.unit.controller;

import com.example.app.controller.CategoryController;
import com.example.app.dto.response.category.CategoryResponse;
import com.example.app.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder().id(1L).name("Technology").description("Technology category")
                .hide(false).build();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/categories - Should get all public categories")
    void testGetAllPublicCategories_Success() throws Exception {
        List<CategoryResponse> categories = new ArrayList<>();
        categories.add(categoryResponse);

        when(categoryService.getAllPublicCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].name").value("Technology"))
                .andExpect(jsonPath("$.resultList[0].description").value("Technology category"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/categories - Should return empty list when no categories exist")
    void testGetAllPublicCategories_Empty() throws Exception {
        List<CategoryResponse> categories = new ArrayList<>();

        when(categoryService.getAllPublicCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories")).andExpect(status().isOk()).andExpect(jsonPath("$.resultList").isArray())
                .andExpect(jsonPath("$.resultList.length()").value(0));
    }
}
