package com.jd.xn.clinet.utils.files;

import com.jd.xn.clinet.Constants;

import java.io.*;

/**
 * 文件包装类，支持本地文件、字节数组和输入流三种方式。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/23 10:58
 */
public class FileItem {
    private Contract contract;

    /**
     * 基于本地文件的构造器，适用于上传本地文件。
     *
     * @param file 本地文件
     */
    public FileItem(final File file) {
        this.contract = new LocalContract(file);
    }

    /**
     * 基于文件绝对路径的构造器，适用于上传本地文件。
     *
     * @param filePath 文件绝对路径
     */
    public FileItem(String filePath) {
        this(new File(filePath));
    }

    /**
     * 基于文件名和字节数组的构造器。
     *
     * @param fileName 文件名
     * @param content  文件字节数组
     */
    public FileItem(String fileName, byte[] content) {
        this(fileName, content, null);
    }

    /**
     * 基于文件名、字节数组和媒体类型的构造器。
     *
     * @param fileName 文件名
     * @param content  文件字节数组
     * @param mimeType 媒体类型，如：image/jpeg, text/plain
     */
    public FileItem(String fileName, byte[] content, String mimeType) {
        this.contract = new ByteArrayContract(fileName, content, mimeType);
    }

    /**
     * 基于文件名和字节流的构造器，适应于全流式上传，减少本地内存开销。
     *
     * @param fileName 文件名
     * @param stream   文件字节流
     */
    public FileItem(String fileName, InputStream stream) {
        this(fileName, stream, null);
    }

    /**
     * 基于文件名、字节流和媒体类型的构造器，适应于全流式上传，减少本地内存开销。
     *
     * @param fileName 文件名
     * @param stream   文件字节流
     * @param mimeType 媒体类型，如：image/jpeg, text/plain
     */
    public FileItem(String fileName, InputStream stream, String mimeType) {
        this.contract = new StreamContract(fileName, stream, mimeType);
    }

    public boolean isValid() {
        return this.contract.isValid();
    }

    public String getFileName() {
        return this.contract.getFileName();
    }

    public String getMimeType() throws IOException {
        return this.contract.getMimeType();
    }

    public long getFileLength() {
        return this.contract.getFileLength();
    }

    public void write(OutputStream output) throws IOException {
        this.contract.write(output);
    }

    private static abstract interface Contract {
        public abstract boolean isValid();

        public abstract String getFileName();

        public abstract String getMimeType();

        public abstract long getFileLength();

        public abstract void write(OutputStream paramOutputStream)
                throws IOException;
    }

    /**
     * 本地文件
     */
    private static class LocalContract implements Contract {
        private File file;

        public LocalContract(File file) {
            this.file = file;
        }

        @Override
        public boolean isValid() {
            return this.file != null && this.file.exists() && this.file.isFile();
        }

        @Override
        public String getFileName() {
            return this.file.getName();
        }

        @Override
        public String getMimeType() {
            return Constants.MIME_TYPE_DEFAULT;
        }

        @Override
        public long getFileLength() {
            return this.file.length();
        }

        @Override
        public void write(OutputStream output) throws IOException {
            InputStream input = null;
            try {
                input = new FileInputStream(this.file);
                byte[] buffer = new byte[Constants.READ_BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }

    }

    /**
     * 字节数组
     */
    private static class ByteArrayContract implements Contract {
        private String fileName;
        private byte[] content;
        private String mimeType;

        public ByteArrayContract(String fileName, byte[] content, String mimeType) {
            this.fileName = fileName;
            this.content = content;
            this.mimeType = mimeType;
        }

        @Override
        public boolean isValid() {
            return this.content != null && this.fileName != null;
        }

        @Override
        public String getFileName() {
            return this.fileName;
        }

        @Override
        public String getMimeType() {
            //如果没有设置默认的请求文件流方式
            if (this.mimeType == null) {
                return Constants.MIME_TYPE_DEFAULT;
            } else {
                return this.mimeType;
            }
        }

        @Override
        public long getFileLength() {
            return this.content.length;
        }

        @Override
        public void write(OutputStream output) throws IOException {
            output.write(this.content);
        }
    }

    /**
     * 输入流
     */
    private static class StreamContract implements Contract {
        private String fileName;
        private InputStream stream;
        private String mimeType;

        public StreamContract(String fileName, InputStream stream, String mimeType) {
            this.fileName = fileName;
            this.stream = stream;
            this.mimeType = mimeType;
        }

        @Override
        public boolean isValid() {
            return this.stream != null && this.fileName != null;
        }

        @Override
        public String getFileName() {
            return this.fileName;
        }

        @Override
        public String getMimeType() {
            if (this.mimeType == null) {
                return Constants.MIME_TYPE_DEFAULT;
            } else {
                return this.mimeType;
            }
        }

        @Override
        public long getFileLength() {
            return 0L;
        }

        @Override
        public void write(OutputStream output) throws IOException {
            try {
                //创建固定大小的缓存
                byte[] buffer = new byte[Constants.READ_BUFFER_SIZE];
                int n = 0;
                //读取流文件
                while (-1 != (n = stream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }

}
