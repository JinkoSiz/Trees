package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BTreeTest {

    @Test
    public void testInsertAndSearch() {
        BTree bTree = new BTree(3);

        // Вставляем элементы в B-дерево
        bTree.insert(10);
        bTree.insert(20);
        bTree.insert(5);

        // Проверяем, что элементы можно найти
        assertNotNull(bTree.search(10), "Key 10 should be found in the BTree");
        assertNotNull(bTree.search(20), "Key 20 should be found in the BTree");
        assertNotNull(bTree.search(5), "Key 5 should be found in the BTree");

        // Проверяем, что элемента, которого нет в дереве, не существует
        assertNull(bTree.search(100), "Key 100 should not be found in the BTree");
    }

    @Test
    public void testDelete() {
        BTree bTree = new BTree(3);

        // Вставляем элементы в B-дерево
        bTree.insert(10);
        bTree.insert(20);
        bTree.insert(5);

        // Удаляем элемент
        bTree.delete(10);

        // Проверяем, что элемент был удалён
        assertNull(bTree.search(10), "Key 10 should not be found in the BTree after deletion");
        assertNotNull(bTree.search(20), "Key 20 should still be found in the BTree");
    }

    @Test
    public void testInsertAndDeleteMultiple() {
        BTree bTree = new BTree(3);

        // Вставляем несколько элементов
        bTree.insert(10);
        bTree.insert(20);
        bTree.insert(5);
        bTree.insert(6);
        bTree.insert(30);
        bTree.insert(50);

        // Удаляем элементы
        bTree.delete(10);
        bTree.delete(30);

        // Проверяем наличие элементов после удаления
        assertNull(bTree.search(10), "Key 10 should be deleted from the BTree");
        assertNull(bTree.search(30), "Key 30 should be deleted from the BTree");
        assertNotNull(bTree.search(20), "Key 20 should still be in the BTree");
    }
}
