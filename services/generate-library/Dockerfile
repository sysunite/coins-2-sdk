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

ENV COINSVERSION 1.1.605

# install coins-cli
RUN echo "#!/usr/bin/env bash\njava -jar \"/opt/coins-cli/target/coins-cli-\$COINSVERSION-jar-with-dependencies.jar\" \"\$@\"" > /usr/bin/coins-cli
RUN chmod 777 /usr/bin/coins-cli

WORKDIR /opt
CMD cd /opt/services/generate-library/libraries && \
#coins-cli generate -a "/opt/dist/coins-api-$COINSVERSION.dll" -dll -i SE.rdf -o se
coins-cli generate -a "/opt/dist/coins-api-$COINSVERSION.dll" -dll -i cbim-2.0.rdf -o cbim
