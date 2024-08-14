package com.ad.adrepository.controller;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 8. 13.
 */
import io.opentelemetry.context.propagation.TextMapGetter;

import java.util.Map;

public class MapTextMapGetter implements TextMapGetter<Map<String, String>> {

    @Override
    public Iterable<String> keys(Map<String, String> map) {
        return map.keySet();
    }

    @Override
    public String get(Map<String, String> map, String key) {
        return map.get(key);
    }
}
