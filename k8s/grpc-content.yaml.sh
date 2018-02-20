#!/bin/bash

cat <<YAML
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: grpc-content-a
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: grpc-content-a
    spec:
      containers:
        - name: grpc-content-a
          image: gcr.io/$GCP_PROJECT/grpc-content-server:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          env:
            - name: CONTENT_RESOURCE
              value: "$CONTENT_URL_A"
---
apiVersion: v1
kind: Service
metadata:
  name: grpc-content-a
spec:
  type: NodePort
  selector:
    app: grpc-content-a
  ports:
   - port: 8080
     targetPort: 8080
     protocol: TCP
---
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: grpc-content-b
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: grpc-content-b
    spec:
      containers:
        - name: grpc-content-b
          image: gcr.io/$GCP_PROJECT/grpc-content-server:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          env:
            - name: CONTENT_RESOURCE
              value: "$CONTENT_URL_B"
---
apiVersion: v1
kind: Service
metadata:
  name: grpc-content-b
spec:
  type: NodePort
  selector:
    app: grpc-content-b
  ports:
   - port: 8080
     targetPort: 8080
     protocol: TCP
---
YAML