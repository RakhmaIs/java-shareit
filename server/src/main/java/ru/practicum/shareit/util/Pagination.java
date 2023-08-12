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
/*    public static Pageable getPaginationWithSortDesc(Integer fromIndex, Integer pageSize) {
        Sort sortDesc = Sort.by("created").descending();

        if (fromIndex == null || pageSize == null) {
            return PageRequest.of(0, Integer.MAX_VALUE, sortDesc);
        }
        if (fromIndex < 0 || pageSize < 1) {
            log.error("Один из параметров пагинации или оба неверны");
            throw new PaginationInvalidParamException("Один из параметров или оба параметра пагинации не верны");
        }
        if (fromIndex == 0) {
            return PageRequest.of(0, pageSize, sortDesc);
        }
        return PageRequest.of(fromIndex / pageSize, pageSize, sortDesc);
    }

    public static Pageable getPaginationWithoutSort(Integer fromIndex, Integer pageSize) {
        if (fromIndex == null || pageSize == null) {
            return PageRequest.of(0, Integer.MAX_VALUE);
        }
        if (fromIndex < 0 || pageSize < 0) {
            log.error("Один из параметров пагинации или оба неверны");
            throw new PaginationInvalidParamException("Один из параметров или оба параметра пагинации не верны");
        }
        if (fromIndex == 0) {
            return PageRequest.of(0, pageSize);
        }

        return PageRequest.of(fromIndex / pageSize, pageSize);
    }*/

    public static Pageable getPaginationWithSortDesc(Integer from, Integer size) {
        Sort sortCreated = Sort.by("created").descending();

        if (from == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE, sortCreated);
        }

        if (from < 0 || size < 0) {
            throw new PaginationInvalidParamException("Неверные параметры пагинации.");
        }

        return PageRequest.of(from / size, size, sortCreated);
    }

    public static Pageable getPaginationWithoutSort(Integer from, Integer size) {
        if (from == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE);
        }

        if (from < 0 || size < 0) {
            throw new PaginationInvalidParamException("Неверные параметры пагинации.");
        }

        return PageRequest.of(from / size, size);
    }
}
