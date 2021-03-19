#!/bin/bash
if [[ "$1" == "test" ]]
#20201218
then
    mkdir -p /lifecycle
    echo 'image=box-function' >> /lifecycle/env
    echo "update=20201217001" >> /lifecycle/env
    echo "BMRG_TAG=$BMRG_TAG" >> /lifecycle/env
    echo "args=$@" >> /lifecycle/env
    env
    env > /lifecycle/myenv
    exit
else
    echo "start box function $BMRG_TAG"
    java -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT -Dhttp.nonProxyHosts=$NO_PROXY -jar /opt/service-box-$BMRG_TAG.jar "$@"
    exit
fi
