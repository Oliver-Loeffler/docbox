FROM rockylinux/rockylinux:10-minimal

RUN microdnf -y --refresh upgrade \
    && microdnf -y --best --nodocs --noplugins \
                install  \
                httpd  \
                tar  \
                unzip  \
                curl \
                zip \
                which \
                procps \
                gcc \
                glibc-devel \
                zlib-devel \
                nano \
                vi \
    && microdnf clean all \
    && microdnf -y --best --nodocs --noplugins --enablerepo=crb \
                install  \
                libstdc++-static \
    && microdnf clean all

RUN curl -s "https://get.sdkman.io?ci=true" | bash && \
    source "/root/.sdkman/bin/sdkman-init.sh" && \
    sdk install java 21.0.7-graal && \
    sdk install maven 3.9.9 && \
    mkdir -p /usr/lib/maven && \ 
    mv /root/.sdkman/candidates/maven/3.9.9/ /usr/lib/maven && \
    ln -s /usr/lib/maven/3.9.9/bin/mvn /usr/local/bin/mvn && \
    mkdir -p /usr/lib/jvm && \ 
    mv /root/.sdkman/candidates/java/21.0.7-graal /usr/lib/jvm && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/java /usr/local/bin/java && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/native-image /usr/local/bin/native-image && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/native-image-configure /usr/local/bin/native-image-configure && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/jshell /usr/local/bin/jshell && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/jdeps /usr/local/bin/jdeps && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/jpackage /usr/local/bin/jpackage && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/jar /usr/local/bin/jar && \
    ln -s /usr/lib/jvm/21.0.7-graal/bin/jlink /usr/local/bin/jlink && \
    rm -rf /root/.sdkman && \
    rm -rf /root/.bashrc

ENV JAVA_HOME=/usr/lib/jvm/21.0.7-graal
ENV GRAALVM_HOME=/usr/lib/jvm/21.0.7-graal
ENV MAVEN_HOME=/usr/lib/maven/3.9.9


ENTRYPOINT ["/bin/bash"]
