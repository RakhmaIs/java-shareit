package ru.practicum.shareit.request.repositorytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("test")
                .email("test@mail.com")
                .build();
        User user = entityManager.persist(owner);

        User owner2 = User.builder()
                .name("test")
                .email("test2@mail.com")
                .build();
        User user1 = entityManager.persist(owner2);

        ItemRequest itemRequest = ItemRequest.builder()
                .requesterId(user.getId())
                .created(LocalDateTime.now())
                .description("test")
                .build();

        entityManager.persist(itemRequest);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .requesterId(user1.getId())
                .created(LocalDateTime.now())
                .description("test")
                .build();

        entityManager.persist(itemRequest1);
    }

    @Test
    void findItemRequestByIdShouldNotBeEmptyIfRequestExists() {
        List<ItemRequest> all = itemRequestRepository.findAll();
        Optional<ItemRequest> itemRequestById = itemRequestRepository.findItemRequestById(all.get(0).getId());
        assertTrue(itemRequestById.isPresent());
    }

    @Test
    void findItemRequestByIdShouldBeEmptyWhenRequestNoExists() {
        assertTrue(itemRequestRepository.findItemRequestById(0L).isEmpty());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDescShouldReturnValidListSize() {
        List<ItemRequest> allByRequesterIdOrderByCreatedDesc = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(1L);
        assertEquals(1, allByRequesterIdOrderByCreatedDesc.size());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_whenInvoked_thenHaveEmptyList() {
        List<ItemRequest> allByRequesterIdOrderByCreatedDesc = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(0L);
        assertEquals(0, allByRequesterIdOrderByCreatedDesc.size());
    }

    @Test
    void findAllByRequesterIdNot_whenInvoked_thenDontHaveIdRequestor() {
        List<ItemRequest> all = itemRequestRepository.findAll();
        List<ItemRequest> collect = itemRequestRepository
                .findAllByRequesterIdNot(all.get(0).getId(), Pagination.getPaginationWithoutSort(0, 1))
                .stream().collect(Collectors.toList());

        assertNotSame(collect.get(0).getId(), all.get(0).getId());
    }
}
