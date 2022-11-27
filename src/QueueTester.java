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
        ArrayList<TreeNode> nodes = new ArrayList<>();
        inOrder(root, nodes);
        HashMap<Integer, String> map = new HashMap<>();
        for (TreeNode node : nodes) {
            map.put(node.getValue(), getByte(root, node));
        }
        bits.reset();
        read = 0;
        String binary = "";
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                binary += map.get(read);
            }
        }
        binary += map.get(256);
        StringBuilder decode = help(root, binary);
        System.out.println(decode.toString());
    }


    private static StringBuilder help(TreeNode root, String val) {
        StringBuilder result = new StringBuilder();
        String str = val;
        TreeNode temp = root;
        while (str.length() != 0) {
            if (temp.getValue() != -1) {
                result.append((char) (temp.getValue()));
                temp = root;
            } else if (str.charAt(0) == '0') {
                temp = temp.getLeft();
                str = str.substring(1);
            } else if (str.charAt(0) == '1') {
                temp = temp.getRight();
                str = str.substring(1);
            }
        }
        result.append((char) (temp.getValue()));
        return result;
    }

    private static void inOrder(TreeNode node, ArrayList<TreeNode> list) {
        if (node.getLeft() != null) {
            inOrder(node.getLeft(), list);
        }
        if (node.getValue() != -1) {
            list.add(node);
        }
        if (node.getRight() != null) {
            inOrder(node.getRight(), list);
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