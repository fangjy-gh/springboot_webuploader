package controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;
import java.util.UUID;

/**
 * @Auther: fangjy
 * @Date: 2019/8/7 16:04
 * @Description:文件下载
 */
@Controller
@RequestMapping("/down")
public class DownloadFileController {

    @Value("${linux_file_path}")
    private String linuxPath;
    @Value("${window_file_path}")
    private String windowPath;
    @Value("${file_service_url}")
    private String fileServiceUrl;

    @CrossOrigin
    @RequestMapping("/download.do")
    public String download(String fileName , String filePath, HttpServletRequest request, HttpServletResponse response){
        try {
            //解决post请求乱码
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //定义缓冲数据流读写文件
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        //获取文件根目录
        //指定上传文件路径
        String realPath;
        if(isOSLinux()){
            realPath = linuxPath;
        }else{
            realPath = windowPath;
        }
        String downLoadPath = realPath+filePath.replaceFirst(fileServiceUrl, "");  //注意不同系统的分隔符
        //	String downLoadPath =filePath.replaceAll("/", "\\\\\\\\");   //replace replaceAll区别 *****
        System.out.println(downLoadPath);
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        try {
            long fileLength = new File(downLoadPath).length();
            //自动识别下载文件类型
            response.setContentType("application/x-msdownload;");
            //设置响应头，文件名
            response.setHeader("Content-disposition", "attachment; filename=" + UUID.randomUUID().toString().replace("-","")+fileSuffix);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;
    }


    /**
     * 判断操作系统
     * @return
     */
    public static boolean isOSLinux(){
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        if(os!=null&&os.toLowerCase().indexOf("linux")>-1){
            return true;
        }else{
            return false;
        }
    }

}
