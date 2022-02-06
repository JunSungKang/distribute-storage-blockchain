package com.jskang.storageclient.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jskang.storageclient.common.Converter;
import com.jskang.storageclient.common.RequestApi;
import com.jskang.storageclient.node.NodeStatusDaos;
import com.jskang.storageclient.response.ResponseData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeInfo {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private NodeStatusDaos nodeList;
    private RequestApi requestApi = new RequestApi();

    public void load() {
        LOG.info("Node list loading ...");
        // data 디렉토리가 없는 경우 디렉토리 생성
        File dataDirectory = Paths.get("data").toFile();
        dataDirectory.mkdirs();

        File file = Paths.get("data", "nodes.json").toFile();
        if (!file.exists()) {
            // 파일이 없을 때
            try {
                ResponseData result = requestApi.get("127.0.0.1:20040/node/list");
                if (result == null || result.getHeader().getCode() != 200) {
                    throw new IllegalStateException("Node list load fail.");
                }

                String json = Converter.objToJson(result.getBody());
                nodeList = (NodeStatusDaos) Converter
                    .jsonToObj(json, new TypeReference<NodeStatusDaos>() {
                    });

                FileOutputStream out = new FileOutputStream(file);
                out.write(json.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } else {
            // 파일이 있을 때
            try {
                FileInputStream in = new FileInputStream(file);
                byte[] jsonByte = in.readAllBytes();
                String json = new String(jsonByte, StandardCharsets.UTF_8);
                nodeList = (NodeStatusDaos) Converter
                    .jsonToObj(json, new TypeReference<NodeStatusDaos>() {
                    });
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        LOG.info("Node list success.");
    }

    public NodeStatusDaos getNodeList() {
        return this.nodeList;
    }

    @JsonRootName("node")
    class Node {

        @JsonProperty("hostName")
        private String hostName;
        @JsonProperty("port")
        private String port;

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String connectHostName() {
            return this.hostName + ":" + this.port;
        }
    }
}
