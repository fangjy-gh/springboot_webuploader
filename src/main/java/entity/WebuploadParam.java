package entity;

import java.io.Serializable;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/12
 * 描述
 */
public class WebuploadParam implements Serializable {
    //当前分片
    private String chunk;
    //总分片数
    private String chunks;
    //总分片大小
    private String size;
    //单个分片大小
    private String chunkSize;
    //文件分类目录
    private String category;
    //文件md5值
    private String fileMd5;
    //文件名
    private String fileName;
    //上传进度
    private String percent;

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public String getChunks() {
        return chunks;
    }

    public void setChunks(String chunks) {
        this.chunks = chunks;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(String chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "WebuploadParam{" +
                "chunk='" + chunk + '\'' +
                ", chunks='" + chunks + '\'' +
                ", size='" + size + '\'' +
                ", chunkSize='" + chunkSize + '\'' +
                ", category='" + category + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileName='" + fileName + '\'' +
                ", percent='" + percent + '\'' +
                '}';
    }
}
