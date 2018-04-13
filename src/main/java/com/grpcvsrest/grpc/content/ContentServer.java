package com.grpcvsrest.grpc.content;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.grpcvsrest.grpc.content.streaming.ContentStreamer;
import com.grpcvsrest.grpc.content.streaming.ContentStreamingService;
import com.grpcvsrest.grpc.content.unary.ContentService;
import com.grpcvsrest.grpc.content.unary.storage.InMemoryContentStorage;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.netty.NettyServerBuilder;
import org.grpcvsrest.content.ContentProducer;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static brave.sampler.Sampler.ALWAYS_SAMPLE;

public class ContentServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        ContentStreamer streamer = new ContentStreamer(createContentProducer(),
                TimeUnit.MILLISECONDS.toMillis(1 + new Random().nextInt(49)));
        Server grpcServer = NettyServerBuilder.forPort(8080)
                .addService(new ContentService(new InMemoryContentStorage(createContentProducer())))
                .addService(new ContentStreamingService(streamer))
                .intercept(grpcTracing().newServerInterceptor()).build()
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

    private static GrpcTracing grpcTracing() {

        String zipkinHost = System.getenv("ZIPKIN_SERVICE_HOST");
        int zipkinPort = Integer.valueOf(System.getenv("ZIPKIN_SERVICE_PORT"));

        URLConnectionSender sender = URLConnectionSender.newBuilder()
                .endpoint(String.format("http://%s:%s/api/v2/spans", zipkinHost, zipkinPort))
                .build();

        return GrpcTracing.create(Tracing.newBuilder()
                .sampler(ALWAYS_SAMPLE)
                .spanReporter(AsyncReporter.create(sender))
                .build());
    }

}
