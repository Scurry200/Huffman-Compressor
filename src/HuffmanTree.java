import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
/**
    Huffman tree class that builds a huffman tree or goes through the tree to interpret tree data
 */

public class HuffmanTree {
    private TreeNode root;
    private HashMap<Integer, String> huffMap;

    /**
     * pre: none
     * post: should create a tree based in the inputstream of characters
     * constructors to build tree for store tree format
     */
    public HuffmanTree(BitInputStream inputStream) throws IOException {
        this.root = buildTree(inputStream);
    }


    /**
     * pre: none
     * post: should create a tree based on the ascii frequency arrays
     * constructors to build tree for store count format
     * goes through array to create nodes, and will eventually create a priority queue,
     * that ends up creating a huffman tree, also creates huffman tree map
     */

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

    /**
     * pre: none
     * post: should create a tree based on inputstream codes
     * builds a tree based on the input stream of huffman codes
     * @return huffman tree from inputstream of codes
     */
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

    /**
     * pre: none
     * post: should get a linkedlist of all the nodes in tree
     * goes through huffman tree and adds each node into list
     */

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

    /**
     * pre: none
     * post: should create a map with all the node values with huffman codes
     * goes through list of nodes to find the huffman codes for each node in tree
     */

    private void fillMap(LinkedList<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            huffMap.put(node.getValue(), get(root, "", node.getValue()));
        }
    }

    /**
     * pre: none
     * post: should calculate the number of bits that is in the huffman tree
     * method meant for calculating the number of bits that will be in the compressed file
     * whenever format is supposed to be store tree
     * @return bits in the binary tree from the root to the leaves
     */
    public int goingThroughTree(TreeNode temp) {
        //goes through tree finding total bits
        if (temp.isLeaf()) {
            return 1 + IHuffConstants.BITS_PER_WORD + 1; //1 for node, 9 for bits per leaf node
        } else {
            return 1 + goingThroughTree(temp.getLeft()) + goingThroughTree(temp.getRight());
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
