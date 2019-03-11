package com.es.esindexfiledemo.init;

import com.es.esindexfiledemo.service.FileSearvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IndexRunner implements CommandLineRunner {

    @Autowired
    FileSearvice fileSearvice;

    @Override
    public void run(String... args) throws Exception {
        fileSearvice.index();
        System.out.println("索引文件结束");
    }
}