# FROM openeuler/openeuler:22.03 as Builder
FROM openeuler/openeuler:22.03-lts-sp1 as BUILDER

RUN sed -i "s|repo.openeuler.org|mirrors.nju.edu.cn/openeuler|g" /etc/yum.repos.d/openEuler.repo \
    && sed -i '/metalink/d' /etc/yum.repos.d/openEuler.repo \
    && sed -i '/metadata_expire/d' /etc/yum.repos.d/openEuler.repo

WORKDIR /

RUN cd / \
    && yum install -y wget

RUN wget https://mirrors.tuna.tsinghua.edu.cn/Adoptium/18/jdk/x64/linux/OpenJDK18U-jdk_x64_linux_hotspot_18.0.2.1_1.tar.gz \
    && tar -zxvf OpenJDK18U-jdk_x64_linux_hotspot_18.0.2.1_1.tar.gz \
    && wget https://repo.huaweicloud.com/apache/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz \
    && tar -zxvf apache-maven-3.8.1-bin.tar.gz

ENV JAVA_HOME=/jdk-18.0.2.1+1
ENV PATH=${JAVA_HOME}/bin:$PATH

ENV MAVEN_HOME=/apache-maven-3.8.1
ENV PATH=${MAVEN_HOME}/bin:$PATH

COPY . /oneid-workbench

RUN cd oneid-workbench \
    && mvn clean install package -Dmaven.test.skip \
    && mv ./target/oneid-workbench-0.0.1-SNAPSHOT.jar ./target/oneid-workbench.jar

FROM openeuler/openeuler:22.03-lts-sp1

RUN sed -i "s|repo.openeuler.org|mirrors.nju.edu.cn/openeuler|g" /etc/yum.repos.d/openEuler.repo \
    && sed -i '/metalink/d' /etc/yum.repos.d/openEuler.repo \
    && sed -i '/metadata_expire/d' /etc/yum.repos.d/openEuler.repo

RUN yum update -y \
    && yum install -y shadow passwd \
    && groupadd -g 1001 oneid-workbench \
    && useradd -u 1001 -g oneid-workbench -s /bin/bash -m oneid-workbench \
    && yum install -y fontconfig glibc-all-langpacks

ENV LANG=zh_CN.UTF-8
ENV WORKSPACE=/home/oneid-workbench
ENV SOURCE=${WORKSPACE}/file/source
ENV TARGET=${WORKSPACE}/file/target

WORKDIR ${WORKSPACE}

COPY --chown=oneid-workbench --from=Builder /oneid-workbench/target/oneid-workbench.jar ${WORKSPACE}/target/oneid-workbench.jar

RUN echo "umask 027" >> /home/oneid-workbench/.bashrc \
    && echo "umask 027" >> /root/.bashrc \
    && source /home/oneid-workbench/.bashrc \
    && echo "set +o history" >> /etc/bashrc \
    && echo "set +o history" >> /home/oneid-workbench/.bashrc \
    && sed -i "s|HISTSIZE=1000|HISTSIZE=0|" /etc/profile \
    && sed -i "s|PASS_MAX_DAYS[ \t]*99999|PASS_MAX_DAYS 30|" /etc/login.defs \
    && sed -i '4,6d' /home/oneid-workbench/.bashrc

RUN passwd -l oneid-workbench \
    && usermod -s /sbin/nologin sync \
    && usermod -s /sbin/nologin shutdown \
    && usermod -s /sbin/nologin halt \
    && usermod -s /sbin/nologin oneid-workbench \
    && echo "export TMOUT=1800 readonly TMOUT" >> /etc/profile

RUN dnf install -y wget \
    && wget https://mirrors.tuna.tsinghua.edu.cn/Adoptium/18/jre/x64/linux/OpenJDK18U-jre_x64_linux_hotspot_18.0.2.1_1.tar.gz -O jre-18.0.2.tar.gz \
    && tar -zxvf jre-18.0.2.tar.gz \
    && rm jre-18.0.2.tar.gz \
    && chown -R oneid-workbench:oneid-workbench jdk-18.0.2.1+1-jre

RUN rm -rf `find / -iname "*tcpdump*"` \
    && rm -rf `find / -iname "*sniffer*"` \
    && rm -rf `find / -iname "*wireshark*"` \
    && rm -rf `find / -iname "*Netcat*"` \
    && rm -rf `find / -iname "*gdb*"` \
    && rm -rf `find / -iname "*strace*"` \
    && rm -rf `find / -iname "*readelf*"` \
    && rm -rf `find / -iname "*cpp*"` \
    && rm -rf `find / -iname "*gcc*"` \
    && rm -rf `find / -iname "*dexdump*"` \
    && rm -rf `find / -iname "*mirror*"` \
    && rm -rf `find / -iname "*JDK*"` \
    && rm -rf /root/.m2/repository/* \
    && rm -rf /tmp/*

RUN rm -rf /usr/bin/gdb* \
    && rm -rf /usr/share/gdb \
    && rm -rf /usr/share/gcc-10.3.1 \
	&& yum remove gdb-gdbserver findutils passwd shadow -y \
    && yum clean all \
    && chmod 600 -R /home/oneid-workbench/ \
    && chmod 700 /home/oneid-workbench \
    && chmod 500 -R /home/oneid-workbench/jdk-18.0.2.1+1-jre \
    && chmod 500 -R /home/oneid-workbench/target

ENV JAVA_HOME=${WORKSPACE}/jdk-18.0.2.1+1-jre
ENV PATH=${JAVA_HOME}/bin:$PATH
ENV MALLOC_ARENA_MAX=4

EXPOSE 8080

ENV SOURCE= \
    TARGET=

USER oneid-workbench

CMD java --add-opens java.base/java.util=ALL-UNNAMED \
         --add-opens java.base/java.lang=ALL-UNNAMED \
         --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
         -jar ${WORKSPACE}/target/oneid-workbench.jar --spring.config.location=${APPLICATION_PATH}