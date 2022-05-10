package com.dux.push;

public interface IPush {
    void init();
    void init(String appId, String appKey);
    String setAlias(String alias);
    String unsetAlias(String alias);
    String setTag(String tag);
    String unsetTag(String tag);
}
