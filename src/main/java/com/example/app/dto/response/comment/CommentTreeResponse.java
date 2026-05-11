package com.example.app.dto.response.comment;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentTreeResponse extends CommentResponse {
	private List<CommentTreeResponse> children = new ArrayList<CommentTreeResponse>();
}
