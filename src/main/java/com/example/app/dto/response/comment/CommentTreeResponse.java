package com.example.app.dto.response.comment;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentTreeResponse extends CommentResponse {
	@Default
	private List<CommentTreeResponse> children = new ArrayList<CommentTreeResponse>();
}
