## SDK地址

>[MinIO | Java Client API Reference(官方)](https://docs.min.io/docs/java-client-api-reference.html)
>
>[Java Client API参考文档 | Minio中文文档(中文)](http://docs.minio.org.cn/docs/master/java-client-api-reference#putObject)



## 安装(docker)

```shell
## 下载镜像
docker pull minio/minio

## 启动容器
docker run -d -p 9000:9000 -p 9001:9001  \
-e "MINIO_ACCESS_KEY=admin" \
-e "MINIO_SECRET_KEY=admin123456" \
--restart=always \
--name minio \
minio/minio server /data
```

