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

public class Uncompress {
    private HuffmanTree hf;

    /**
     * Prepares the file to be uncompressed with the correct header.
     * @param inputStream used to read bits
     * @throws IOException if an error occurs while reading from the input file.
     */
    public Uncompress(BitInputStream inputStream, int format, IHuffViewer view)
        throws IOException {
        int[] frequencyArr = new int[IHuffConstants.ALPH_SIZE + 1];
        if (format == IHuffConstants.STORE_COUNTS) {
            showString("uncompress store count format", view);
            for(int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                frequencyArr[i] = inputStream.readBits(IHuffConstants.BITS_PER_INT);
            }
            frequencyArr[IHuffConstants.PSEUDO_EOF] = 1;
            hf = new HuffmanTree(frequencyArr);
        } else {
            showString("uncompress store tree format", view);
            inputStream.readBits(IHuffConstants.BITS_PER_INT);
            hf = new HuffmanTree(inputStream, view);
        }
    }

    /**
     * Returns the bits written when the file is uncompressed
     * @param inputStream used to read bits
     * @param outputStream used to write bits
     * @return bits in uncompressed file
     * @throws IOException if an error occurs while reading/writing from the input/output file.
     */
    public int unhuff(BitInputStream inputStream, BitOutputStream outputStream, IHuffViewer viewer)
        throws IOException {
        return hf.writeTree(inputStream, outputStream, viewer);
    }

    /**
     * pre: none
     * shows a message on viewer
     * @param s is what needs to be shown
     */
    private void showString(String s, IHuffViewer myViewer) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }

}
