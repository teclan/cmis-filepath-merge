# 文本文件合并工具

## 环境
- JDK8+
- Maven3+

## 依赖项目
- [teclan-ssh](https://github.com/teclan/teclan-ssh)

``` 

```

## 依赖安装
```xml
        <!-- https://github.com/teclan/teclan-ssh -->
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>teclan-ssh</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

```shell script
git clone git@github.com:teclan/teclan-ssh.git
cd teclan-ssh
mvn clean install -Dmaven.test.skip=true
```


## 打包
```
mvn clean package
```

## 运行
``` 
java -jar cmis-filepath-merge-1.0.jar
```

