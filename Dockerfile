FROM adoptopenjdk:8-hotspot 
ARG BMRG_TAG
ENV BMRG_TAG $BMRG_TAG
COPY target/service-box-$BMRG_TAG.jar /opt
ADD start.sh /opt/bin/start.sh
ENTRYPOINT ["bash", "/opt/bin/start.sh"]
