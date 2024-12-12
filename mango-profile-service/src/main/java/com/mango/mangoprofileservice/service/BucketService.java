package com.mango.mangoprofileservice.service;

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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {

    private final Storage storage;

    @Value("${gcp.bucket.id}")
    private String bucketName;

    @Value("${gcp.bucket.dir}")
    private String bucketDir;

    @SneakyThrows
    public String saveFile(long id, FilePart file) {
        String fileName = bucketDir + "/" + id + "-" + file.filename();
        BlobId blobId = BlobId.of(bucketName, fileName);

        byte[] fileBytes = extractFileBytes(file);

        BlobInfo blobInfo = createBlobInfo(blobId);
        Blob blob = storage.create(blobInfo, fileBytes);

        return blob.getName();
    }

    @SneakyThrows
    public void dropFile(String filePath) {
        BlobId id = BlobId.of(bucketName, filePath);
        storage.delete(id);
    }

    @SneakyThrows
    public byte[] getFile(String url) {
        BlobId id = BlobId.of(bucketName, url);
        Blob blob = storage.get(id);
        return blob.getContent();
    }

    private byte[] extractFileBytes(FilePart file) {
        List<DataBuffer> dataBuffers = file.content().collectList().block();

        if (dataBuffers == null || dataBuffers.isEmpty()) {
            throw new IllegalArgumentException("File content is empty.");
        }

        int size = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        byte[] byteArray = new byte[size];
        int offset = 0;

        for (DataBuffer buffer : dataBuffers) {
            int bufferSize = buffer.readableByteCount();
            buffer.read(byteArray, offset, bufferSize);
            offset += bufferSize;
            DataBufferUtils.release(buffer);
        }

        return byteArray;
    }

    private BlobInfo createBlobInfo(BlobId blobId) {
        return BlobInfo.newBuilder(blobId)
                .setContentType("application/pdf")
                .build();
    }
}
