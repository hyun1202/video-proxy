package example.videoproxy;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;

public class ResponseWithHeaders {
    private final HttpHeaders headers;
    private final DataBuffer dataBuffer;

    public ResponseWithHeaders(HttpHeaders headers, DataBuffer dataBuffer) {
        this.headers = headers;
        this.dataBuffer = dataBuffer;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }
}