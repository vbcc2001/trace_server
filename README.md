## 直接运行
```bash
# 运行素士版本
mvn spring-boot:run -Dport=8082
# 运行小米版本
mvn spring-boot:run -Pxiaomi -Dport=8081
```

## 打包为可执行Jar

```bash
#打包素士版本:
mvn clean package
# 打包小米版本
mvn clean package -Pxiaomi
```
