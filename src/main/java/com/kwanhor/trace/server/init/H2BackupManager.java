package com.kwanhor.trace.server.init;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Script;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Slf4j
public class H2BackupManager {

    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Scheduled(cron = "0 0 3 * * ?") //每天凌晨3点执行一次
    public void backup() throws SQLException {
        if(DictionaryManager.getInstance()!=null){
            var dbBackPath = DictionaryManager.getInstance().getDicValue("system","dbBackPath");
            if(dbBackPath == null || dbBackPath.trim().length() == 0){
                log.info("------------------ 系统没有配置H2数据库备份路径，使用默认路径----------------");
                dbBackPath = "./backup";
            }
            log.info("------------------H2备份定时任务开始----------------");
            File filePath= new File(dbBackPath);
            if (!filePath.exists()) {
                var result  = filePath.mkdirs();
                log.info("备份路径"+dbBackPath+"不存在，进行创建，结果 = "+ result);
            }
            log.info("任务执行时间：" + LocalDateTime.now());
            var today = LocalDate.now();
            var timeString = DateTimeFormatter.ofPattern("yyyyMMdd").format(today);
            String fileString = dbBackPath + "/db_back_" +timeString+ ".zip";
            log.info("file：" + fileString);
            Script.process(url,username,password,fileString,"","compression zip");
            // 在程序在执行的时候，Backup不能备份数据库
            // Backup.execute(file, ".././db/", "base", false);
            log.info("备份文件"+fileString+"完成");
            var timeStringOld = DateTimeFormatter.ofPattern("yyyyMMdd").format(today.plusDays(-30));
            String fileStringOld = dbBackPath + "/db_back_" +timeStringOld+ ".zip";
            var deleteResult= new File(fileStringOld).delete();
            log.info("删除30天前备份文件"+fileStringOld+"，结果 = "+ deleteResult);
            log.info("------------------H2备份定时任务结束----------------");

        }else{
            log.info("------------------DictionaryManager 暂未初始化----------------");
        }
    }
}
