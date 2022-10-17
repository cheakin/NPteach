rm -rf ./dmanywhere
mkdir dmanywhere
cd ./dmanywhere
wget http://img.dmanywhere.cn/download/dmanywhere.team.linux.0.8.4
chmod +x ./dmanywhere.team.linux.0.8.4
nohup ./dmanywhere.team.linux.0.8.4 -p 1111 &