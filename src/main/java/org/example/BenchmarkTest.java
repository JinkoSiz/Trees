package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

@Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkTest {

    // ------------------ B-дерево ------------------
    @State(Scope.Benchmark)
    public static class BTreeState {
        public List<Integer> data;         // исходные данные из CSV
        public BTree baseTree;             // базовое дерево, в которое уже вставлены все данные
        public List<Integer> searchKeys;   // список ключей для поиска (копия data)
        public List<Integer> insertKeys;   // список ключей для вставки (ключи > max(data))
        public List<Integer> deleteKeys;   // список ключей для удаления (копия data)
        public int searchIndex;            // текущий индекс для поиска
        public int insertIndex;            // текущий индекс для вставки
        public int deleteIndex;            // текущий индекс для удаления

        @Setup(Level.Trial)
        public void setUp() throws Exception {
            List<String> lines = Files.readAllLines(Paths.get("data/pokemon.csv"), StandardCharsets.UTF_8);
            data = new ArrayList<>();
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
                    int value = (int) Double.parseDouble(parts[attackIndex].trim());
                    data.add(value);
                }
            }
            baseTree = new BTree(3);
            for (Integer key : data) {
                baseTree.insert(key);
            }
            searchKeys = new ArrayList<>(data);
            deleteKeys = new ArrayList<>(data);
            int max = Collections.max(data);
            insertKeys = new ArrayList<>();
            for (int i = 1; i <= data.size(); i++) {
                insertKeys.add(max + i);
            }
            searchIndex = 0;
            insertIndex = 0;
            deleteIndex = 0;
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkBTreeSearchSingle(BTreeState state) {
        int key = state.searchKeys.get(state.searchIndex);
        state.searchIndex = (state.searchIndex + 1) % state.searchKeys.size();
        state.baseTree.search(key);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkBTreeInsertSingle(BTreeState state) {
        int key = state.insertKeys.get(state.insertIndex);
        state.insertIndex = (state.insertIndex + 1) % state.insertKeys.size();
        state.baseTree.insert(key);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkBTreeDeleteSingle(BTreeState state) {
        int key = state.deleteKeys.get(state.deleteIndex);
        state.deleteIndex = (state.deleteIndex + 1) % state.deleteKeys.size();
        state.baseTree.delete(key);
    }

    // ------------------ k-d дерево ------------------
    @State(Scope.Benchmark)
    public static class KDTreeState {
        public List<Point> points;         // исходные точки, загруженные из CSV
        public KDTree baseTree;            // базовое k-d дерево, в которое уже вставлены все точки
        public List<Point> searchPoints;   // список точек для поиска k-NN
        public List<Point> insertPoints;   // список точек для вставки
        public int searchIndex;            // текущий индекс для поиска
        public int insertIndex;            // текущий индекс для вставки
        public int k = 5;
        public Point query;

        @Setup(Level.Trial)
        public void setUp() throws Exception {
            List<String> lines = Files.readAllLines(Paths.get("data/cancer.csv"), StandardCharsets.UTF_8);
            points = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        double x = Double.parseDouble(parts[2].trim());
                        double y = Double.parseDouble(parts[3].trim());
                        points.add(new Point(x, y));
                    }
                }
            }
            baseTree = new KDTree();
            for (Point p : points) {
                baseTree.insert(p);
            }
            searchPoints = new ArrayList<>(points);
            insertPoints = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                double x = (i % 1000) * 1.0;
                double y = ((i + 2) % 1000) * 1.0;
                insertPoints.add(new Point(x, y));
            }
            searchIndex = 0;
            insertIndex = 0;
            query = new Point(500, 500);
        }
    }

    @Benchmark
    public void benchmarkKDTreeKNNSingle(KDTreeState state) {
        Point p = state.searchPoints.get(state.searchIndex);
        state.searchIndex = (state.searchIndex + 1) % state.searchPoints.size();
        state.baseTree.findKNearest(p, state.k);
    }

    @Benchmark
    public void benchmarkKDTreeInsertSingle(KDTreeState state) {
        Point p = state.insertPoints.get(state.insertIndex);
        state.insertIndex = (state.insertIndex + 1) % state.insertPoints.size();
        state.baseTree.insert(p);
    }

    @Benchmark
    public void benchmarkKDTreeBuildSingle(KDTreeState state) {
        KDTree tree = new KDTree();
        for (Point p : state.points) {
            tree.insert(p);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
