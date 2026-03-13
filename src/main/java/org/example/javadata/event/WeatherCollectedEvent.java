package org.example.javadata.event;

import java.util.List;

public class WeatherCollectedEvent {

    private final List<Long> outboxIds;

    public WeatherCollectedEvent(List<Long> outboxIds) {
        this.outboxIds = outboxIds;
    }

    public List<Long> getOutboxIds() {
        return outboxIds;
    }
}
