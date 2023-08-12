package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader(name = USER_ID) Long requesterId,
                                                            @RequestBody ItemRequestPostDto itemRequestPostDto,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.createItemRequest(requesterId, itemRequestPostDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> readAllOtherUsersItemRequests(@RequestHeader(name = USER_ID) Long requesterId,
                                                                         @RequestParam(name = "from", required = false,
                                                                                 defaultValue = "0") Integer from,
                                                                         @RequestParam(name = "size", required = false,
                                                                                 defaultValue = "10") Integer size) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.readAllOtherItemRequests(requesterId, from, size));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> readOwnItemRequests(@RequestHeader(name = USER_ID) Long requesterId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.readOwnItemRequestsById(requesterId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> readOneConcreteItemRequest(@RequestHeader(name = USER_ID) Long requesterId,
                                                                     @PathVariable Long requestId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.readOneConcreteItemRequest(requesterId, requestId));
    }
}
