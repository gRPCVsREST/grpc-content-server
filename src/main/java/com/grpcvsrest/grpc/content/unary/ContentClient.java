package com.grpcvsrest.grpc.content.unary;

import com.grpcvsrest.grpc.ContentRequest;
import com.grpcvsrest.grpc.ContentResponse;
import com.grpcvsrest.grpc.ContentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

public class ContentClient {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String... args) throws InterruptedException {
        int port = args != null && args.length > 0? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", port).usePlaintext().build();

        ContentServiceGrpc.ContentServiceBlockingStub stub = ContentServiceGrpc.newBlockingStub(channel);

        int itemId = 0;
        while (!Thread.currentThread().isInterrupted()) {
            ContentRequest.Builder requestBuilder = ContentRequest.newBuilder();
            if (itemId != 0) {
                requestBuilder.setItemId(itemId);
            }
            ContentResponse response = stub.get(requestBuilder.build());
            System.out.printf("Content: %s. %s", response.getContent(), response);
            itemId = response.getNextItemId();
            Thread.sleep(1_000);
        }
    }

}
