package com.jskang.storageclient.handler;

import com.jskang.storageclient.common.Converter;
import java.util.HashMap;
import java.util.Map;

public class HandlerUtils {

    Map<String, Object> getBodyParser(byte[] bytes) {
        return Converter.jsonToMap(new String(bytes));
    }

    Map<String, Object> getQueryParser(String query) {
        if (query == null) {
            return new HashMap<>();
        }

        Map<String, Object> paramMap = new HashMap<>();

        String[] params = query.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length < 2) {
                continue;
            }

            paramMap.put(param[0], param[1]);
        }

        return paramMap;
    }
}
