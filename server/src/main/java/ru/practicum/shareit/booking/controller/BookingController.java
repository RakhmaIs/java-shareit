
package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                                    @RequestHeader(USER_ID) Long id, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bookingService.create(bookingRequestDto, id), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader(USER_ID) Long userId, @PathVariable Long bookingId,
                                                     @RequestParam(name = "approved", required = false) Boolean approved) {
        return new ResponseEntity<>(bookingService.approve(bookingId, userId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        return new ResponseEntity<>(bookingService.getByIdAndBookerId(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(USER_ID) Long userId,
                                                            @RequestParam(name = "from", required = false/*, defaultValue = "0"*/)
                                                            Integer from,
                                                            @RequestParam(name = "size", required = false/*, defaultValue = "10"*/)
                                                            Integer size,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getAllByUser(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerStuffBookings(@RequestHeader(USER_ID) Long ownerId,
                                                                  @RequestParam(name = "from", required = false/*, defaultValue = "0"*/)
                                                                  Integer from,
                                                                  @RequestParam(name = "size", required = false/*, defaultValue = "10"*/)
                                                                  Integer size,
                                                                  @RequestParam(defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getAllByOwner(ownerId, state, from, size), HttpStatus.OK);
    }

}


