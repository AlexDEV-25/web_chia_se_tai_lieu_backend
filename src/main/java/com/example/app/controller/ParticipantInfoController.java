package com.example.app.controller;

import com.example.app.dto.request.ParticipantInfoRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;
import com.example.app.service.ParticipantInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participant-infos")
@AllArgsConstructor
public class ParticipantInfoController {
    private ParticipantInfoService participantInfoService;

    @PutMapping("/update-last-seen/{id}")
    public APIResponse<ParticipantInfoResponse> updateLastSeen(@PathVariable Long id) {
        APIResponse<ParticipantInfoResponse> apiResponse = new APIResponse<ParticipantInfoResponse>();
        apiResponse.setResult(participantInfoService.updateLastSeen(id));
        return apiResponse;
    }

    @PostMapping("/add-member")
    public APIResponse<ParticipantInfoResponse> addMember(@RequestBody @Valid ParticipantInfoRequest dto) {
        APIResponse<ParticipantInfoResponse> apiResponse = new APIResponse<ParticipantInfoResponse>();
        apiResponse.setResult(participantInfoService.addMember(dto));
        return apiResponse;
    }

    @DeleteMapping("/delete-member/user-id/{userId}/conversation-id/{conversationId}")
    public APIResponse<Void> deleteMember(@PathVariable Long userId, @PathVariable Long conversationId) {
        participantInfoService.deleteMember(userId, conversationId);
        return APIResponse.<Void>builder().build();
    }

    @PutMapping("/change-role")
    public APIResponse<ParticipantInfoResponse> changeRole(@RequestBody @Valid ParticipantInfoRequest dto) {
        APIResponse<ParticipantInfoResponse> apiResponse = new APIResponse<ParticipantInfoResponse>();
        apiResponse.setResult(participantInfoService.changeRole(dto));
        return apiResponse;
    }
}
