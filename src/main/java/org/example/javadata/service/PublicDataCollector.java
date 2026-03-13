package org.example.javadata.service;

/**
 * 모든 공공데이터 수집기가 구현해야 하는 인터페이스
 *
 * 핵심 아이디어:
 * - 어떤 API든 같은 방식으로 처리 가능
 * - WeatherCollector, MovieCollector, ... 모두 이 인터페이스 구현
 */
public interface PublicDataCollector {

    /**
     * 공공데이터를 수집한다
     * @return 수집된 데이터 개수
     */
    int collect();

    /**
     * 수집기의 이름 (로깅/식별용)
     * @return 수집기 이름 (예: "WeatherCollector")
     */
    String getCollectorName();
}
