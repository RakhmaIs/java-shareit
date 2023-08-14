package ru.practicum.shareit.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.PaginationInvalidParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {
    @Test
    @DisplayName("getPaginationWithSortDesc - valid params - success")
    void getPaginationWithSortDescShouldReturnValidPageWhenDataIsValid() {
        Pageable paginationWithSortDesc = Pagination.getPaginationWithSortDesc(20, 5);
        assertEquals(4, paginationWithSortDesc.getPageNumber());
        assertEquals(5, paginationWithSortDesc.getPageSize());
    }

    @Test
    @DisplayName("getPaginationWithSortDesc - 0 1 - success")
    void getPaginationWithSortDescWhenPaginationFromEquals0SizeEquals1ShouldReturnPage0() {
        Pageable paginationWithSortDesc = Pagination.getPaginationWithSortDesc(0, 1);

        assertEquals(0, paginationWithSortDesc.getPageNumber());
        assertEquals(1, paginationWithSortDesc.getPageSize());
    }

    @Test
    @DisplayName("getPaginationWithoutSortDesc - valid params - success")
    void getPaginationWithoutSortDescShouldReturnValidPageWhenDataIsValid() {
        Pageable paginationWithSortDesc = Pagination.getPaginationWithoutSort(20, 5);
        assertEquals(4, paginationWithSortDesc.getPageNumber());
        assertEquals(5, paginationWithSortDesc.getPageSize());
    }

    @Test
    @DisplayName("getPaginationWithoutSortDesc - 0 1 - success page 0")
    void getPaginationWithoutSort_whenPaginationFrom0Size1_ThenReturnPage0() {
        Pageable paginationWithoutSort = Pagination.getPaginationWithoutSort(0, 1);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(1, paginationWithoutSort.getPageSize());
    }

    @Test
    @DisplayName("getPaginationWithoutSortDesc - null null - success page 0")
    void getPaginationWithoutSort_whenPaginationNull_ThenReturnPage0() {
        Pageable paginationWithoutSort = Pagination.getPaginationWithoutSort(null, null);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(20, paginationWithoutSort.getPageSize());
    }

    @Test
    @DisplayName("getPaginationWithoutSortDesc - negative negative - throw PaginationInvalidParamException")
    void getPaginationWithoutSort_whenPaginationNegative_ThenThrowPaginationParameterException() {
        PaginationInvalidParamException exception = assertThrows(PaginationInvalidParamException.class,
                () -> Pagination.getPaginationWithoutSort(-1, -1));

        assertEquals("Неверные параметры пагинации.", exception.getMessage());
    }

}
