FROM elasticsearch:7.17.3

# 安装 gosu
RUN apt-get update && apt-get install -y gosu && rm -rf /var/lib/apt/lists/* && elasticsearch-plugin install --batch https://get.infini.cloud/elasticsearch/analysis-ik/7.17.3
