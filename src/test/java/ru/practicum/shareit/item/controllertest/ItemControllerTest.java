package ru.practicum.shareit.item.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@AutoConfigureMockMvc
class ItemControllerTest {
    String HEADER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    ItemDto itemDto;
    List<ItemDto> itemDtoList;
    ItemResponseDto itemResponseDto;
    List<ItemResponseDto> itemResponseListDto;
    CommentResponseDto commentResponseDto;
    CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
        itemDto = createItemDto();
        itemResponseDto = createResponseItemDto();
        commentResponseDto = createCommentResponseDto();
        commentRequestDto = createCommentRequestDto();
        itemResponseListDto = itemResponseDtoList();
        itemDtoList = itemDtoList();
    }

    @Test
    @SneakyThrows
    void getAllItems_whenEmptyList_thenStatusOk() {
        when(itemService.readItemsOwnedByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAllItems_whenListHaveOneItem_thenStatusOk() {
        when(itemService.readItemsOwnedByUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemResponseListDto);


        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvokedCorrect_thenStatusOk() {
        when(itemService.readItemByItemIdAndUserId(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)))
                .andDo(print())
                .andReturn();
    }


    @Test
    @SneakyThrows
    void addItem_whenInvokedCorrect_thenStatusCreated() {
        when(itemService.createItem(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
    }


    @Test
    @SneakyThrows
    void updateItem_whenInvokedCorrect_thenStatusOk() {
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void searchShouldReturnStatusOkWhenDataIsValid() {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "description")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andDo(print())
                .andReturn();


        ItemDto.builder()
                .requestId(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingResponseDto.builder().id(1L).build())
                .lastBooking(BookingResponseDto.builder().id(2L).build())
                .comments(List.of())
                .build();
    }

    @Test
    @SneakyThrows
    void deleteItem_whenNotExistItem_thenStatusNotFound() {
        doThrow(ItemNotFoundException.class).when(itemService).deleteById(0L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @SneakyThrows
    void addComment_whenInvokedCorrect_thenStatusOk() {
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponseDto)))
                .andDo(print())
                .andReturn();
    }

    private static ItemDto createItemDto() {
        return ItemDto.builder()
                .requestId(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingResponseDto.builder().id(1L).build())
                .lastBooking(BookingResponseDto.builder().id(2L).build())
                .comments(List.of())
                .build();
    }

    private static ItemResponseDto createResponseItemDto() {
        return ItemResponseDto.builder()
                .id(1L)
                .requestId(1L)
                .name("name")
                .description("description")
                .available(true)
                .nextBooking(BookingResponseDto.builder().id(1L).build())
                .lastBooking(BookingResponseDto.builder().id(2L).build())
                .comments(List.of())
                .build();
    }

    private static CommentRequestDto createCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("comment")
                .build();
    }

    private static CommentResponseDto createCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .authorName("name")
                .created(LocalDateTime.now())
                .text("comment")
                .build();
    }

    private static List<ItemResponseDto> itemResponseDtoList() {
        return List.of(createResponseItemDto());
    }

    private static List<ItemDto> itemDtoList() {
        return List.of(createItemDto());
    }
}