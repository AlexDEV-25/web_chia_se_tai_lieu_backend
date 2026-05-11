package com.example.app.event;

import com.example.app.model.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentDeleteEvent {
	Document document;
}
