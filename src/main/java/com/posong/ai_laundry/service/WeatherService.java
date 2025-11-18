package com.posong.ai_laundry.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posong.ai_laundry.dto.WeatherAdvice;
import com.posong.ai_laundry.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    // ✅ 단기예보 조회 서비스 URL (48시간 예보 제공)
    private static final String BASE_URL =
            "https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst";

    public WeatherResponse getWeather(double nx, double ny) throws Exception {
        // ✅ 현재 날짜 및 발표 기준 시각
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = getBaseTime();

        // ✅ API URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", (int) nx)
                .queryParam("ny", (int) ny)
                .queryParam("authKey", serviceKey)
                .toUriString();

        // ✅ API 호출
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        // ✅ JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode items = mapper.readTree(response)
                .path("response").path("body").path("items").path("item");

        // ✅ 48시간 예보 데이터 리스트
        List<Integer> pops = new ArrayList<>();   // 강수확률
        List<Integer> rehs = new ArrayList<>();   // 습도
        List<Double> tmps = new ArrayList<>();    // 기온
        List<Double> wsds = new ArrayList<>();    // 풍속

        for (JsonNode node : items) {
            String category = node.path("category").asText();
            double value = node.path("fcstValue").asDouble();

            switch (category) {
                case "POP": pops.add((int) value); break;
                case "REH": rehs.add((int) value); break;
                case "TMP": tmps.add(value); break;
                case "WSD": wsds.add(value); break;
            }
        }

        // ✅ 12시간 내 평균 및 48시간 내 최대값 계산
        int pop12 = averageInt(pops.subList(0, Math.min(4, pops.size()))); // 12시간(3h×4)
        int maxPop12to48 = pops.stream().skip(4).mapToInt(Integer::intValue).max().orElse(0);
        int reh = averageInt(rehs);
        double tmp = averageDouble(tmps);
        double wsd = averageDouble(wsds);

        WeatherAdvice advice = generateForecastAdvice(pop12, maxPop12to48, reh, tmp, wsd);

        // ✅ 결과 반환
        return new WeatherResponse(tmp, reh, pop12, 0, 0, wsd, advice);
    }

    // ✅ 발표 기준 시간 계산 (단기예보 발표 시각 기준)
    private String getBaseTime() {
        int hour = LocalTime.now().getHour();
        if (hour < 2) return "2300";
        else if (hour < 5) return "0200";
        else if (hour < 8) return "0500";
        else if (hour < 11) return "0800";
        else if (hour < 14) return "1100";
        else if (hour < 17) return "1400";
        else if (hour < 20) return "1700";
        else if (hour < 23) return "2000";
        else return "2300";
    }

    // ✅ 평균 계산 (정수용)
    private int averageInt(List<Integer> list) {
        return list.isEmpty() ? 0 :
                (int) list.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    // ✅ 평균 계산 (실수용)
    private double averageDouble(List<Double> list) {
        return list.isEmpty() ? 0 :
                list.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    // ✅ 세탁/건조 추천 로직 (단기예보 48시간 기반)
    private WeatherAdvice generateForecastAdvice(int pop12, int maxPop12to48, int reh, double tmp, double wsd) {
        String summary;
        List<String> details = new ArrayList<>();

        if (pop12 >= 50) {
            summary = "강수확률이 높아요 ☔ 세탁은 비추천이에요.";
            details.add("급한 경우 건조기 사용을 권장드려요.");
        } else if (pop12 >= 30) {
            summary = "강수확률이 " + pop12 + "%예요. 약한 비 가능성이 있어요.";
            details.add("그래도 세탁은 가능하지만, 실외건조는 주의하세요.");
        } else { // pop12 < 30%
            if (reh >= 60) {
                summary = "습도가 높아요 💦 냄새날 수 있으니 제습기나 건조기와 함께하세요.";
            } else {
                if (tmp >= 20) {
                    if (wsd < 3) summary = "빨래하기 딱 좋은 날이에요 🌤️ 실외건조 추천!";
                    else summary = "바람이 강해요 🌬️ 실외건조는 피하세요.";
                } else if (tmp >= 10) {
                    if (wsd < 3) summary = "세탁하기 괜찮은 날이에요 ☁️";
                    else summary = "바람이 다소 강하네요 🌬️ 실외건조는 피하세요.";
                } else {
                    summary = "기온이 낮아 건조가 어려워요 🧊 건조기 권장합니다.";
                }
            }

            // 내일(12~48시간 이내)에 비 예보가 있으면 경고 추가
            if (maxPop12to48 >= 30) {
                details.add("내일 비 예보가 있어요 ☔ 빨래는 오늘 안에 걷으세요.");
            }
        }

        return new WeatherAdvice(summary, details);
    }
}
