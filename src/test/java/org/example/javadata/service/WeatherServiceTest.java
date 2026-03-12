package org.example.javadata.service;

import org.example.javadata.entity.WeatherHistoryEntity;
import org.example.javadata.entity.WeatherLatestEntity;
import org.example.javadata.io.WeatherInput;
import org.example.javadata.repository.WeatherHistoryRepository;
import org.example.javadata.repository.WeatherLatestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherInput weatherInput;

    @Mock
    private WeatherHistoryRepository historyRepository;

    @Mock
    private WeatherLatestRepository latestRepository;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @SuppressWarnings("unchecked")
    void fetchAndSave_skipsRowsWithoutCurrentTemp() throws Exception {
        when(weatherInput.callApi(1, 100)).thenReturn("""
                {
                  "body": [
                    {
                      "STDG_SGG_CD": "11110",
                      "SGG_NM": "Seoul",
                      "NOW_AIRTP": "",
                      "FRCST_CRTR_TM": "202603121900"
                    },
                    {
                      "STDG_SGG_CD": "22220",
                      "SGG_NM": "Busan",
                      "NOW_AIRTP": "12.3",
                      "FRCST_CRTR_TM": "202603121900"
                    }
                  ]
                }
                """);
        when(historyRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(latestRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        int savedCount = weatherService.fetchAndSave(1, 100);

        assertThat(savedCount).isEqualTo(1);

        ArgumentCaptor<List<WeatherHistoryEntity>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(historyRepository).saveAll(historyCaptor.capture());
        assertThat(historyCaptor.getValue()).hasSize(1);
        assertThat(historyCaptor.getValue().get(0).getRegionCode()).isEqualTo("22220");

        ArgumentCaptor<List<WeatherLatestEntity>> latestCaptor = ArgumentCaptor.forClass(List.class);
        verify(latestRepository).saveAll(latestCaptor.capture());
        assertThat(latestCaptor.getValue()).hasSize(1);
        assertThat(latestCaptor.getValue().get(0).getRegionCode()).isEqualTo("22220");

        verify(latestRepository).deleteByRegionCode("22220");
        verify(latestRepository, never()).deleteByRegionCode("11110");
    }
}
