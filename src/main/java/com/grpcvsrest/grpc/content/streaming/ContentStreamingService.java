package com.grpcvsrest.grpc.content.streaming;

import com.grpcvsrest.grpc.ContentStreamingRequest;
import com.grpcvsrest.grpc.ContentStreamingResponse;
import com.grpcvsrest.grpc.ContentStreamingServiceGrpc.ContentStreamingServiceImplBase;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class ContentStreamingService extends ContentStreamingServiceImplBase {

    private final ContentStreamer streamer;

    public ContentStreamingService(ContentStreamer streamer) {
        this.streamer = streamer;
    }

    @Override
    public void subscribe(ContentStreamingRequest request,
                          StreamObserver<ContentStreamingResponse> responseObserver) {
        ServerCallStreamObserver streamObserver = (ServerCallStreamObserver) responseObserver;
        streamer.addObserver(streamObserver);
    }
}
