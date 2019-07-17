
package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
class ProfileService {

    private final ApplicationEventPublisher publisher;
    private final ProfileRepository repository;

    ProfileService(ApplicationEventPublisher publisher, ProfileRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Flux<Profile> all() {
        return this.repository.findAll();
    }

    public Mono<Profile> get(String id) {
        return this.repository.findById(id);
    }

    public Mono<Profile> update(String id, String email) {
        return this.repository
                .findById(id)
                .map(p -> new Profile(p.getId(), email))
                .flatMap(this.repository::save);
    }

    public Mono<Profile> delete(String id) {
        return this.repository
                .findById(id)
                .flatMap(p -> this.repository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<Profile> create(String email) {
        return this.repository
                .save(new Profile(null, email))
                .doOnSuccess(entity -> this.publisher.publishEvent(new ProfileCreatedEvent(entity)));
    }
}

