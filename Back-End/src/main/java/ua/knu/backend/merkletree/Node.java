package ua.knu.backend.merkletree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private Node left;
    private Node right;
    private String hash;

    public Node(Node left, Node right, String hash) {
        this.left = left;
        this.right = right;
        this.hash = hash;
    }
}