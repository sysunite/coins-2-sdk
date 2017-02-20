FROM mono:latest

RUN apt-get update
RUN apt-get install wget unzip -y -qq

# install ikvmc
RUN cd /tmp && wget -q https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip && \
    unzip ikvmbin-7.2.4630.5.zip && mv /tmp/ikvm-7.2.4630.5 /etc/ikvm
RUN echo "#!/usr/bin/env bash\nmono /etc/ikvm/bin/ikvmc.exe \"\$@\"" > /usr/bin/ikvmc
RUN chmod 777 /usr/bin/ikvmc

ENV COINSVERSION 1.1.605

WORKDIR /opt
CMD \
ikvmc -version:$COINSVERSION -nologo -target:exe     "coins-cli/target/coins-cli-$COINSVERSION-jar-with-dependencies.jar" -out:"dist/coins-cli-$COINSVERSION.exe" && \
ikvmc -version:$COINSVERSION -nologo -target:library "coins-api/target/coins-api-$COINSVERSION-jar-with-dependencies.jar" -out:"dist/coins-api-$COINSVERSION.dll" && \
sleep 1s
