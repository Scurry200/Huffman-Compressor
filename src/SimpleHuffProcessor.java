/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private char[] ascii;
    private PriorityQueue314 queue;
    private int format;

    public SimpleHuffProcessor() {
        ascii = new char[257];
        queue = new PriorityQueue314();
    }




    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        format = headerFormat;

        BitInputStream bits = new BitInputStream(in);
        int read = 0;
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                ascii[read]++;

            }
        }
        ascii[ALPH_SIZE] = 1;
        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != 0) {
                queue.add(new TreeNode(i, ascii[i]));
            }
        }
        //PriorityQueue314 queue = new PriorityQueue314();
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
        return findingOriginalBits() - findingCompressedBits(map, root);
    }

    private int findingOriginalBits() {
        int bits = 0;
        for (int i = 0; i < ALPH_SIZE - 1; i++) {
            bits += BITS_PER_WORD * ascii[i];
        }
        return bits;
    }

    private int findingCompressedBits(HashMap<Integer, String> map, TreeNode root) {
        int compressed = 2 * BITS_PER_INT;
        for (int key: map.keySet()) {
            compressed += ascii[key] * map.get(key).length();
        }
        if (format == STORE_COUNTS) {
            compressed += ALPH_SIZE * BITS_PER_INT;
        }
        if (format == STORE_TREE) {
            compressed += BITS_PER_INT;
            compressed += goingThroughTree(root);
        }
        return compressed;
    }

    private int goingThroughTree(TreeNode temp) {
        //goes through tree finding total bits
        if (temp.isLeaf()) {
            return 1 + BITS_PER_WORD + 1; //1 for node, 9 for bits per leaf node
        } else {
            return 1 + goingThroughTree(temp.getLeft()) + goingThroughTree(temp.getRight());
        }
    }

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
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {

        throw new IOException("compress is not implemented");
        //return 0;
    }


    private int decode() throws IOException {
        TreeNode currNode = queue.peek();
        //throw new IOException(){
        BitInputStream bitsIn = new BitInputStream("a6_feedback.txt");
            boolean done = false;
            while (!done) {
                int bit = bitsIn.readBits(1);
                if (bit == -1) {
                    throw new IOException("error reading compressed " +
                        "file");
                } else {
                    if (bit == 0) {
                        currNode = currNode.getLeft();
                    } else if (bit == 1) {
                        currNode = currNode.getRight();
                    }
                    if (currNode.isLeaf()) {
                        if (bit == 256) {
                            done = true;
                        }
                    } else {
                        ascii[bit]++;
                        currNode = queue.peek();
                    }
                }
            }
        return 0;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        //return 0;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}