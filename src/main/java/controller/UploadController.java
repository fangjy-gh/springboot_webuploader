package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.ResultVo;
import entity.WebuploadParam;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.util.*;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/11
 * 描述
 *
 */
@Controller
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${linux_file_path}")
    private String linuxPath;
    @Value("${window_file_path}")
    private String windowPath;

    @Value("${file_service_url}")
    private String fileServiceUrl;

    /**
     * 普通上传
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload.do")
    @ResponseBody
    @CrossOrigin
    public ResultVo upload(@RequestParam("file") MultipartFile multipartFile,String category){
        System.out.println("参数==="+category);
        ResultVo resultVo = new ResultVo("error","上传失败");
        if(multipartFile.isEmpty()){
            logger.info("上传失败，请选择文件");
            resultVo.setMsg("上传失败，请选择文件");
            return resultVo;
        }
        //获取文件名
        String filename = multipartFile.getOriginalFilename();
        System.out.println(filename);
        //截取后缀
        String fileNameSuffix = filename.substring(filename.lastIndexOf("."));
        System.out.println(fileNameSuffix);
        //生成唯一的文件名
        String newFileName = UUID.randomUUID().toString().replace("-","")+fileNameSuffix;
        System.out.println(newFileName);
        //指定上传文件路径
        String filePath;
        if(isOSLinux()){
            filePath = linuxPath;
        }else{
            filePath = windowPath;
        }
        //避免目录下文件过多
        LocalDate now = LocalDate.now();
        String timeDirec;
        if(category!=null&&category.trim().length()>0){
            timeDirec = category +"/"+now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        }else{
            timeDirec = now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        }
        File file = new File(filePath+"/"+timeDirec,newFileName);
        //如果目录不存在，则新建
        if(!file.exists()){
            file.mkdirs();
        }
        //上传
        try {
            multipartFile.transferTo(file);
            logger.info("上传成功");
            resultVo = new ResultVo("success","上传成功");
            resultVo.setFileName(filename);
            resultVo.setFilePath(file.getAbsolutePath());
            resultVo.setFileSize(multipartFile.getSize()+"");
            resultVo.setFileRequestUrl(fileServiceUrl+"/"+timeDirec+"/"+newFileName);
            return resultVo;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return resultVo;
    }

    /**
     * 合并文件
     * @param param 请求参数
     * @return 返回文件相关信息
     */
    @RequestMapping(value = "/hbFile.do",method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResultVo hbFile(WebuploadParam param){
        System.out.println("合并文件=="+param);
        //读取目录里的所有文件
        String filePath;
        if(isOSLinux()){
            filePath = linuxPath;
        }else{
            filePath = windowPath;
        }
        File file = new File(filePath+"/temp/"+param.getFileMd5());
        File[] fileArray = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //排除目录，只有文件
                if(pathname.isDirectory()){
                    return false;
                }
                return true;
            }
        });
        //转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.valueOf(o1.getName())<Integer.valueOf(o2.getName())){
                    return -1;
                }
                return 1;
            }
        });

        //避免目录下文件过多
        LocalDate now = LocalDate.now();
        String timeDirec;
        if(param.getCategory()!=null&&param.getCategory().trim().length()>0){
            timeDirec = param.getCategory() +"/"+now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        }else{
            timeDirec = now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        }
        String fileName = param.getFileName();
        //截取后缀
        String fileNameSuffix = fileName.substring(fileName.lastIndexOf("."));
        File srcFile = new File(filePath+"/"+timeDirec,param.getFileMd5()+fileNameSuffix);
        //如果目录不存在，则新建
        if(!srcFile.getParentFile().exists()){
            srcFile.getParentFile().mkdirs();
        }
        try {
            srcFile.createNewFile();
            //输出流
            FileChannel outChnnel = new FileOutputStream(srcFile).getChannel();
            //合并
            FileChannel inChnnel;
            for(File f:fileList){
                inChnnel = new FileInputStream(f).getChannel();
                inChnnel.transferTo(0,inChnnel.size(),outChnnel);
                inChnnel.close();
                //删除分片
                f.delete();
            }
            outChnnel.close();
            //删除临时文件夹
            if(file.isDirectory()&&file.exists()){
                file.delete();
            }
            System.out.println("合并成功。。");
            ResultVo resultVo = new ResultVo("success","文件上传成功");
            resultVo.setFileName(fileName);
            resultVo.setFilePath(srcFile.getAbsolutePath());
            resultVo.setFileSize(srcFile.length()+"");
            resultVo.setFileRequestUrl(fileServiceUrl+"/"+timeDirec+"/"+param.getFileMd5()+fileNameSuffix);
            if(".mp4".equals(fileNameSuffix)){
                resultVo.setFileTime(getFileTime(srcFile));
            }
            //对象转json字符串
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(resultVo);
            redisTemplate.opsForHash().put(param.getFileMd5(),"info",jsonString);
            return resultVo;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (EncoderException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件上传前检查是否已经上传过
     * @param fileMd5
     * @return
     */
    @RequestMapping(value = "/beforeCheck.do",method =RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResultVo beforeCheck(String fileMd5){
        if(redisTemplate.opsForHash().hasKey(fileMd5,"info")){
            String info = (String) redisTemplate.opsForHash().get(fileMd5, "info");
            ObjectMapper mapper = new ObjectMapper();
            try {
                ResultVo resultVo = mapper.readValue(info, ResultVo.class);
                return resultVo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * webuploader分片上传
     * @param multipartFile
     * @param param
     * @return
     */
    @RequestMapping("/webupload.do")
    @ResponseBody
    @CrossOrigin
    public void webupload(@RequestParam("file") MultipartFile multipartFile, WebuploadParam param){
        System.out.println("参数对象=="+param);
        String chunk;
        if(param.getChunk()==null){
            chunk="0";
        }else {
            chunk = param.getChunk();
        }
        //指定上传文件路径
        String filePath;
        if(isOSLinux()){
            filePath = linuxPath;
        }else{
            filePath = windowPath;
        }
        File file = new File(filePath+"/temp/"+param.getFileMd5(),chunk);
        //如果目录不存在，则新建
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            multipartFile.transferTo(file);
            //上传成功保存当前分片到redis
            redisTemplate.opsForHash().put(param.getFileMd5(),chunk,chunk);
            if(param.getChunk()!=null){
                redisTemplate.opsForHash().put(param.getFileMd5(),"percent",param.getPercent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * webupload文件上传前判断是否已上传
     * @param fileMd5
     * @param chunk
     * code的值：0-已上传完成，1-正在上传中，2-第一次上传
     */
    @RequestMapping(value = "/checkFile.do",method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public boolean checkFile(String fileMd5,String chunk){
        return redisTemplate.opsForHash().hasKey(fileMd5, chunk);
    }

    //获取文件时长
    public int getFileTime(File file) throws EncoderException {
        Encoder encoder = new Encoder();
        MultimediaInfo info = encoder.getInfo(file);
        long ls = info.getDuration();
        int second = (int) (ls/1000);
        System.out.println("此视频时长为:" + second + "秒！");
        return second;
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
