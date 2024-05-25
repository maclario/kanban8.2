package util;

import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TimeCalculator {

    public static Duration findDurationSum(List<Integer> idList, Map<Integer, Subtask> subs) {
        return idList.stream()
                .map(subs::get)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(null);
    }

    public static LocalDateTime findEarliestStartTime(List<Integer> idList, Map<Integer, Subtask> subs) {
        return idList.stream()
                .map(subs::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public static LocalDateTime findLatestEndTime(List<Integer> idList, Map<Integer, Subtask> subs) {
        return  idList.stream()
                .map(subs::get)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

}
