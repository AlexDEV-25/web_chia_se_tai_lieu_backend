package com.example.app.dto.response.statistic;

import java.time.LocalDate;

public record DailyCountResponse(LocalDate date, Long total) {
}
