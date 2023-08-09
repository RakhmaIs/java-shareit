package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findItemsByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);


    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.available = true" +
            " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))",
            countQuery = "SELECT COUNT(i) FROM Item i " +
                    "JOIN i.owner o " +
                    "WHERE i.available = true" +
                    " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
                    " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

}
