package com.tianji.learning.task;

import com.tianji.common.utils.UserContext;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LessonTask {
    private final ILearningLessonService lessonService;

    @Scheduled(cron = "0 0 * * * *")
    public void handleExpiredLesson(){
        log.info("检查课程是否过期并处理，userId：{}", UserContext.getUser());
        lessonService.handleExpiredLesson();
    }
}
