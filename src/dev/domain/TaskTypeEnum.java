package dev.domain;

public enum TaskTypeEnum {


    TASK("Обычная задача", "1"),
    EPIC("Эпик-задача", "2"),
    SUBTASK("Подзадача", "3");

    public final String title;
    public final String key;

    TaskTypeEnum(String title, String key) {
        this.title = title;
        this.key = key;
    }
    public static TaskTypeEnum fromKey(String key) {
        for (TaskTypeEnum command : values()) {
            if (command.key.equals(key)) {
                return command;
            }
        }
        return TaskTypeEnum.TASK;
    }
}
