package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    public List<Item> findItemsByOwnerIdOrderByIdAsc(Long userId);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.available = true" +
            " AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);


}
