package controller;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Properties;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/19
 * 描述
 */
@Controller
@RequestMapping("/pdf")
public class PdfConvertController {
    @Value("${linux_file_path}")
    private String linuxPath;
    @Value("${window_file_path}")
    private String windowPath;
    @Value("${file_service_url}")
    private String fileServiceUrl;
    /**
     * 转换pdf文档
     * @param filePath
     * @return
     */
    @RequestMapping("/convertPdf.do")
    @ResponseBody
    @CrossOrigin
    public String convertPdf(String filePath){
        String result="";
        try {
            //获取文件根目录
            String realPath;
            if(isOSLinux()){
                realPath = linuxPath;
            }else{
                realPath = windowPath;
            }
            //源文件路径
            String srcPath = realPath+filePath.replaceFirst(fileServiceUrl, "");
            //目标文件路径
            String descPath = srcPath.substring(0,srcPath.lastIndexOf("."))+".pdf";
            File file = new File(descPath);
            if(file.exists()){
                result = filePath.substring(0,filePath.lastIndexOf("."))+".pdf";
            }
            //文档转换
            int i = office2Pdf(srcPath, descPath);
            System.out.println("转换完成。。。"+i);
            if(i==0){
                result = filePath.substring(0,filePath.lastIndexOf("."))+".pdf";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
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

    /**
     * 支持doc、docx、word
     * @param srcPath  源文件 --绝对路径
     * @param desPath  输出文件
     * @return 返回1 转换失败，返回-1 源文件找不到，返回0 成功
     */
    public static int office2Pdf(String srcPath, String desPath){

        OpenOfficeConnection connection=null;
        try {
            File srcFile = new File(srcPath);
            if(!srcFile.exists()){
                return -1;//源文件不存在
            }
            //如果目标路径不存在，则新建该路径
            File desFile = new File(desPath);
            if(!desFile.getParentFile().exists()){
                desFile.getParentFile().mkdirs();
            }
            // 启动OpenOffice的服务
/*            String command = path
                    + " soffice -headless -accept=\"socket,host=0.0.0.0,port=8100;urp;\" -nofirststartwizard";
            Runtime.getRuntime().exec(command);//启动服务*/
            //建立连接
            connection = new SocketOpenOfficeConnection("192.168.168.130",8100);
            connection.connect();

            //转换操作
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
            converter.convert(srcFile,desFile);
            //关闭连接
            connection.disconnect();

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
        return 1;
    }

}
