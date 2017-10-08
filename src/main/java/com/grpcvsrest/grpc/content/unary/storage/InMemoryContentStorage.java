package com.grpcvsrest.grpc.content.unary.storage;

import com.grpcvsrest.grpc.ContentResponse;
import io.grpc.Status;
import org.grpcvsrest.content.ContentProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toConcurrentMap;

/**
 * In-memory implementation of {@link ContentStorage}.
 */
public class InMemoryContentStorage implements ContentStorage {

    private final Map<Integer, ContentResponse> responses;

    public InMemoryContentStorage(ContentProducer contentProducer) {

        List<ContentResponse.Builder> builders = new ArrayList<>();

        int currentId = 1;
        String content = contentProducer.next();
        if (content == null) {
            throw new IllegalStateException("Content is empty.");
        }

        ContentResponse.Builder previous =
                ContentResponse.newBuilder()
                        .setId(currentId++)
                        .setContent(content);
        builders.add(previous);

        while (content != null) {

            int id = currentId++;
            previous.setNextItemId(id);

            ContentResponse.Builder response =
                    ContentResponse.newBuilder()
                            .setId(id)
                            .setContent(content);
            builders.add(response);

            previous = response;
            content = contentProducer.next();
        }

        responses = builders.stream()
                .map(ContentResponse.Builder::build)
                .collect(toConcurrentMap(ContentResponse::getId, Function.identity()));

    }

    @Override
    public Optional<ContentResponse> get(int id) {
        return Optional.ofNullable(responses.get(Integer.valueOf(id)));
    }

    @Override
    public void next(Consumer<ContentResponse> callback) {
        ContentResponse element = responses.values().stream().findFirst().orElseThrow(() ->
                Status.NOT_FOUND.withDescription(
                        String.format("Content cannot be found."))
                        .asRuntimeException());
        callback.accept(element);
    }

}
