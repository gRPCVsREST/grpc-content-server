package com.grpcvsrest.grpc.content.unary.storage;

import com.grpcvsrest.grpc.ContentResponse;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Stores and provides content as a {@link ContentResponse} by id.
 */
public interface ContentStorage {

    Optional<ContentResponse> get(int id);

    void next(Consumer<ContentResponse> callback);
}
