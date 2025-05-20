package example.videoproxy;

import lombok.ToString;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
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
        release();
        return dataBuffer;
    }

    private void release() {
        DataBufferUtils.release(dataBuffer);
    }

    @Override
    public String toString() {
        return "ResponseWithHeaders{" +
                "headers=" + headers;
    }
}