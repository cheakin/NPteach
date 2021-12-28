#!/bin/bash
filename="app"

# 备份
if [ -f $filename.jar ];then
	name=$(date +%Y%m%d%H%M%S)
	mv $filename.jar ./history/${filename}_${name}.jar
	echo $filename.jar已备份至history
fi

# 将jar包重命名
for f in `ls *.jar`; do 
    mv "$f" $filename.jar
done

# 启动
if [ ! -f $filename.jar ];then
	echo $filename.jar不存在
	exec /bin/bas
else
	docker-compose up --build -d
fi
