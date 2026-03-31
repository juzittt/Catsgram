// src/main/java/ru/yandex/practicum/catsgram/controller/PostController.java
package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.Post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.Post.PostDto;
import ru.yandex.practicum.catsgram.dto.Post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public Collection<PostDto> findAll(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "asc") String sort) {

        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше 0");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть >= 0");
        }
        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            throw new ParameterNotValidException("sort", "Должно быть 'asc' или 'desc'");
        }

        return postService.findAll(sortOrder, from, size).stream()
                .map(PostMapper::mapToPostDto)
                .toList();
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable long postId) {
        return postService.findById(postId)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(() -> new ru.yandex.practicum.catsgram.exception.NotFoundException("Пост не найден"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(
            @RequestBody NewPostRequest request,
            @RequestParam long authorId) {
        Post post = PostMapper.mapToPost(request, authorId);
        post = postService.create(post);
        return PostMapper.mapToPostDto(post);
    }

    @PutMapping("/{postId}")
    public PostDto update(
            @PathVariable long postId,
            @RequestBody UpdatePostRequest request) {
        if (!request.hasDescription()) {
            throw new ParameterNotValidException("description", "Описание не может быть пустым");
        }

        Post existing = postService.findById(postId)
                .orElseThrow(() -> new ru.yandex.practicum.catsgram.exception.NotFoundException("Пост не найден"));

        existing.setDescription(request.getDescription());
        Post updated = postService.update(existing);

        return PostMapper.mapToPostDto(updated);
    }
}