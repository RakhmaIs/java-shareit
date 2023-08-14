package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.PaginationInvalidParamException;

@UtilityClass
@Slf4j
public class Pagination {
    public static Pageable getPaginationWithSortDesc(Integer from, Integer size) {
        Sort sortCreated = Sort.by("created").descending();

        if (from == null || size == null) {
            return PageRequest.of(0, 20, sortCreated);
        }

        if (from < 0 || size < 0) {
            throw new PaginationInvalidParamException("Неверные параметры пагинации.");
        }

        return PageRequest.of(from / size, size, sortCreated);
    }

    public static Pageable getPaginationWithoutSort(Integer from, Integer size) {
        if (from == null || size == null) {
            return PageRequest.of(0, 20);
        }

        if (from < 0 || size < 0) {
            throw new PaginationInvalidParamException("Неверные параметры пагинации.");
        }

        return PageRequest.of(from / size, size);
    }
}

