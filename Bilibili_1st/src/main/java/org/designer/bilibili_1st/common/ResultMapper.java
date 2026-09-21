package org.designer.bilibili_1st.common;

import java.util.Map;

public final class ResultMapper {
    private ResultMapper() {}

    public static Result<Map<String, Object>> from(Map<String, Object> map) {
        if (map == null) { return Result.fail("操作失败"); }
        Object successFlag = map.get("success");
        boolean success = successFlag instanceof Boolean b ? b : true;
        Object message = map.getOrDefault("message", success ? "操作成功" : "操作失败");
        if (success) { return Result.success(String.valueOf(message), map); }
        return Result.fail(String.valueOf(message));
    }

    public static <T> Result<T> ok(T data) { return Result.success(data); }
}

