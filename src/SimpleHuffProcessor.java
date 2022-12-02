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
 *  UTEID: Ayaan Nazir
 *  email address: nazir@utexas.edu
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Huffman coding class that has functions to compress data, uncompress data, based on
 * whether format requested is tree or count
 */
public class SimpleHuffProcessor implements IHuffProcessor {
    private IHuffViewer myViewer;
    private Compress comp;

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
        if (myViewer == null) {

        }
        comp = new Compress(new BitInputStream(in), headerFormat, myViewer);
        return comp.bitsSaved();
    }

    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is
     * larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        // reading in 8 bits at a time with the generated huffman codes (in the map),
        // but this is after we put in header format, magic number, header, and at the end we
        // include peof (but that's already stored in the map)
        if (comp.getFormat() != STORE_COUNTS && comp.getFormat() != STORE_TREE) {
            //myViewer.showError("format is neither store counts or storetree");
        }
        if (!force && comp.bitsSaved() < 0) {
            //myViewer.showMessage("you don't save any bits, so no need to have file");
            return 0;
        }
        //if (myViewer != null) {
            return comp.huff(new BitInputStream(in), new BitOutputStream(out), myViewer);
        //}
        //throw new IllegalArgumentException("viewer is null");
        //return 0;
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
        BitInputStream inputStream = new BitInputStream(in);
        BitOutputStream outputStream = new BitOutputStream(out);
        int magic = inputStream.readBits(BITS_PER_INT);
        if (magic != MAGIC_NUMBER) {
            //myViewer.showError("Error reading compressed file. \n" +
                //"File did not start with the huff magic number.");
            return -1;
        }
        if (myViewer != null) {
            Uncompress uncomp = new Uncompress(inputStream);
            return uncomp.unhuff(inputStream, outputStream);
        }
        throw new IllegalArgumentException("myviewer is null");
    }

    /**
     * Make sure this model communicates with some view.
     * @param viewer is the view for communicating.
     */
    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}