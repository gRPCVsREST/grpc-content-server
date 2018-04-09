package com.grpcvsrest.grpc.content.streaming;

import com.grpcvsrest.grpc.ContentStreamingRequest;
import com.grpcvsrest.grpc.ContentStreamingResponse;
import com.grpcvsrest.grpc.ContentStreamingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Semaphore;

public class ContentStreamingClient {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
        int port = args != null && args.length > 0? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", port).usePlaintext().build();

        ContentStreamingServiceGrpc.ContentStreamingServiceStub stub =
                ContentStreamingServiceGrpc.newStub(channel);

        Semaphore exitSemaphore = new Semaphore(0);
        stub.subscribe(
                ContentStreamingRequest.getDefaultInstance(),
                new StreamObserver<ContentStreamingResponse>() {
                    @Override
                    public void onNext(ContentStreamingResponse response) {
                        System.out.printf("Content: %s%n", response.getContent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        exitSemaphore.release();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Call completed!");
                        exitSemaphore.release();
                    }
                });

        exitSemaphore.acquire();
    }

}
