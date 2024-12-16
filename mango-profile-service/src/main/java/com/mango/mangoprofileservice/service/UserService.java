package com.mango.mangoprofileservice.service;

import com.mango.mangoprofileservice.dto.Response;
import com.mango.mangoprofileservice.dto.user.UserById;
import com.mango.mangoprofileservice.dto.user.UserInfoDto;
import com.mango.mangoprofileservice.entity.User;
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

    public Mono<UserInfoDto> getCurrentUser(ServerWebExchange exchange) {
        return userRedisService.buildUser(exchange);
    }

    public Mono<Response> deleteCurrentUser(ServerWebExchange exchange) {
        return getCurrentUser(exchange)
                .flatMap(this::deleteUserIfExists);
    }

    public Mono<UserById> getUserById(long id) {
        return userRepository.findById(id)
                .map(this::buildUserById)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
    }

    public Mono<Response> updateUserLinks(ServerWebExchange exchange, String link) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateLink(currentUser.getId(), link)
                                .then(Mono.just(Response.builder()
                                        .message("Link updated successfully.")
                                        .status(HttpStatus.OK)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating link: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<Response> updateUserAboutSection(ServerWebExchange exchange, String about) {
        return getCurrentUser(exchange)
                .flatMap(currentUser ->
                        userRepository.updateUserAbout(currentUser.getId(), about)
                                .then(Mono.just(Response.builder()
                                        .message("About section updated successfully.")
                                        .status(HttpStatus.OK)
                                        .build()))
                )
                .onErrorResume(e -> {
                    log.error("Error updating about section: {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    public Mono<Response> updateUserCV(ServerWebExchange exchange, FilePart file) {
        return getCurrentUser(exchange)
                .flatMap(currentUser -> {
                    if (currentUser.getCv() != null) {
                        bucketService.dropFile(currentUser.getCv());
                    }

                    String link = bucketService.saveFile(currentUser.getId(), file);
                    return userRepository.updateUserCV(currentUser.getId(), link)
                            .then(Mono.just(Response.builder()
                                    .status(HttpStatus.OK)
                                    .message("CV successfully saved")
                                    .build()));
                })
                .onErrorResume(e -> {
                    log.error("Error updating file. {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    @SneakyThrows
    public Mono<ResponseEntity<ByteArrayResource>> findCVById(long id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    String url = user.getCv();

                    if (url == null || url.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("CV not found for user"));
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
                    log.error("Error fetching CV. {}", e.getMessage());
                    return Mono.error(new IllegalArgumentException(e.getMessage()));
                });
    }

    private Mono<Response> deleteUserIfExists(UserInfoDto userInfo) {
        Long userId = userInfo.getId();
        return userRepository.existsById(userId)
                .flatMap(exists -> exists ? deleteUser(userId) : Mono.error(new UserNotFoundException("Current user not found")));
    }

    private Mono<Response> deleteUser(Long userId) {
        return userRepository.deleteById(userId)
                .then(Mono.fromSupplier(() -> Response.builder()
                        .message("User deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .build()))
                .onErrorResume(e -> {
                    log.error("User wasn't deleted.");
                    return Mono.empty();
                });
    }

    public UserById buildUserById(User user) {
        return UserById.builder()
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
