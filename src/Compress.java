import java.io.IOException;
import java.util.ArrayList;

public class Compress {
    private int[] ascii;
    private int format;
    private int compressedBits;
    private int originalBits;
    private HuffmanTree tree;

    public Compress(BitInputStream inputStream, int header) throws IOException {
        format = header;
        ascii = new int[IHuffConstants.ALPH_SIZE + 1];
        int read = inputStream.read();
        while (read != -1) {
            ascii[read]++;
            read = inputStream.read();
        }
        ascii[IHuffConstants.ALPH_SIZE] = 1;
        tree = new HuffmanTree(ascii);
        findingOriginalBits();
        findingCompressedBits(tree.getTree(), header);
    }

    public int bitsSaved() {
        return originalBits - compressedBits;
    }

    public int huff(BitInputStream inputStream, BitOutputStream outputStream) throws IOException {
        outputStream.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
        outputStream.writeBits(IHuffConstants.BITS_PER_INT, format);
        if (format == IHuffConstants.STORE_COUNTS) {
            for(int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                outputStream.writeBits(IHuffConstants.BITS_PER_INT, ascii[i]);
            }
        } else if (format == IHuffConstants.STORE_TREE) {
            ArrayList<TreeNode> nodes = new ArrayList<>();
            preOrder(tree.getTree(), nodes);
            int count = 0;
            for (TreeNode node : nodes) {
                if (node.isLeaf()) {
                    count++;
                }
            }
            int sizeInternal = nodes.size() - count;
            outputStream.writeBits(IHuffConstants.BITS_PER_INT, (sizeInternal + (count * (IHuffConstants.BITS_PER_WORD + 2))));
            preOrderHelp(outputStream, tree.getTree());
        }
        int read = inputStream.read();
        while (read != -1) {
            sequenceConverting(tree.getMap().get(read), outputStream);
            read = inputStream.read();
        }
        sequenceConverting(tree.getMap().get(IHuffConstants.ALPH_SIZE), outputStream);
        outputStream.close();
        return compressedBits;
    }

    public int getFormat() {
        return format;
    }

    /**
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
     * pre: none
     * post: should have a list of all the nodes in the huffman tree
     * recursively goes through the tree and adds all the nodes in the huffman tree
     */
    private void preOrder(TreeNode node, ArrayList<TreeNode> list) {
        list.add(node);
        if (node.getLeft() != null) {
            preOrder(node.getLeft(), list);
        }
        if (node.getRight() != null) {
            preOrder(node.getRight(), list);
        }
    }

    /**
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

    /**
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

}
