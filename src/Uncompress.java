import java.io.IOException;

public class Uncompress {
    private HuffmanTree hf;

    public Uncompress(BitInputStream inputStream) throws IOException {
        int format = inputStream.readBits(IHuffConstants.BITS_PER_INT);
        if (format != IHuffConstants.STORE_COUNTS && format != IHuffConstants.STORE_TREE) {
            throw new IllegalArgumentException("format is not right");
        }
        int[] frequencyArr = new int[IHuffConstants.ALPH_SIZE + 1];
        if (format == IHuffConstants.STORE_COUNTS) {
            for(int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                frequencyArr[i] = inputStream.readBits(IHuffConstants.BITS_PER_INT);
            }
            frequencyArr[IHuffConstants.PSEUDO_EOF] = 1;
            hf = new HuffmanTree(frequencyArr);
        } else {
            inputStream.readBits(IHuffConstants.BITS_PER_INT);
            hf = new HuffmanTree(inputStream);
        }
    }

    public int unhuff(BitInputStream inputStream, BitOutputStream outputStream)
        throws IOException {
        return hf.writeTree(inputStream, outputStream);
    }

}
