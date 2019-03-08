FROM ubuntu:latest
LABEL build="151903011310" maintainer="Songhe Zhang" java.version="jdk1.8.0_151" glassfish.version="5.0"

COPY dpkg-excludes /etc/dpkg/dpkg.cfg.d/excludes
COPY start.sh /

RUN echo 'debconf debconf/frontend select Noninteractive' | debconf-set-selections && \
    apt-get update && \
    apt-get -y dist-upgrade && \
    apt-get -y install locales wget unzip && \
    locale-gen en_US.UTF-8 && \
    locale-gen de_CH.UTF-8 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
    
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8' TZ='Europe/Zurich'

ADD jdk1.8.0_151.tar.gz /opt/
ADD langtext /var/lib

RUN cd /opt/ && \
    wget http://download.oracle.com/glassfish/5.0/release/glassfish-5.0.zip && \
    unzip glassfish-5.0.zip -x "glassfish5/glassfish/domains/*" && \
    rm -f glassfish-5.0.zip && \
    ln -s /opt/glassfish5 /opt/glassfish && \
    update-alternatives --install "/usr/bin/java" "java" "/opt/jdk1.8.0_151/bin/java" 1 && \
    update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk1.8.0_151/bin/javac" 1 && \
    update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk1.8.0_151/bin/javaws" 1 && \
    update-alternatives --install "/usr/bin/jar" "jar" "/opt/jdk1.8.0_151/bin/jar" 1 && \
    ln -s /opt/jdk1.8.0_151 /opt/java && \
    chmod 0750 /start.sh && \
    mkdir /etc/fantasystep


EXPOSE 8080 8181 4848
VOLUME ["/opt/glassfish/glassfish/domains"]
ENTRYPOINT ["/start.sh"]