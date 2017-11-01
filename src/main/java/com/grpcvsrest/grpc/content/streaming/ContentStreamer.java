package com.grpcvsrest.grpc.content.streaming;

import com.google.common.collect.Iterators;
import com.grpcvsrest.grpc.ContentStreamingResponse;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.grpcvsrest.content.ContentProducer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ContentStreamer {

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(0);
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final List<ContentStreamingResponse> responses = new CopyOnWriteArrayList<>();

    private final ContentProducer contentProducer;
    private final ScheduledFuture<?> generateContentTask;
    private final long delayInMillis;

    private final Set<StreamObserver<ContentStreamingResponse>> observers =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ContentStreamer(ContentProducer contentProducer, long delayInMillis) {
        this.contentProducer = contentProducer;
        this.delayInMillis = delayInMillis;
        this.generateContentTask =
                executor.scheduleAtFixedRate(this::generateContent,
                        delayInMillis, delayInMillis, MILLISECONDS);
    }

    void addObserver(ServerCallStreamObserver<ContentStreamingResponse> responseObserver) {
        observers.add(responseObserver);
        responseObserver.setOnCancelHandler(() -> observers.remove(responseObserver));
    }

    private void generateContent() {
        String value = contentProducer.next();
        if (value != null) {
            ContentStreamingResponse response = ContentStreamingResponse.newBuilder()
                    .setId(idGenerator.getAndIncrement())
                    .setContent(value + ".")
                    .build();
            responses.add(response);
            stream(response);
        } else {
            scheduleStreaming();
        }
    }

    private void scheduleStreaming() {
        generateContentTask.cancel(false);
        Iterator<ContentStreamingResponse> responsesIterator = Iterators.cycle(responses);
        executor.scheduleAtFixedRate(() -> stream(responsesIterator.next()),
                delayInMillis, delayInMillis, MILLISECONDS);
    }

    private void stream(ContentStreamingResponse response) {
        observers.forEach(c -> c.onNext(response));
    }
}
