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
import java.util.UUID;

@Service
public class FileSearvice {

    @Autowired
    IndexContentRepository indexContentRepository;


    @Value("${file.path}")
    public String filePath;


    public long index() {
        long count=0;
        File rootFilePath = new File(filePath);
        System.out.println("file package is：" + filePath);
        System.out.println("find files：" + Objects.requireNonNull(rootFilePath.listFiles()).length);

        for (File file : FileUtils.listFiles(rootFilePath, null, true)) {
            String id = Base64.getEncoder().encodeToString(file.getAbsolutePath().getBytes());
            if (indexContentRepository.findById(id).isPresent()) {
                System.out.println("deal file:" + file.getAbsolutePath() + "\t" + "this file had indexed");
                continue;
            } else {
                System.out.println("deal file:" + file.getAbsolutePath());
            }
            try {

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
                IndexContent indexContent = new IndexContent(id, title, UUID.randomUUID().toString(), file.getAbsolutePath(), space, content, type, date);
                indexContentRepository.index(indexContent);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return count;
    }
}
