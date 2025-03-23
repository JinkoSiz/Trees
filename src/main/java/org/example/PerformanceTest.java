package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;

public class PerformanceTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Запуск тестов производительности B-дерева...");
        List<Integer> pokemonData = loadPokemonData("data/pokemon.csv");
        BTree btree = createBTreeFromPokemonData(pokemonData);
        testBTreeSearch(btree, pokemonData);
        testBTreeInsert(btree, pokemonData);
        testBTreeDelete(btree, pokemonData);
        System.out.println("B-дерево: тесты завершены.");

        System.out.println("Запуск тестов производительности k-d дерева...");
        List<Point> cancerPoints = loadCancerData("data/cancer.csv");
        KDTree kdtree = createKDTreeFromCancerData(cancerPoints);
        testKDTreeFindKNearest(kdtree, cancerPoints);
        testKDTreeInsert(kdtree);
        System.out.println("k-d дерево: тесты завершены.");
    }

    private static List<Integer> loadPokemonData(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        List<Integer> data = new ArrayList<>();
        if (lines.isEmpty()) {
            System.out.println("Файл " + filePath + " пустой или не найден.");
            return data;
        }
        String header = lines.get(0);
        String[] headerColumns = header.split(",");
        int attackIndex = -1;
        for (int i = 0; i < headerColumns.length; i++) {
            if (headerColumns[i].trim().equalsIgnoreCase("Attack")) {
                attackIndex = i;
                break;
            }
        }
        if (attackIndex == -1) {
            attackIndex = 0;
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.trim().isEmpty()) {
                String[] parts = line.split(",");
                try {
                    int value = (int) Double.parseDouble(parts[attackIndex].trim());
                    data.add(value);
                } catch (NumberFormatException e) {
                }
            }
        }
        return data;
    }

    private static BTree createBTreeFromPokemonData(List<Integer> data) {
        BTree btree = new BTree(3);
        for (Integer key : data) {
            btree.insert(key);
        }
        return btree;
    }

    private static void testBTreeSearch(BTree btree, List<Integer> data) {
        int n = data.size();
        for (int i = 0; i < 100000; i++) {
            int key = data.get(i % n);
            btree.search(key);
        }
    }

    private static void testBTreeInsert(BTree btree, List<Integer> data) {
        int max = Collections.max(data);
        for (int i = 0; i < 10000; i++) {
            btree.insert(max + i + 1);
        }
    }

    private static void testBTreeDelete(BTree btree, List<Integer> data) {
        int n = data.size();
        for (int i = 0; i < 5000; i++) {
            int key = data.get(i % n);
            btree.delete(key);
        }
    }

    private static List<Point> loadCancerData(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        List<Point> points = new ArrayList<>();
        if (lines.isEmpty()) {
            System.out.println("Файл " + filePath + " пустой или не найден.");
            return points;
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        double x = Double.parseDouble(parts[2].trim());
                        double y = Double.parseDouble(parts[3].trim());
                        points.add(new Point(x, y));
                    } catch (NumberFormatException e) {
                        // Игнорируем некорректные данные
                    }
                }
            }
        }
        return points;
    }

    private static KDTree createKDTreeFromCancerData(List<Point> points) {
        KDTree kdtree = new KDTree();
        for (Point p : points) {
            kdtree.insert(p);
        }
        return kdtree;
    }

    private static void testKDTreeFindKNearest(KDTree kdtree, List<Point> points) {
        int n = points.size();
        int k = 5;
        for (int i = 0; i < 100000; i++) {
            Point target = points.get(i % n);
            kdtree.findKNearest(target, k);
        }
    }

    private static void testKDTreeInsert(KDTree kdtree) {
        for (int i = 0; i < 10000; i++) {
            double x = (i % 1000) * 1.0;
            double y = ((i + 1) % 1000) * 1.0;
            kdtree.insert(new Point(x, y));
        }
    }
}
