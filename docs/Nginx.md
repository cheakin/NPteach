## 解决跨域问题

### 解决http(s)跨域

在转发片段中添加`add_header Access-Control-Allow-Origin *;`，如：

```shell
location ~* ^(\/cache\/files.*)(\/.*) {
  # 解決跨域http(s)
  add_header Access-Control-Allow-Origin *;
}
```

### 解决WS(WebSocket)跨域

在转发片段中添加如下片段：

```shell
location /office/{
	add_header 'Access-Control-Allow-Origin' '*';
	add_header 'Access-Control-Allow-Credentials' 'true';
	proxy_pass http://10.10.10.25:8010/;
	
	# 解决WS跨域
	proxy_http_version 1.1;
	proxy_set_header Upgrade $http_upgrade;
	proxy_set_header Connection "upgrade";
}
```

