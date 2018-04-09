#!/bin/bash

if [ -z $GCP_PROJECT ]; then echo "GCP_PROJECT is not set"; exit -1; fi
if [ -z $CONTENT_URL_A ]; then echo "CONTENT_URL_A is not set"; exit -1; fi
if [ -z $CONTENT_URL_B ]; then echo "CONTENT_URL_B is not set"; exit -1; fi

$(dirname $0)/grpc-content.yaml.sh | kubectl delete -f -