package cn.aishu.exporter.common.output;

import java.util.ArrayList;
import java.util.List;

class SerializerContainer {
    private int strLength = 0;
    private boolean isGetNull;  //标记此次是否获取到数据
    private List<String> list = new ArrayList<>(HttpSender.STR_LIST_SIZE_LIMIT);
    private String contentStr = null;

    public boolean putAndShouldSend(Serializer serializer) {
        putContentStrIntoList();
        if (serializer != null) {
            isGetNull = false;
            contentStr = serializer.toJson();
            strLength += contentStr.length();
            if (strLength >= HttpSender.STR_LENGTH_LIMIT) {
                // 字符总长度超过了限制，先发送这批数据
                return true;
            } else {
                putContentStrIntoList();
                // list总条数达到了预设值，先发送这批数据
                return list.size() == HttpSender.STR_LIST_SIZE_LIMIT;
            }
        } else {
            isGetNull = true;
            return false;
        }
    }

    public List<String> getList() {
        strLength = 0;
        return list;
    }

    private void putContentStrIntoList() {
        if (contentStr != null) {
            list.add(contentStr);
            contentStr = null;
        }
    }

    public boolean isGetNull() {
        return isGetNull;
    }
}

