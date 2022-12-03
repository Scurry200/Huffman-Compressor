/*  Student information for assignment:
 *
 *  On Our honor, Sherwin Amal and Ayaan Nazir, this programming assignment is Our own work
 *  and We have not provided this code to any other student.
 *
 *  Number of slip days used: 2
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: sa53879
 *  email address: sherwinamal@utexas.edu
 *  Grader name: Skyler
 *
 *  Student 2
 *  UTEID: an29256
 *  email address: nazir@utexas.edu
 *
 */
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
     * @param inputStream is input of bits
     * @param viewer is GUI
     * @throws IOException if an error occurs while reading from the input file.
     */
    public HuffmanTree(BitInputStream inputStream, IHuffViewer viewer) throws IOException {
        this.root = buildTree(inputStream, viewer);
    }


    /**
     * pre: none
     * post: should create a tree based on the ascii frequency arrays
     * @param freq is frequency array of ascii
     * constructors to build tree for store count format
     * goes through array to create nodes, and will eventually create a priority queue,
     * that ends up creating a huffman tree, also creates huffman tree map
     */
    public HuffmanTree(int[] freq) {
        PriorityQueue314<TreeNode> queue = new PriorityQueue314<>();
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
     * @param temp is node tree just traversed through
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

    /**
     * Uses tree to write the uncompressed file
     * @param inputStream used for reading bits
     * @param outputStream used for writing bits
     * @param view is GUI
     * @return bits written
     * @throws IOException if an error occurs while reading/writing from the input/output file.
     */
    public int writeTree(BitInputStream inputStream, BitOutputStream outputStream,
                         IHuffViewer view)
        throws IOException {
        TreeNode node = getTree();
        boolean done = false;
        int count = 0;
        while (!done) {
            int bit = inputStream.readBits(1);
            if (bit == -1) {
                view.showError("NO PSEUDO VALUE FOUND, REACHED END OF FILE");
                throw new IOException("Error reading compressed file. \n" +
                    "unexpected end of input. No PSEUDO_EOF value.");
            } else {
                node = (bit == 0) ? node.getLeft() : node.getRight();
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
        showString("done with writing tree", view);
        return count;
    }

    /**
     * getter for tree
     * @return the root of tree
     */
    public TreeNode getTree() {
        return root;
    }

    /**
     * getter for map
     * @return the map
     */
    public HashMap<Integer, String> getMap() {
        return huffMap;
    }

    /**
     * @param viewer is GUI
     * @param inputStream is current bits to read
     * pre: none
     * post: should create a tree based on inputstream codes
     * builds a tree based on the input stream of huffman codes
     * @return huffman tree from inputstream of codes
     * @throws IOException if an error occurs while reading from the input file.
     */
    private TreeNode buildTree(BitInputStream inputStream, IHuffViewer viewer) throws IOException {
        int bit = inputStream.readBits(1);
        if (bit == 0) {
            return new TreeNode(buildTree(inputStream, viewer), -1,
                buildTree(inputStream, viewer));
        } else if (bit == 1) {
            int leafVal = inputStream.readBits(1 + IHuffConstants.BITS_PER_WORD);
            return new TreeNode(leafVal, -1);
        } else {
            viewer.showError("issue with reading");
            //only used whenever tree cannot be built
            throw new IllegalArgumentException("view gui, file cannot be read");
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
     * Searches through the tree for the correct bit sequence
     * @param node branch being traversed through
     * @param val string creating path
     * @param bit value being searched for
     * @return String containing bit sequence
     */
    private String get(TreeNode node, String val, int bit) {
        if (node == null) {
            return null;
        }
        if (node.getValue() == bit) {
            return val;
        }
        String temp = get(node.getLeft(), val + "0", bit);
        if (temp != null) {
            return temp;
        }
        return get(node.getRight(), val + "1", bit);
    }

    /**
     * pre: none
     * shows a message on viewer
     * @param s is what needs to be shown
     * @param myViewer is viewer gui
     */
    private void showString(String s, IHuffViewer myViewer) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
