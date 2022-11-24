import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueueTester {
    public static void main(String[] args) throws IOException {
        char[] ascii = new char[257];
        BitInputStream bits = new BitInputStream("smallTxt.txt");
        int read = 0;
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                ascii[read]++;
            }
        }
        ascii[256] = 1;
        PriorityQueue314 queue = new PriorityQueue314();
        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != 0) {
                queue.add(new TreeNode(i, ascii[i]));
            }
        }
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, -1, right));
        }
        TreeNode root = queue.poll();
        HashMap<TreeNode, String> map = new HashMap<>();
        inOrder(root, map);
        for (TreeNode node : map.keySet()) {
            map.put(node, getByte(root, node));
        }
        System.out.println(queue);
    }

    private static void inOrder(TreeNode node, HashMap<TreeNode, String> map) {
        if (node.getLeft() != null) {
            inOrder(node.getLeft(), map);
        }
        if (node.getValue() != -1) {
            map.put(node, "");
        }
        if (node.getRight() != null) {
            inOrder(node.getRight(), map);
        }
    }

    private static String getByte(TreeNode root, TreeNode node) {
        return get(root, new String(), node.getValue());
    }

    private static String get(TreeNode node, String val, int bite) {
        if (node == null) {
            return null;
        }
        if (node.getValue() == bite) {
            return val;
        }
        String temp = get(node.getLeft(), val + "0", bite);
        if (temp != null) {
            return temp;
        }
        return get(node.getRight(), val + "1", bite);
    }
}