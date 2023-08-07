package ru.practicum.shareit.request.controllertest;

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
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID;

@WebMvcTest(controllers = {ItemRequestController.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    ItemRequestDto itemRequestDto;
    List<ItemRequestDto> itemRequestListDto;
    ItemRequestPostDto itemRequestPostDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = createItemRequestDto();
        itemRequestListDto = createItemRequestDtoList();
        itemRequestPostDto = createItemRequestPostDto();
    }

    @Test
    @SneakyThrows
    void readOwnItemRequestsByIdWhenInvokeWithEmptyDataShouldReturnStatusOk() {
        when(itemRequestService.readOwnItemRequestsById(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void readOwnItemRequestsByIdWhenRequestHaveOneListShouldReturnStatusOk() {
        when(itemRequestService.readOwnItemRequestsById(anyLong())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests")
                        .header(USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void readOneConcreteItemRequestWhenInvokedWithValidDataShouldReturnStatusOk() {
        when(itemRequestService.readOneConcreteItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void readAllOtherItemRequestsWhenInvokedShouldReturnOneRequestAndStatusOk() {
        when(itemRequestService.readAllOtherItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void readAllOtherItemRequestsWhenInvokedWithEmptyListShouldReturnStatusOk() {
        when(itemRequestService.readAllOtherItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createItemRequestWhenInvokedWithValidDataShouldReturnStatusCreated() {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, "1")
                        .content(objectMapper.writeValueAsString(itemRequestPostDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createItemRequestWhenInvokeWithEmptyDescriptionShouldReturnStatusBadRequest() {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    private static ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("test")
                .items(List.of(ItemDto.builder().id(1L).build()))
                .build();
    }

    private static ItemRequestPostDto createItemRequestPostDto() {
        return ItemRequestPostDto.builder()
                .description("test")
                .build();
    }

    private static List<ItemRequestDto> createItemRequestDtoList() {
        return List.of(createItemRequestDto());
    }
}

