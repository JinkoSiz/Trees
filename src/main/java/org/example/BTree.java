package org.example;
import java.util.*;

public class BTree {
    class Node {
        int n;                // число ключей в узле
        int[] keys;           // массив ключей
        Node[] children;      // массив дочерних узлов
        boolean leaf;         // является ли узел листом

        Node(boolean leaf) {
            this.leaf = leaf;
            keys = new int[2 * t - 1];
            children = new Node[2 * t];
            n = 0;
        }
    }

    private int t;
    private Node root;

    public BTree(int t) {
        this.t = t;
        root = new Node(true);
    }

    // Поиск ключа в B-дереве
    public Node search(int key) {
        return search(root, key);
    }

    private Node search(Node node, int key) {
        int i = 0;
        while (i < node.n && key > node.keys[i]) {
            i++;
        }
        if (i < node.n && node.keys[i] == key) {
            return node;
        }
        if (node.leaf) {
            return null;
        }
        return search(node.children[i], key);
    }

    public void insert(int key) {
        Node r = root;
        if (r.n == 2 * t - 1) {
            Node s = new Node(false);
            root = s;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    private void insertNonFull(Node node, int key) {
        int i = node.n - 1;
        if (node.leaf) {
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.n++;
        } else {
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            if (node.children[i].n == 2 * t - 1) {
                splitChild(node, i, node.children[i]);
                if (key > node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key);
        }
    }

    private void splitChild(Node parent, int i, Node fullChild) {
        Node newChild = new Node(fullChild.leaf);
        newChild.n = t - 1;
        for (int j = 0; j < t - 1; j++) {
            newChild.keys[j] = fullChild.keys[j + t];
        }
        if (!fullChild.leaf) {
            for (int j = 0; j < t; j++) {
                newChild.children[j] = fullChild.children[j + t];
            }
        }
        fullChild.n = t - 1;
        for (int j = parent.n; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = newChild;
        for (int j = parent.n - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }
        parent.keys[i] = fullChild.keys[t - 1];
        parent.n++;
    }

    public void delete(int key) {
        delete(root, key);
        if (root.n == 0 && !root.leaf) {
            root = root.children[0];
        }
    }

    private void delete(Node node, int key) {
        int idx = findKey(node, key);
        if (idx < node.n && node.keys[idx] == key) {
            if (node.leaf) {
                for (int i = idx; i < node.n - 1; i++) {
                    node.keys[i] = node.keys[i + 1];
                }
                node.n--;
            } else {
                deleteInternalNode(node, key, idx);
            }
        } else {
            if (node.leaf) {
                return;
            }
            boolean flag = (idx == node.n);
            if (node.children[idx].n < t) {
                fill(node, idx);
            }
            if (flag && idx > node.n) {
                delete(node.children[idx - 1], key);
            } else {
                delete(node.children[idx], key);
            }
        }
    }

    private int findKey(Node node, int key) {
        int idx = 0;
        while (idx < node.n && node.keys[idx] < key) {
            idx++;
        }
        return idx;
    }

    private void deleteInternalNode(Node node, int key, int idx) {
        if (node.children[idx].n >= t) {
            int pred = getPredecessor(node, idx);
            node.keys[idx] = pred;
            delete(node.children[idx], pred);
        } else if (node.children[idx + 1].n >= t) {
            int succ = getSuccessor(node, idx);
            node.keys[idx] = succ;
            delete(node.children[idx + 1], succ);
        } else {
            merge(node, idx);
            delete(node.children[idx], key);
        }
    }

    private int getPredecessor(Node node, int idx) {
        Node cur = node.children[idx];
        while (!cur.leaf) {
            cur = cur.children[cur.n];
        }
        return cur.keys[cur.n - 1];
    }

    private int getSuccessor(Node node, int idx) {
        Node cur = node.children[idx + 1];
        while (!cur.leaf) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    private void fill(Node node, int idx) {
        if (idx != 0 && node.children[idx - 1].n >= t) {
            borrowFromPrev(node, idx);
        } else if (idx != node.n && node.children[idx + 1].n >= t) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.n) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(Node node, int idx) {
        Node child = node.children[idx];
        Node sibling = node.children[idx - 1];
        for (int i = child.n - 1; i >= 0; i--) {
            child.keys[i + 1] = child.keys[i];
        }
        if (!child.leaf) {
            for (int i = child.n; i >= 0; i--) {
                child.children[i + 1] = child.children[i];
            }
        }
        child.keys[0] = node.keys[idx - 1];
        if (!child.leaf) {
            child.children[0] = sibling.children[sibling.n];
        }
        node.keys[idx - 1] = sibling.keys[sibling.n - 1];
        child.n++;
        sibling.n--;
    }

    private void borrowFromNext(Node node, int idx) {
        Node child = node.children[idx];
        Node sibling = node.children[idx + 1];
        child.keys[child.n] = node.keys[idx];
        if (!child.leaf) {
            child.children[child.n + 1] = sibling.children[0];
        }
        node.keys[idx] = sibling.keys[0];
        for (int i = 0; i < sibling.n - 1; i++) {
            sibling.keys[i] = sibling.keys[i + 1];
        }
        if (!sibling.leaf) {
            for (int i = 0; i < sibling.n; i++) {
                sibling.children[i] = sibling.children[i + 1];
            }
        }
        child.n++;
        sibling.n--;
    }

    private void merge(Node node, int idx) {
        Node child = node.children[idx];
        Node sibling = node.children[idx + 1];
        child.keys[t - 1] = node.keys[idx];
        for (int i = 0; i < sibling.n; i++) {
            child.keys[i + t] = sibling.keys[i];
        }
        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; i++) {
                child.children[i + t] = sibling.children[i];
            }
        }
        for (int i = idx + 1; i < node.n; i++) {
            node.keys[i - 1] = node.keys[i];
        }
        for (int i = idx + 2; i <= node.n; i++) {
            node.children[i - 1] = node.children[i];
        }
        child.n += sibling.n + 1;
        node.n--;
    }
}
