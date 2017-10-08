package com.grpcvsrest.grpc.content.unary;

import com.grpcvsrest.grpc.ContentRequest;
import com.grpcvsrest.grpc.ContentResponse;
import com.grpcvsrest.grpc.ContentServiceGrpc.ContentServiceImplBase;
import com.grpcvsrest.grpc.content.unary.storage.ContentStorage;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class ContentService extends ContentServiceImplBase {

    private final ContentStorage contentStorage;

    public ContentService(ContentStorage contentStorage) {
        this.contentStorage = contentStorage;
    }

    @Override
    public void get(ContentRequest request, StreamObserver<ContentResponse> responseObserver) {

        int id = request.getItemId();
        if (id > 0) {
            Optional<ContentResponse> contentOpt = contentStorage.get(id);
            ContentResponse content = contentOpt.orElseThrow(() ->
                    Status.OUT_OF_RANGE.withDescription(String.format("Content with id %s cannot be found.", id))
                            .asRuntimeException());
            responseObserver.onNext(content);
        } else {
            contentStorage.next(responseObserver::onNext);
        }
        responseObserver.onCompleted();
    }
}
