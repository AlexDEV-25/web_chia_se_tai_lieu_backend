package com.example.app.unit.controller;

import com.example.app.dto.response.userfollow.FollowCountResponse;
import com.example.app.dto.response.userfollow.UserFollowResponse;
import com.example.app.service.UserFollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("UserFollowController Tests")
class UserFollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFollowService userFollowService;

    private UserFollowResponse userFollowResponse;
    private FollowCountResponse followCountResponse;

    @BeforeEach
    void setUp() {
        userFollowResponse = UserFollowResponse.builder().id(1L).followerId(1L).followingId(2L).build();

        followCountResponse = FollowCountResponse.builder().following(10L).follower(5L).build();
    }

    @Test
    @DisplayName("POST /api/follows/{followingId} - Should follow user successfully")
    @WithMockUser(authorities = "FOLLOW")
    void testSave_Success() throws Exception {
        when(userFollowService.save(anyLong())).thenReturn(userFollowResponse);

        mockMvc.perform(post("/api/follows/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.followingId").value(2L));
    }

    @Test
    @DisplayName("DELETE /api/follows/{followingId} - Should unfollow user successfully")
    @WithMockUser(authorities = "UNFOLLOW")
    void testDelete_Success() throws Exception {
        doNothing().when(userFollowService).delete(anyLong());

        mockMvc.perform(delete("/api/follows/2")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/follows/follow-count/{userId} - Should get user follow counts")
    void testGetFollowCount_Success() throws Exception {
        when(userFollowService.getFollowCount(anyLong())).thenReturn(followCountResponse);

        mockMvc.perform(get("/api/follows/follow-count/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.following").value(10L))
                .andExpect(jsonPath("$.result.follower").value(5L));
    }

    @Test
    @DisplayName("GET /api/follows/following - Should get users being followed")
    @WithMockUser(authorities = "GET_LIST_FOLLOWING")
    void testGetFollowing_Success() throws Exception {
        List<UserFollowResponse> following = new ArrayList<>();
        following.add(userFollowResponse);

        when(userFollowService.getFollowingByFollower()).thenReturn(following);

        mockMvc.perform(get("/api/follows/following")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].followingId").value(2L));
    }

    @Test
    @DisplayName("GET /api/follows/follower - Should get followers")
    @WithMockUser(authorities = "GET_LIST_FOLLOWER")
    void testGetFollower_Success() throws Exception {
        List<UserFollowResponse> followers = new ArrayList<>();
        followers.add(userFollowResponse);

        when(userFollowService.getFollowerByFollowing()).thenReturn(followers);

        mockMvc.perform(get("/api/follows/follower")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].followerId").value(1L));
    }

    @Test
    @DisplayName("GET /api/follows/my-follow-count - Should get current user follow counts")
    @WithMockUser(authorities = "GET_MY_FOLLOW_COUNT")
    void testGetMyFollowCount_Success() throws Exception {
        when(userFollowService.getMyFollowCount()).thenReturn(followCountResponse);

        mockMvc.perform(get("/api/follows/my-follow-count")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.following").value(10L))
                .andExpect(jsonPath("$.result.follower").value(5L));
    }

    @Test
    @DisplayName("GET /api/follows/check/{userId} - Should check if user is followed")
    @WithMockUser(authorities = "CHECK_FOLLOWED")
    void testCheckFollowed_True() throws Exception {
        when(userFollowService.checkFollowed(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/follows/check/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @DisplayName("GET /api/follows/check/{userId} - Should return false when not followed")
    @WithMockUser(authorities = "CHECK_FOLLOWED")
    void testCheckFollowed_False() throws Exception {
        when(userFollowService.checkFollowed(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/follows/check/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    @DisplayName("GET /api/follows/check-is-me/{userId} - Should check if same user")
    @WithMockUser(authorities = "CHECK_IS_ME")
    void testCheckIsMe_True() throws Exception {
        when(userFollowService.checkIsMe(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/follows/check-is-me/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @DisplayName("GET /api/follows/check-is-me/{userId} - Should return false when different user")
    @WithMockUser(authorities = "CHECK_IS_ME")
    void testCheckIsMe_False() throws Exception {
        when(userFollowService.checkIsMe(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/follows/check-is-me/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    @DisplayName("GET /api/follows/following - Should return empty list when not following anyone")
    @WithMockUser(authorities = "GET_LIST_FOLLOWING")
    void testGetFollowing_Empty() throws Exception {
        List<UserFollowResponse> following = new ArrayList<>();

        when(userFollowService.getFollowingByFollower()).thenReturn(following);

        mockMvc.perform(get("/api/follows/following")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
    }
}
