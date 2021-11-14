package com.jskang.storageclient.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("response")
public class ResponseData {

    @JsonProperty("header")
    private Header header;
    @JsonProperty("body")
    private Object body;

    private ResponseData(
        @JsonProperty("header") Header header,
        @JsonProperty("body") Object body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
            "header=" + header +
            ", body=" + body +
            '}';
    }
}
