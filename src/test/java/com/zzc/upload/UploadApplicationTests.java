package com.zzc.upload;

import com.zzc.upload.bus.controller.UploadController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UploadApplicationTests {

    @Resource
    UploadController uploadController;

    @Test
    void contextLoads() {
        //System.out.println(uploadController.getFile("C:\\Users\\Administrator\\Desktop\\巡控"));
        System.out.println(uploadController.getAllFile("C:\\Users\\Administrator\\Desktop"));
    }

}
