package com.zzc.upload.bus.controller;

import com.zzc.upload.bus.pojo.bean.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zzc
 */
@RequestMapping(value = "upload")
@RestController
public class UploadController {

    private static final String FOLDER_PATH = "/home/test";

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file, String path) {
        //获取文件名
        String fileName = file.getOriginalFilename();
        //fileName = getFileName(fileName);
        //String filepath = getUploadPath();//获取当前系统路径
        String filepath = FOLDER_PATH;
        if (StringUtils.isNotBlank(path)) {
            filepath = path;
        }
        if (!file.isEmpty()) {
            try (BufferedOutputStream out = new BufferedOutputStream
                    (new FileOutputStream(new File(filepath + File.separator + fileName)))) {
                out.write(file.getBytes());
                out.flush();
                System.out.println("文件上传成功，文件名：" + fileName);
//                //上传到指定目录
//                return new Result(ResultCode.SUCCESS, filepath + fileName);
                //上传到获取的当前系统路径
                try {
                    //获取当前主机+ip地址
                    InetAddress ip4 = Inet4Address.getLocalHost();
                    //获取ip
                    String hostAddress = ip4.getHostAddress();
                    //返回一个可访问的路径.9088是当前服务端口号
                    return Result.success();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return Result.error();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                System.out.println("上传文件失败 Exception：" + LINE_SEPARATOR + sw);
                return Result.error(sw.toString());
            }
        } else {
            System.out.println("上传文件失败，文件为空");
            return Result.error();
        }
    }

    @GetMapping("/runSh")
    public String runSh(@RequestParam(required = false) String shell) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(shell, "myArg1", "myArg2");
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        StringBuilder r = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            r.append(line).append(LINE_SEPARATOR);
        }
        return r.toString();
    }

    /**
     * 下载
     *
     * @param response
     * @param name
     * @param path
     * @author zzc
     */
    @GetMapping("/download")
    public void getImage(HttpServletResponse response, @RequestParam String name, @RequestParam String path) throws IOException {
        //动态获取图片存放位置
        //获取当前系统路径
        //        String path = getUploadPath();
        String imagePath = path + File.separator + name;
        //String imagePath = name;
        if (!new File(imagePath).exists()) {
            return;
        }
        //图片
        //response.setContentType("image/jpeg;charset=utf-8");
        //文件
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(name, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Files.readAllBytes(Paths.get(path).resolve(name)));
        outputStream.flush();
        outputStream.close();
    }

    @GetMapping("/getAllFile")
    public List<File> getAllFile(@RequestParam String dirPath) {
        List<File> allFileList = new ArrayList<>();
        File[] fileList = new File(dirPath).listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                getAllFile(file.getPath());
            } else {
                allFileList.add(file);
            }
        }
        //System.out.println(allFileList);
        return allFileList;
    }

    @GetMapping("/getFile")
    public List<File> getFile(@RequestParam String dirPath) {
        File[] fileList = new File(dirPath).listFiles();
        assert fileList != null;
        List<File> allFileList = new ArrayList<>(Arrays.asList(fileList));
        //System.out.println(allFileList);
        return allFileList;
    }


    /**
     * 文件名后缀前添加一个时间戳
     */
    private String getFileName(String fileName) {
        int index = fileName.lastIndexOf(".");
        //设置时间格式
        final SimpleDateFormat sDateFormate = new SimpleDateFormat("yyyyMMddHHmmss");
        // 当前时间
        String nowTimeStr = sDateFormate.format(new Date());
        fileName = fileName.substring(0, index) + "_" + nowTimeStr + fileName.substring(index);
        return fileName;
    }


    /**
     * 获取当前系统路径
     */
    private String getUploadPath() {
        File path = null;
        try {
            //默认是target/classes/
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            path = new File("");
        }
        File upload = new File(path.getAbsolutePath(), "static/upload/");
        if (!upload.exists()) {
            upload.mkdirs();
        }
        return upload.getAbsolutePath();
    }

    /**
     * 删除
     *
     * @param name 文件名
     * @param path 路径
     * @return Result
     * @author zzc
     */
    @GetMapping("/delete")
    public Result<Object> delete(@RequestParam String name, @RequestParam String path){
        //动态获取图片存放位置
        //获取当前系统路径
        //        String path = getUploadPath();
        String filePath = path + File.separator + name;
        //String imagePath = name;
        File file = new File(filePath);
        if (!file.exists()) {
            return Result.error("无此文件");
        }
        boolean delete = file.delete();
        if (delete) {
            return Result.success();
        } else {
            return Result.error();
        }

    }

    /**
     * 设置权限
     *
     * @param path 路径
     * @return Result
     * @author zzc
     */
    @GetMapping("/permission")
    public Result<Object> permission(@RequestParam String path){
        File file = new File(path);
        //设置可执行权限
        boolean exec= file.setExecutable(true,false);
        //设置可读权限
        boolean read= file.setReadable(true,false);
        //设置可写权限
        boolean write= file.setWritable(true,false);
        return Result.success("exec:" + exec + ",read:" + read + ",write:" + write);
    }

    @GetMapping("/")
    public Result<String> hello() {
    return Result.success("hello");
    }
}