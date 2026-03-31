// src/main/java/ru/yandex/practicum/catsgram/service/PostService.java
package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public Collection<Post> findAll(SortOrder sort, int from, int size) {
        return postRepository.findAll().stream()
                .sorted(sort.equals(SortOrder.ASCENDING) ?
                        (p1, p2) -> p1.getPostDate().compareTo(p2.getPostDate()) :
                        (p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()))
                .skip(from)
                .limit(size)
                .toList();
    }

    public Optional<Post> findById(long postId) {
        return postRepository.findById(postId);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().trim().isEmpty()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        try {
            userService.getUserById(post.getAuthorId());
        } catch (NotFoundException e) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }
        post.setPostDate(java.time.Instant.now());
        return postRepository.save(post);
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Post existing = postRepository.findById(newPost.getId())
                .orElseThrow(() -> new NotFoundException("Пост с id = " + newPost.getId() + " не найден"));
        if (newPost.getDescription() == null || newPost.getDescription().trim().isEmpty()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        existing.setDescription(newPost.getDescription());
        return postRepository.update(existing);
    }
}