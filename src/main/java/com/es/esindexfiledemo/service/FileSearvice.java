package com.es.esindexfiledemo.service;


import com.es.esindexfiledemo.entity.IndexContent;
import com.es.esindexfiledemo.repository.IndexContentRepository;
import com.es.esindexfiledemo.utils.ParseText;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
public class FileSearvice {

    @Autowired
    IndexContentRepository indexContentRepository;


    @Value("${file.path}")
    String filePath;


    public void index() {
        File rootFilePath = new File(filePath);
        System.out.println("文件夹是：" + filePath);
        System.out.println("发现文件个数：" + Objects.requireNonNull(rootFilePath.listFiles()).length);

        for (File file : Objects.requireNonNull(rootFilePath.listFiles())) {
            try {
                String id = Base64.getEncoder().encodeToString(file.getAbsolutePath().getBytes());
                String title = file.getName();
                String type = "";
                String content = "";
                long space = file.length();
                Date date = new Date(file.lastModified());

                if (file.getName().endsWith(".txt")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "txt");
                    type = "txt";
                }
                if (file.getName().endsWith(".pdf")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "pdf");
                    type = "pdf";
                }
                if (file.getName().endsWith(".doc")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "doc");
                    type = "doc";
                }
                if (file.getName().endsWith(".docx")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "docx");
                    type = "docx";
                }
                if (file.getName().endsWith(".ppt")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "ppt");
                    type = "ppt";
                }
                if (file.getName().endsWith(".pptx")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "pptx");
                    type = "pptx";
                }
                if (file.getName().endsWith(".xls")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "xls");
                    type = "xls";
                }
                if (file.getName().endsWith(".xlsx")) {
                    content = ParseText.parse(FileUtils.readFileToByteArray(file), "xlsx");
                    type = "xlsx";
                }
                IndexContent indexContent = new IndexContent(id, title, space, content, type, date);
                indexContentRepository.index(indexContent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
