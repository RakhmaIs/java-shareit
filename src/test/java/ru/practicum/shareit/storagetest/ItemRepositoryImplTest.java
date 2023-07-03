package ru.practicum.shareit.storagetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ItemRepositoryImplTest {
    ItemRepositoryImpl itemRepository;
    Item item;
    Item item2;
    User user;
    Item itemForUpdate;

    @BeforeEach
    void setup() {
        itemRepository = new ItemRepositoryImpl();

        user = User.builder()
                .name("TestUser")
                .email("testuser@mail.ru")
                .id(1L)
                .build();

        item = Item.builder()
                .name("testItem")
                .id(1L)
                .available(true)
                .ownerId(1L)
                .description("QWERTY")
                .build();

        item2 = Item.builder()
                .name("testItem")
                .id(2L)
                .available(true)
                .ownerId(1L)
                .description("This item needs only for test")
                .build();

        itemForUpdate = Item.builder()
                .name("testUpdate")
                .id(1L)
                .available(true)
                .ownerId(1L)
                .description("This item needs only for updateTest")
                .build();

    }

    @Test
    void createItemShouldAddItemInMap() {
        itemRepository.createItem(item, 1L);
        assertThat(itemRepository.readAll(1L).size()).isEqualTo(1);
    }

    @Test
    void updateItemShouldUpdateItemInMap() {
        itemRepository.createItem(item, 1L);
        assertThat(itemRepository.readById(item.getId(), user.getId()).getName()).isEqualTo("testItem");
        itemRepository.updateItem(itemForUpdate, user.getId(), item.getId());
        assertThat(itemRepository.readById(itemForUpdate.getId(), user.getId()).getName()).isEqualTo("testUpdate");
    }

    @Test
    void readAllShouldReturnListOfItems() {
        itemRepository.createItem(item, 1L);
        itemRepository.createItem(item2, 1L);
        assertThat(itemRepository.readAll(user.getId()).size()).isEqualTo(2);

    }

    @Test
    void deleteItemShouldDeleteItemFromMap() {
        itemRepository.createItem(item, 1L);
        assertThat(itemRepository.readAll(user.getId()).size()).isEqualTo(1);
        itemRepository.deleteById(item.getId());
        assertThat(itemRepository.readAll(user.getId()).size()).isEqualTo(0);
    }

    @Test
    void searchShouldReturnItems() {
        itemRepository.createItem(item, 1L);
        itemRepository.createItem(item2, 1L);
        assertThat(itemRepository.search("QWERTY").size()).isEqualTo(1);
    }
}
