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
import java.util.ArrayList;

/**
 * Class that deals with anything relevant to compressing a file, including preprocess
 */

public class Compress {
    private int[] ascii;
    private int format;
    private int compressedBits;
    private int originalBits;
    private HuffmanTree tree;

    /**
     * @param inputStream helps read file
     * @param  header is header value
     * general constructor for compress object to do preprocess work
     * pre: none
     * post: should be finished with all preprocess completed
     * @throws IOException if an error occurs while reading from the input file.
     */
    public Compress(BitInputStream inputStream, int header, IHuffViewer view)
        throws IOException {
        format = header;
        ascii = new int[IHuffConstants.ALPH_SIZE + 1];
        int read = inputStream.read();
        while (read != -1) {
            ascii[read]++;
            read = inputStream.read();
        }
        showString("finished with frequency array", view);
        ascii[IHuffConstants.ALPH_SIZE] = 1;
        tree = new HuffmanTree(ascii);
        showString("created huffman tree", view);
        findingOriginalBits();
        findingCompressedBits(tree.getTree(), header);
        showString("saved bits are:" + bitsSaved(), view);
    }

    /**
     * pre: none
     * post: returns original bits minus compressed bits
     * simple method to calculate bits saved
     * @return value for what should be returned for compress and preprocesscompress
     */
    public int bitsSaved() {
        return originalBits - compressedBits;
    }

    /**
     * Compresses the data of the file
     * @param inputStream is stream of input data to read
     * @param outputStream helps write file
     * @return total compressed bits
     * @throws IOException if an error occurs while reading/writing from the input/output file.
     */
    public int huff(BitInputStream inputStream, BitOutputStream outputStream, IHuffViewer view)
        throws IOException {
        outputStream.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
        outputStream.writeBits(IHuffConstants.BITS_PER_INT, format);
        if (format == IHuffConstants.STORE_COUNTS) {
            for(int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                outputStream.writeBits(IHuffConstants.BITS_PER_INT, ascii[i]);
            }
            showString("done with store count header writing", view);
        } else if (format == IHuffConstants.STORE_TREE) {
            int sizeInternal = countInternalNodes();
            int count = tree.getMap().size();
            outputStream.writeBits(IHuffConstants.BITS_PER_INT, (sizeInternal +
                (count * (IHuffConstants.BITS_PER_WORD + 2))));
            preOrderHelp(outputStream, tree.getTree());
            showString("done with store tree header writing", view);
        }
        compressData(inputStream, outputStream);
        showString("done with compressing", view);
        showString("compressedBits:" + compressedBits, view);
        outputStream.close();
        return compressedBits;
    }

    /**
     * Reads original data and writes the compressed version of it
     * @param inputStream reads bits from file
     * @param outputStream writes bits to file
     * @throws IOException if an error occurs while reading/writing from the input/output file.
     */
    private void compressData(BitInputStream inputStream, BitOutputStream outputStream)
        throws IOException {
        int read = inputStream.read();
        while (read != -1) {
            sequenceConverting(tree.getMap().get(read), outputStream);
            read = inputStream.read();
        }
        sequenceConverting(tree.getMap().get(IHuffConstants.ALPH_SIZE), outputStream);
    }

    /**
     * Counts the nodes that aren't leaves
     * @return internal nodes
     */
    private int countInternalNodes() {
        ArrayList<TreeNode> nodes = new ArrayList<>();
        tree.preOrder(tree.getTree(), nodes);
        int count = 0;
        for (TreeNode node : nodes) {
            if (node.isLeaf()) {
                count++;
            }
        }
        return nodes.size() - count;
    }

    /**
     * pre: none
     * post: returns format value
     * returns format value
     * @return format value
     */
    public int getFormat() {
        return format;
    }

    /**
     * @param outputStream is stream that writes bits to a file
     * @param huffCode is the huffcode of a node value in the tree
     * pre: none
     * post: goes through a huffcode to write bits for compressed file
     * method meant for writing bit by bit based on huffman code from a tree node
     */
    private void sequenceConverting(String huffCode, BitOutputStream outputStream) {
        for (int i = 0; i < huffCode.length(); i++) {
            outputStream.writeBits(1, Integer.parseInt(huffCode.substring(i, i+1)));
        }
    }

    /**
     * @param outputStream helps write file
     * @param node is the current node that the method traversed through in tree
     * pre: none
     * post: should write all node leaf values that should be in the compressed file, 1 bit for
     * internal nodes, 10 total bits for leaf node data
     * method meant for calculating the number of bits that will be in the compressed file based
     * on huffman codes and format requested
     */
    private void preOrderHelp(BitOutputStream outputStream,
                              TreeNode node) {
        if (node.isLeaf()) {
            outputStream.writeBits(1, 1);
            outputStream.writeBits(IHuffConstants.BITS_PER_WORD + 1, node.getValue());
            //9 bits bc ascii is 256-511, 8 bits for 0-255, 256 is peof
        } else {
            outputStream.writeBits(1, 0);
            preOrderHelp(outputStream, node.getLeft());
            preOrderHelp(outputStream, node.getRight());
            //since huffman is complete tree, no need to check if a child is null
        }
    }

    /**
     * pre: none
     * post: find the original bits in the file
     * method meant for preprocessing for the total bits of original file before tree is created
     */
    private void findingOriginalBits() {
        for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
            originalBits += IHuffConstants.BITS_PER_WORD * ascii[i];
        }
    }

    /** @param root is node that the method traversed through in tree
     * @param header finds header type
     * pre: none
     * post: should calculate the number of bits that should be in the compressed file
     * method meant for calculating the number of bits that will be in the compressed file based
     * on huffman codes and format requested
     */
    private void findingCompressedBits(TreeNode root, int header) {
        compressedBits = IHuffConstants.BITS_PER_INT + IHuffConstants.BITS_PER_INT;
        for (int key: tree.getMap().keySet()) {
            compressedBits += ascii[key] * tree.getMap().get(key).length();
        }
        if (header == IHuffConstants.STORE_COUNTS) {
            compressedBits += IHuffConstants.ALPH_SIZE * IHuffConstants.BITS_PER_INT;
        } else {
            compressedBits += IHuffConstants.BITS_PER_INT;
            compressedBits += tree.goingThroughTree(root);
        }
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
