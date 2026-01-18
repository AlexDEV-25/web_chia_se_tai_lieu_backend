package com.example.app.dto.response;

import java.time.LocalDate;

public record DailyCountResponse(LocalDate date, Long total) {
}
