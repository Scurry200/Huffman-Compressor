import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueueTester {
    public static void main(String[] args) throws IOException {
        char[] ascii = new char[257];
        BitInputStream bits = new BitInputStream("C:\\Users\\sherw\\Downloads\\ciaFactBook2008.txt");
        //Reads file data and puts int an array of ints where the index corresponds to character and value to frequency
        int read = 0;
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                ascii[read]++;
            }
        }
        //PEOF value
        ascii[256] = 1;
        PriorityQueue314 queue = new PriorityQueue314();
        //Adds each value that is in the file into a queue
        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != 0) {
                queue.add(new TreeNode(i, ascii[i]));
            }
        }
        //Converts queue into a tree
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, -1, right));
        }
        //Final tree made from queue
        TreeNode root = queue.poll();
        //Used for HashMap
        ArrayList<TreeNode> nodes = new ArrayList<>();
        inOrder(root, nodes);
        HashMap<Integer, String> map = new HashMap<>();
        //Uses placement in tree to create a map of values and location in tree
        for (TreeNode node : nodes) {
            map.put(node.getValue(), getByte(root, node));
        }
        bits.reset();
        read = 0;
        String binary = "";
        //Converts the new compressed data back to uncompressed
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                binary += map.get(read);
            }
        }
        //Adds PEOF value
        binary += map.get(256);
        StringBuilder decode = help(root, binary);
        System.out.println(decode.toString());
    }

    //Converts the compressed bits into comprehensible characters
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

    //Puts each node of the tree in an ArrayList based on the order of the text
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

    //Converts each node in tree into a bit reference
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