FROM openjdk:8-jre-slim

ARG VERSION

# Installs libopus on Debian
RUN apt-get update && apt-get install -y libopus0

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD ./media-server-standalone-${VERSION} /opt/mediaserver

CMD [ \
  "/usr/bin/java", \
  "-Dprogram.name=mediaserver", \
  "-Xms2g", \
  "-Xmx2g", \
  "-XX:+UseG1GC", \
  "-XX:ParallelGCThreads=8", \
  "-XX:ConcGCThreads=8", \
  "-XX:G1RSetUpdatingPauseTimePercent=10", \
  "-XX:+ParallelRefProcEnabled", \
  "-XX:G1HeapRegionSize=4m", \
  "-XX:G1HeapWastePercent=5", \
  "-XX:InitiatingHeapOccupancyPercent=85", \
  "-XX:+UnlockExperimentalVMOptions", \
  "-XX:G1MixedGCLiveThresholdPercent=85", \
  "-XX:+AlwaysPreTouch", \
  "-XX:+UseCompressedOops", \
  "-Dhttp.keepAlive=false", \
  # "-Djava.library.path=/usr/lib/x86_64-linux-gnu:/opt/mediaserver/lib/native", \
  # "-Drestcomm.opus.library=opus_jni_linux", \
  "-Dspring.output.ansi.enabled=DETECT", \
  "-Dspring.config.location=/opt/mediaserver/conf/", \
  "-Dspring.config.name=media-plugin-vad-noise-threshold,media-plugin-dtmf-detector-goertzel,media-server,media-extra", \
  "-Dlogging.config=/opt/mediaserver/conf/log4j2.yml", \
  "-Djava.ext.dirs=/opt/mediaserver/lib:/docker-java-home/jre/lib/ext", \
  "-Dmbrola.base=/opt/mediaserver/mbrola", \
  "-classpath", \
  "/opt/mediaserver/bin/media-server-standalone-bootstrap.jar", \
  "org.restcomm.media.server.standalone.bootstrap.spring.SpringBootstrapper" \
]

