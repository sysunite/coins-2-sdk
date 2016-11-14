FROM mono:latest

RUN apt-get update
RUN apt-get install wget zip -y -qq

RUN cd /tmp && wget -q https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip && \
    unzip ikvmbin-7.2.4630.5.zip && mv /tmp/ikvm-7.2.4630.5 /etc/ikvm
RUN echo "#!/usr/bin/env bash\nmono /etc/ikvm/bin/ikvmc.exe \"\$@\"" > /usr/bin/ikvmc
RUN chmod 777 /usr/bin/ikvmc

WORKDIR /opt
CMD VERSION=1.1.502 && \
    ikvmc -version:$VERSION -nologo -target:exe     "coins-cli/target/coins-cli-$VERSION-jar-with-dependencies.jar" -out:"dist/coins-cli-$VERSION.exe" && \
    ikvmc -version:$VERSION -nologo -target:library "coins-api/target/coins-api-$VERSION-jar-with-dependencies.jar" -out:"dist/coins-api-$VERSION.dll" && \
    sleep 1s
