package com.grpcvsrest.grpc.content;

import com.grpcvsrest.grpc.content.streaming.ContentStreamingService;
import com.grpcvsrest.grpc.content.unary.ContentService;
import com.grpcvsrest.grpc.content.unary.storage.InMemoryContentStorage;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.netty.NettyServerBuilder;
import org.grpcvsrest.content.ContentProducer;

import java.io.IOException;

public class ContentServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server grpcServer = NettyServerBuilder.forPort(8080)
                .addService(new ContentService(new InMemoryContentStorage(createContentProducer())))
                .addService(new ContentStreamingService(ContentServer::createContentProducer)).build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(grpcServer::shutdown));
        grpcServer.awaitTermination();
    }

    private static ContentProducer createContentProducer() {
        try {
            return new ContentProducer(System.getenv("CONTENT_RESOURCE"));
        } catch (IOException e) {
            throw Status.INTERNAL.withCause(e).asRuntimeException();
        }
    }

}
