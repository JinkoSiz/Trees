package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class KDTreeTest {

    @Test
    public void testInsertAndFindKNearest() {
        KDTree kdTree = new KDTree();

        // Вставляем несколько точек
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(5, 6);
        Point p4 = new Point(7, 8);
        Point p5 = new Point(2, 1);
        Point p6 = new Point(4, 3);

        kdTree.insert(p1);
        kdTree.insert(p2);
        kdTree.insert(p3);
        kdTree.insert(p4);
        kdTree.insert(p5);
        kdTree.insert(p6);

        // Ищем 3 ближайших соседа для точки (3, 3)
        Point query = new Point(3, 3);
        int k = 3;
        List<Point> neighbors = kdTree.findKNearest(query, k);

        // Проверяем, что ближайшие соседи содержат ожидаемые точки
        assertEquals(3, neighbors.size(), "Should return 3 nearest neighbors");

        // Сортировка результатов по ожидаемым расстояниям
        assertTrue(neighbors.contains(p2), "Result should contain point (3, 4)");
        assertTrue(neighbors.contains(p6), "Result should contain point (4, 3)");
        assertTrue(neighbors.contains(p1) || neighbors.contains(p5), "Result should contain point (1, 2) or (2, 1)");
    }

    @Test
    public void testFindKNearestNoPoints() {
        KDTree kdTree = new KDTree();

        // Ищем ближайших соседей в пустом дереве
        Point query = new Point(3, 3);
        int k = 3;
        List<Point> neighbors = kdTree.findKNearest(query, k);

        // Проверяем, что возвращённый список пустой, так как точек в дереве нет
        assertTrue(neighbors.isEmpty(), "Should return empty list of neighbors");
    }

    @Test
    public void testInsertAndFindSingleNearest() {
        KDTree kdTree = new KDTree();

        // Вставляем точки
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);

        kdTree.insert(p1);
        kdTree.insert(p2);

        // Ищем ближайшую точку для (2, 3)
        Point query = new Point(2, 3);
        int k = 1;
        List<Point> neighbors = kdTree.findKNearest(query, k);

        // Проверяем, что найденный сосед соответствует ожидаемому
        assertEquals(1, neighbors.size(), "Should return only 1 nearest neighbor");
        assertTrue(neighbors.contains(p1) || neighbors.contains(p2), "The nearest point should be either (1, 2) or (3, 4)");
    }
}
