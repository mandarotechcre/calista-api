package com.mndro.calista.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a common response data transfer object (DTO) used to encapsulate
 * responses returned by APIs or services. It includes a status code, a message,
 * and optional data of generic type T.
 *
 * @param <T> The type of data included in the response.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class CommonResponseDTO<T> {

    /**
     * The HTTP status code indicating the result of the operation.
     */
    private Integer statusCode;

    /**
     * A human-readable message providing additional information about the response.
     */
    private String message;

    /**
     *The optional data payload of generic type T returned with the response.
     */
    private T data;
}
