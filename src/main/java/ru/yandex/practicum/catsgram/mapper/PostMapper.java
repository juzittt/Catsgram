package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.Post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.Post.PostDto;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostMapper {

    public static Post mapToPost(NewPostRequest request, long authorId) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());
        return post;
    }

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthorId());
        dto.setDescription(post.getDescription());
        dto.setPostDate(post.getPostDate());
        return dto;
    }
}