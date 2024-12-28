package com.mango.mangocompanyservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {

    private final Storage storage;

    @Value("${gcp.bucket.id}")
    private String bucketName;

    @Value("${gcp.bucket.dir}")
    private String bucketDir;

    private static final String CONTENT_TYPE = "image/*";

    @SneakyThrows
    public Mono<String> save(FilePart file) {
        String fileName = bucketDir + "/" + UUID.randomUUID() + "-" + file.filename();
        BlobId blobId = BlobId.of(bucketName, fileName);

        return extractFileBytes(file)
                .flatMap(fileBytes -> {
                    BlobInfo blobInfo = createBlobInfo(blobId);
                    Blob blob = storage.create(blobInfo, fileBytes);
                    return Mono.just(blob.getName());
                });
    }

    @SneakyThrows
    public void delete(String filePath) {
        BlobId id = BlobId.of(bucketName, filePath);
        storage.delete(id);
    }

    private Mono<byte[]> extractFileBytes(FilePart file) {
        return file.content()
                .collectList()
                .map(dataBuffers -> {
                    int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
                    byte[] byteArray = new byte[totalSize];
                    int offset = 0;

                    for (DataBuffer buffer : dataBuffers) {
                        int bufferSize = buffer.readableByteCount();
                        buffer.read(byteArray, offset, bufferSize);
                        offset += bufferSize;
                        DataBufferUtils.release(buffer);
                    }

                    return byteArray;
                });
    }

    private BlobInfo createBlobInfo(BlobId blobId) {
        return BlobInfo.newBuilder(blobId)
                .setContentType(CONTENT_TYPE)
                .build();
    }
}
