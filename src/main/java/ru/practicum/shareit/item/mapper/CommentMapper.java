package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentResponseDto toResponseDto(Comment comment) {
        return comment != null ?
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .authorName(comment.getAuthor().getName())
                        .created(comment.getCreated())
                        .build() : null;
    }

    public static Comment fromRequestDto(CommentRequestDto commentRequestDto, Item item, User user, LocalDateTime now) {
        return commentRequestDto != null ?
                Comment.builder()
                        .text(commentRequestDto.getText())
                        .author(user)
                        .item(item)
                        .created(now)
                        .build() : null;
    }

    public static List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toResponseDto).collect(Collectors.toList());
    }
}