package com.mango.mangoprofileservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {

    private final Storage storage;
    private static final String BUCKET_NAME = "mango-app";
    private static final String GOOGLE_STORAGE = "https://storage.googleapis.com/";

    @SneakyThrows
    public String save(long id, FilePart file) {
        String fileName = id + "-" + file.filename();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);

        byte[] fileBytes = extractFileBytes(file);

        BlobInfo blobInfo = createBlobInfo(blobId);
        Blob blob = storage.create(blobInfo, fileBytes);

        return GOOGLE_STORAGE + blob.getBucket() + "/" + blob.getName();
    }

    @SneakyThrows
    public Resource getFileByUrl(String fileUrl) {
        URL url = new URL(fileUrl);
        return new UrlResource(url);
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