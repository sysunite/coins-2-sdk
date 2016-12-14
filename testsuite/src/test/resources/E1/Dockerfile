FROM mono:latest

RUN apt-get update
RUN apt-get install wget unzip -y -qq

WORKDIR /opt
CMD \
cd test && \
mono ../project/coins-cli-$COINSVERSION.exe viewer -i starterskit4.01.ccr && \
sleep 10000s
