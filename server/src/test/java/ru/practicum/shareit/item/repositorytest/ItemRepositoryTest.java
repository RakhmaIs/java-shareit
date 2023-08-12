package ru.practicum.shareit.item.repositorytest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager entityManager;

    Item testItem;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("test")
                .email("test@mail.com")
                .build();
        entityManager.persist(owner);

        Item firstItem = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .build();
        testItem = entityManager.persist(firstItem);

        ItemRequest itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .requesterId(1L)
                .description("Description")
                .build();

        entityManager.persist(itemRequest);

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .requestId(1L)
                .owner(owner)
                .build();
        entityManager.persist(item2);
    }

    @Test
    void searchPage_whenInvoked_thenHaveListOneElement() {
        List<Item> description = itemRepository.search("Description", Pagination.getPaginationWithoutSort(0, 2))
                .stream().collect(Collectors.toList());

        assertEquals(1, description.size());
    }

    @Test
    void findByIdFull_whenInvoked_thenHaveItemById() {
        Optional<Item> byIdFull = itemRepository.findById(1L);

        assertTrue(byIdFull.isPresent());
        assertThat(byIdFull.get()).isEqualTo(testItem);
    }

    @Test
    void findByIdFull_whenInvokedNotExistId_thenEmpty() {
        Optional<Item> byIdFull = itemRepository.findById(0L);

        assertTrue(byIdFull.isEmpty());
    }

    @Test
    void findAllByRequestId_whenInvoked_thenListHaveOneItem() {
        List<Item> allByRequestId = itemRepository.findAllByRequestId(1L);

        assertThat(allByRequestId.size()).isEqualTo(1);
    }

    @Test
    void findAllByRequestId_whenInvokedNotExistRequest_thenListEmpty() {
        List<Item> allByRequestId = itemRepository.findAllByRequestId(0L);

        assertThat(allByRequestId.size()).isEqualTo(0);
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc_whenInvoked_thenListHave2Items() {
        Page<Item> allByOwnerIdOrderByIdAsc = itemRepository
                .findItemsByOwnerIdOrderByIdAsc(1L, Pagination.getPaginationWithoutSort(0, 2));
        assertThat(allByOwnerIdOrderByIdAsc.getContent().size()).isEqualTo(2);
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc_whenInvoked_thenListEmpty() {
        Page<Item> allByOwnerIdOrderByIdAsc = itemRepository
                .findItemsByOwnerIdOrderByIdAsc(0L, Pagination.getPaginationWithoutSort(0, 2));
        assertThat(allByOwnerIdOrderByIdAsc.getContent().size()).isEqualTo(0);
    }
}