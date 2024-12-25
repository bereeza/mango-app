package com.mango.mangoprofileservice.service;

import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.user.UserByIdDto;
import com.mango.mangoprofileservice.dto.user.UserInfoDto;
import com.mango.mangoprofileservice.entity.User;
import com.mango.mangoprofileservice.exception.BadRequestException;
import com.mango.mangoprofileservice.exception.CVNotFoundException;
import com.mango.mangoprofileservice.exception.UserNotFoundException;
import com.mango.mangoprofileservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BucketService bucketService;
    private final UserRedisService userRedisService;

    private static final String PDF = ".pdf";

    public Mono<UserInfoDto> getCurrentUser(ServerWebExchange exchange) {
        return userRedisService.buildUser(exchange);
    }

    public Mono<Response<String>> deleteCurrentUser(ServerWebExchange exchange) {
        return getCurrentUser(exchange)
                .flatMap(user -> userRedisService.deleteByToken(exchange)
                        .then(deleteUserIfExists(user)));
    }

    public Mono<UserByIdDto> getUserById(long id) {
        return userRepository.findById(id)
                .map(this::buildUserById)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
    }

    public Mono<Response<String>> updateUserLink(ServerWebExchange exchange, String link) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateLink(currentUser.getId(), link)
                                .then(Mono.just(Response.<String>builder()
                                        .code(HttpStatus.OK.value())
                                        .message("Link updated successfully.")
                                        .body(link)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating link: {}", e.getMessage());
                    return Mono.error(new BadRequestException("Error updating link."));
                });
    }

    public Mono<Response<String>> updateUserAboutSection(ServerWebExchange exchange, String about) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateUserAbout(currentUser.getId(), about)
                                .then(Mono.just(Response.<String>builder()
                                        .code(HttpStatus.OK.value())
                                        .message("About section updated successfully.")
                                        .body(about)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating about section: {}", e.getMessage());
                    return Mono.error(new BadRequestException("Error updating about section."));
                });
    }

    public Mono<Response<String>> updateUserCV(ServerWebExchange exchange, FilePart file) {
        return getCurrentUser(exchange)
                .flatMap(currentUser -> {
                    long maxSize = 5 * 1024 * 1024;
                    if (!file.filename().endsWith(PDF) && file.headers().getContentLength() > maxSize) {
                        return Mono.error(new BadRequestException("Upload a PDF file up to 5 MB."));
                    }

                    if (currentUser.getCv() != null) {
                        bucketService.dropFile(currentUser.getCv());
                    }

                    return bucketService.saveFile(currentUser.getId(), file)
                            .flatMap(link -> userRepository.updateUserCV(currentUser.getId(), link)
                                    .then(Mono.just(Response.<String>builder()
                                            .code(HttpStatus.OK.value())
                                            .message("CV successfully saved")
                                            .body(link)
                                            .build())));
                })
                .onErrorResume(e -> {
                    log.error("Error updating file. {}", e.getMessage());
                    return Mono.error(new BadRequestException("Error updating file."));
                });
    }

    @SneakyThrows
    public Mono<ResponseEntity<ByteArrayResource>> findCVById(long id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    String url = user.getCv();

                    if (url == null || url.isEmpty()) {
                        return Mono.error(new CVNotFoundException("CV not found."));
                    }

                    byte[] fileBytes = bucketService.getFile(url);
                    ByteArrayResource resource = new ByteArrayResource(fileBytes);

                    return Mono.just(ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .contentLength(fileBytes.length)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "application/pdf")
                            .body(resource));
                })
                .onErrorResume(e -> {
                    log.error("Error fetching CV: {}", e.getMessage());
                    return Mono.error(new CVNotFoundException("Error fetching CV."));
                });
    }

    private Mono<Response<String>> deleteUserIfExists(UserInfoDto userInfo) {
        Long userId = userInfo.getId();
        return userRepository.existsById(userId)
                .flatMap(exist -> exist ? deleteUser(userId) :
                        Mono.error(new UserNotFoundException("Current user not found")));
    }

    private Mono<Response<String>> deleteUser(Long userId) {
        return userRepository.deleteById(userId)
                .then(Mono.fromSupplier(() -> Response.<String>builder()
                        .code(HttpStatus.OK.value())
                        .message("User deleted successfully.")
                        .body("User deleted successfully.")
                        .build()));
    }

    public UserByIdDto buildUserById(User user) {
        return UserByIdDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .cv(user.getCv())
                .about(user.getAbout())
                .reputation(user.getReputation())
                .link(user.getLink())
                .build();
    }
}
