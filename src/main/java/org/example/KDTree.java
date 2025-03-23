package org.example;

public class KDTree {
    private class Node {
        Point point;
        Node left, right;
        int axis; // 0 для x, 1 для y

        Node(Point point, int axis) {
            this.point = point;
            this.axis = axis;
        }
    }

    private Node root;

    // Вставка точки в k-d дерево
    public void insert(Point point) {
        root = insertRec(root, point, 0);
    }

    private Node insertRec(Node node, Point point, int depth) {
        if (node == null) {
            return new Node(point, depth % 2);
        }
        int axis = node.axis;
        if ((axis == 0 && point.x < node.point.x) ||
                (axis == 1 && point.y < node.point.y)) {
            node.left = insertRec(node.left, point, depth + 1);
        } else {
            node.right = insertRec(node.right, point, depth + 1);
        }
        return node;
    }

    private class PointDistance {
        Point point;
        double distance;

        PointDistance(Point point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    public java.util.List<Point> findKNearest(Point target, int k) {
        java.util.PriorityQueue<PointDistance> pq =
                new java.util.PriorityQueue<>((pd1, pd2) -> Double.compare(pd2.distance, pd1.distance));
        searchKNN(root, target, k, pq);
        java.util.List<Point> result = new java.util.ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll().point);
        }
        java.util.Collections.reverse(result);
        return result;
    }

    private void searchKNN(Node node, Point target, int k, java.util.PriorityQueue<PointDistance> pq) {
        if (node == null) {
            return;
        }
        double dist = distance(node.point, target);
        if (pq.size() < k) {
            pq.offer(new PointDistance(node.point, dist));
        } else if (dist < pq.peek().distance) {
            pq.poll();
            pq.offer(new PointDistance(node.point, dist));
        }
        int axis = node.axis;
        Node nearChild, farChild;
        if ((axis == 0 && target.x < node.point.x) ||
                (axis == 1 && target.y < node.point.y)) {
            nearChild = node.left;
            farChild = node.right;
        } else {
            nearChild = node.right;
            farChild = node.left;
        }
        searchKNN(nearChild, target, k, pq);
        double diff = (axis == 0) ? Math.abs(target.x - node.point.x)
                : Math.abs(target.y - node.point.y);
        if (pq.size() < k || diff < pq.peek().distance) {
            searchKNN(farChild, target, k, pq);
        }
    }

    private double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
