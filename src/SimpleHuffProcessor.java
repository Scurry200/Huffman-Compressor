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
import java.util.Arrays;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private int[] ascii;
    private HuffmanTree tree;
    private int format;
    private int compressedBits;

    public SimpleHuffProcessor() {
        ascii = new int[ALPH_SIZE + 1];
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
        ascii = new int[ALPH_SIZE + 1];
        compressedBits = 0;
        format = headerFormat;
        BitInputStream bits = new BitInputStream(in);
        int read = bits.read();
        while (read != -1) {
            ascii[read]++;
            read = bits.read();
        }
        ascii[ALPH_SIZE] = 1;
        tree = new HuffmanTree(ascii);
        findingCompressedBits(tree.getTree());
        return findingOriginalBits() - compressedBits;
    }

    private int findingOriginalBits() {
        int bits = 0;
        for (int i = 0; i < ALPH_SIZE; i++) {
            bits += BITS_PER_WORD * ascii[i];
        }
        return bits;
    }

    private void findingCompressedBits(TreeNode root) {
        compressedBits = BITS_PER_INT + BITS_PER_INT;
        for (int key: tree.getMap().keySet()) {
            compressedBits += ascii[key] * tree.getMap().get(key).length();
        }
        if (format == STORE_COUNTS) {
            compressedBits += ALPH_SIZE * BITS_PER_INT;
        } else {
            compressedBits += BITS_PER_INT;
            compressedBits += goingThroughTree(root);
        }
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
        //reading in 8 bits at a time with the generated huffman codes (in the map),
        // but this is after we put in header format, magic number, header, and at the end we
        // include peof (but that's already stored in the map)
        if (!force && findingOriginalBits() - compressedBits < 0) {
            myViewer.showMessage("you don't save any bits");
            return 0;
        }
        int returnVal = 0;
        BitInputStream inputStream = new BitInputStream(in);
        BitOutputStream bitOutputStream = new BitOutputStream(out);
        bitOutputStream.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        bitOutputStream.writeBits(BITS_PER_INT, format);
        returnVal += BITS_PER_INT * 2;
        if (format == STORE_COUNTS) {
            for(int i = 0; i < ALPH_SIZE; i++) {
                bitOutputStream.writeBits(BITS_PER_INT, ascii[i]);
                returnVal += BITS_PER_INT;
            }
        } else {
            ArrayList<TreeNode> nodes = new ArrayList<>();
            preOrder(tree.getTree(), nodes);
            int count = 0;
            for (TreeNode node : nodes) {
                if (node.isLeaf()) {
                    count++;
                }
            }
            int sizeInternal = nodes.size() - count;
            bitOutputStream.writeBits(BITS_PER_INT, (sizeInternal + (count * (BITS_PER_WORD + 2))));
            returnVal += BITS_PER_INT;
            returnVal += preOrderHelp(bitOutputStream, tree.getTree());
        }
        int read = inputStream.read();
        while (read != -1) {
            returnVal += sequenceConverting(tree.getMap().get(read), bitOutputStream);
            read = inputStream.read();
        }
        returnVal += sequenceConverting(tree.getMap().get(256), bitOutputStream);
        bitOutputStream.close();
        return returnVal;
    }

    public int sequenceConverting(String huffcode, BitOutputStream bitOutputStream) {
        int returnVal = 0;
        for (int i = 0; i < huffcode.length(); i++) {
            returnVal += 1;
            bitOutputStream.writeBits(1, Integer.parseInt(huffcode.substring(i, i+1)));
        }
        return returnVal;
    }

    private int preOrderHelp(BitOutputStream outputStream,
                             TreeNode node) {
        int returnVal = 0;
        if (node.isLeaf()) {
            outputStream.writeBits(1, 1);
            returnVal += 1;

            outputStream.writeBits(BITS_PER_WORD+1, node.getValue());
            returnVal += BITS_PER_WORD+1;
            //9 bits bc ascii is 256-511, 8 bits for 0-255, 256 is peof
        } else {
            outputStream.writeBits(1, 0);
            returnVal += 1;
            returnVal += preOrderHelp(outputStream, node.getLeft());
            returnVal += preOrderHelp(outputStream, node.getRight());
            //since huffman is complete tree, no need to check if a child is null
        }
        return returnVal;
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
        int returnVal = 0;
        BitInputStream inputStream = new BitInputStream(in);
        BitOutputStream outputStream = new BitOutputStream(out);
        int magic = inputStream.readBits(BITS_PER_INT);
        if (magic != MAGIC_NUMBER) {
            myViewer.showError("Error reading compressed file. \n" +
                "File did not start with the huff magic number.");
            return -1;
        }
        int format = inputStream.readBits(BITS_PER_INT);
        int[] frequencyArr = new int[257];
        HuffmanTree hf;
        //outputStream.writeBits(BITS_PER_INT, format);
        //returnVal += BITS_PER_INT;
        if (format == STORE_COUNTS) {
            for(int i = 0; i < ALPH_SIZE; i++) {
                frequencyArr[i] = inputStream.readBits(BITS_PER_INT);
            }
            frequencyArr[PSEUDO_EOF] = 1;
            hf = new HuffmanTree(frequencyArr);
        } else {
            inputStream.readBits(BITS_PER_INT);
            hf = new HuffmanTree(inputStream);
            //goes through size
        }
        returnVal += hf.goThroughTreeToWrite(inputStream, outputStream);
        return returnVal;
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