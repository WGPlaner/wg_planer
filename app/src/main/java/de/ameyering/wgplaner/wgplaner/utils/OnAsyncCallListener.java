package de.ameyering.wgplaner.wgplaner.utils;

import io.swagger.client.ApiException;

public interface OnAsyncCallListener<T> {
    void onFailure(ApiException e);

    void onSuccess(T result);
}
