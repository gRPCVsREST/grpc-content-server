package com.grpcvsrest.grpc.content.streaming;

import com.grpcvsrest.grpc.ContentStreamingRequest;
import com.grpcvsrest.grpc.ContentStreamingResponse;
import com.grpcvsrest.grpc.ContentStreamingServiceGrpc.ContentStreamingServiceImplBase;
import io.grpc.stub.StreamObserver;
import org.grpcvsrest.content.ContentProducer;
import org.grpcvsrest.content.ContentStreamer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ContentStreamingService extends ContentStreamingServiceImplBase {

    private final Supplier<ContentProducer> contentProducerFactory;

    public ContentStreamingService(Supplier<ContentProducer> contentProducerFactory) {
        this.contentProducerFactory = contentProducerFactory;
    }

    @Override
    public void subscribe(ContentStreamingRequest request,
                          StreamObserver<ContentStreamingResponse> responseObserver) {

        AtomicInteger idGenerator = new AtomicInteger(1);
        new ContentStreamer(contentProducerFactory.get(),
                content -> {
                    if (content != null) {
                        ContentStreamingResponse response = build(idGenerator.getAndIncrement(), content);
                        responseObserver.onNext(response);
                    } else {
                        responseObserver.onCompleted();
                    }
                });
    }

    private ContentStreamingResponse build(int id, String content) {
        return ContentStreamingResponse.newBuilder()
                .setId(id).setContent(content).build();
    }

}
