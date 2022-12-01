import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class HuffmanTree {
    private TreeNode root;
    private HashMap<Integer, String> huffMap;

    public HuffmanTree(BitInputStream inputStream) throws IOException {
        this.root = buildTree(inputStream);
    }

    public HuffmanTree(int[] freq) {
        PriorityQueue314 queue = new PriorityQueue314();
        LinkedList<TreeNode> nodes = new LinkedList<>();
        huffMap = new HashMap<>();
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] != 0) {
                nodes.add(new TreeNode(i, freq[i]));
                queue.add(nodes.getLast());
            }
        }
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, -1, right));
        }
        root = queue.poll();
        fillMap(nodes);
    }

    private TreeNode buildTree(BitInputStream inputStream) throws IOException {
        int bit = inputStream.readBits(1);
        if (bit == 0) {
            TreeNode nodeInternal = new TreeNode(buildTree(inputStream), -1, buildTree(inputStream));
            //nodeInternal.setLeft(buildTree(inputStream));
            //nodeInternal.setRight(buildTree(inputStream));
            return nodeInternal;
        } else if (bit == 1) {
            int leafVal = inputStream.readBits(9);
            TreeNode nodeLeaf = new TreeNode(leafVal, -1);
            return nodeLeaf;
        } else {
            throw new IllegalArgumentException("error with reading");
        }
    }

    private void inOrder(TreeNode node, LinkedList<TreeNode> list) {
        if (node.getLeft() != null) {
            inOrder(node.getLeft(), list);
        }
        if (node.isLeaf()) {
            list.add(node);
        }
        if (node.getRight() != null) {
            inOrder(node.getRight(), list);
        }
    }

    private void fillMap(LinkedList<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            huffMap.put(node.getValue(), get(root, "", node.getValue()));
        }
    }

    private String get(TreeNode node, String val, int bite) {
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

    public int goThroughTreeToWrite(BitInputStream inputStream, BitOutputStream outputStream)
        throws IOException {
        TreeNode node = getTree();
        boolean done = false;
        int count = 0;
        while (!done) {
            int bit = inputStream.readBits(1);
            if (bit == -1) {
                throw new IOException("Error reading compressed file. \n" +
                    "unexpected end of input. No PSEUDO_EOF value.");
            } else {
                if (bit == 0) {
                    node = node.getLeft();
                } else if (bit == 1) {
                    node = node.getRight();
                }
                if (node.isLeaf()) {
                    if (node.getValue() == IHuffConstants.ALPH_SIZE) {
                        done = true;
                    } else {
                        outputStream.writeBits(IHuffConstants.BITS_PER_WORD, node.getValue());
                        count += IHuffConstants.BITS_PER_WORD;
                        node = getTree();
                    }
                }
            }
        }
        return count;
    }




    public TreeNode getTree() {
        return root;
    }

    public HashMap<Integer, String> getMap() {
        return huffMap;
    }

    public void setTree(TreeNode node) {
        root = node;
    }


}
