package com.example.app.event;

import com.example.app.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFollowCreateEvent {
	private final User follower;
	private final User following;
}
