package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemDto, Long> {
    public List<ItemDto> findItemsByOwnerIdOrderByIdAsc(Long userId);

    @Query("SELECT i FROM ItemDto i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.available = true" +
            " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<ItemDto> search(String text);


}
