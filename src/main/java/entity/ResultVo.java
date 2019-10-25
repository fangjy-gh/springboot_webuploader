package entity;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/11
 * 描述
 */
public class ResultVo {
    private String code;
    private String msg;
    private String filePath;//文件绝对路径
    private String fileName;//文件名
    private String fileSize;//文件大小
    private String fileRequestUrl;//文件访问地址
    private Integer fileTime;//视频文件时长

    public String getFileRequestUrl() {
        return fileRequestUrl;
    }

    public void setFileRequestUrl(String fileRequestUrl) {
        this.fileRequestUrl = fileRequestUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getFileTime() {
        return fileTime;
    }

    public void setFileTime(Integer fileTime) {
        this.fileTime = fileTime;
    }

    public ResultVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ResultVo(){};
}
