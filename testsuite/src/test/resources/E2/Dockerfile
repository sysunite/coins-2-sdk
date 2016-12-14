FROM mono:latest

RUN apt-get update
RUN apt-get install wget unzip -y -qq --no-install-recommends

# install javac
RUN apt-get install openjdk-7-jdk -y -qq --no-install-recommends

# install ikvmc
RUN cd /tmp && wget -q https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip && \
    unzip ikvmbin-7.2.4630.5.zip && mv /tmp/ikvm-7.2.4630.5 /etc/ikvm
RUN echo "#!/usr/bin/env bash\nmono /etc/ikvm/bin/ikvmc.exe \"\$@\"" > /usr/bin/ikvmc
RUN chmod 777 /usr/bin/ikvmc

WORKDIR /opt
CMD \
cd test && \
mono ../project/coins-cli-$COINSVERSION.exe generate -dll -a ../project/coins-api-$COINSVERSION.dll -i cbim-2.0.rdf -o cbim && \
sleep 1s
