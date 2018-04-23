package com.jd.xn.clinet;

import com.jd.xn.clinet.utils.files.FileItem;

import java.util.Map;

/**
 * @author lijizhen1@jd.com
 * @date 2018/4/23 10:57
 */
public abstract interface JdUploadRequest<T extends TFResponse> extends TFRequest<T> {
    /**
     * 文件数据
     *
     * @return
     */
    public abstract Map<String, FileItem> getFileParams();
}
