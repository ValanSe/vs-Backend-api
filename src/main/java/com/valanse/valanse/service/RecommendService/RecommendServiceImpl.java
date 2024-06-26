package com.valanse.valanse.service.RecommendService;

import com.valanse.valanse.entity.FavoriteCategory;
import com.valanse.valanse.entity.RecommendQuiz;
import com.valanse.valanse.repository.jpa.FavoriteCategoryRepository;
import com.valanse.valanse.repository.jpa.RecommendQuizRepository;
import com.valanse.valanse.util.DataParseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final FavoriteCategoryRepository favoriteCategoryRepository;
    private final RecommendQuizRepository recommendQuizRepository;

    @Override
    public void updateFavoriteCategory(String data) {
        try {
            Map<String, String> dataMap = DataParseUtil.parseCommaSeparatedKeyValuePairs(data);
            Integer userId = Integer.valueOf(dataMap.get("userId"));
            String category = dataMap.get("category");

            FavoriteCategory newFavoriteCategory = FavoriteCategory.builder()
                    .userId(userId)
                    .category(category)
                    .build();

            favoriteCategoryRepository.save(newFavoriteCategory);
            log.info("Successfully updated favorite category for user_id: {}, category: {}", userId, category);
        } catch (NumberFormatException e) {
            log.error("Failed to parse userId from data: {}", data, e);
        } catch (Exception e) {
            log.error("An error occurred while updating favorite category with data: {}", data, e);
        }
    }

    @Override
    public void updateRecommendQuiz(String data) {
        try {
            Map<String, String> dataMap = new HashMap<>();

            // 콜론(:)을 기준으로 나누고, 키와 값을 분리
            String[] keyValuePairs = data.split(",(?=[a-zA-Z])"); // 알파벳 앞에서만 ,로 분리

            for (String pair : keyValuePairs) {
                log.info("Pair: {}", pair); // 각 keyValuePair 확인
                String[] keyValue = pair.split(":");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                dataMap.put(key, value);
            }

            // userId와 recommendQuizIds 추출
            Integer userId = Integer.valueOf(dataMap.get("userId"));
            String recommendQuizIds = dataMap.get("recommendedQuizIds");

            log.info("User ID: {}", userId);
            log.info("Recommended Quiz IDs: {}", recommendQuizIds);

            List<Integer> recommendQuizList = Optional.ofNullable(recommendQuizIds)
                    .filter(ids -> !ids.isEmpty())
                    .map(ids -> Arrays.stream(ids.split(","))
                            .map(Integer::valueOf)
                            .toList())
                    .orElse(new ArrayList<>());

            if (recommendQuizList.isEmpty()) {
                log.warn("recommendQuizIds is null or empty, no recommendations will be updated for user ID: {}", userId);
                // 추천 문제가 없는 경우의 처리 로직
                return;
            }

            List<RecommendQuiz> recommendQuizzes = recommendQuizList.stream()
                    .map(quizId -> RecommendQuiz.builder()
                            .userId(userId)
                            .quizId(quizId)
                            .build())
                    .toList();

            recommendQuizRepository.saveAll(recommendQuizzes);

        } catch (NumberFormatException e) {
            log.error("Error parsing integer values: ", e);
        } catch (Exception e) {
            log.error("Error occurred while updating recommended quizzes: ", e);
        }
    }
}
