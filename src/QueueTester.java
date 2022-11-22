import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueueTester {
    public static void main(String[] args) throws IOException {
        char[] ascii = new char[257];
        BitInputStream bits = new BitInputStream("smallTxt.txt");
        int read = 0;
        while (read != -1) {
            read = bits.read();
            if (read != -1) {
                ascii[read]++;
            }
        }
        ascii[256] = 1;
        PriorityQueue314 queue = new PriorityQueue314();
        for (int i = 0; i < ascii.length; i++) {
            if (ascii[i] != 0) {
                queue.add(new TreeNode(i, ascii[i]));
            }
        }
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, -1, right));
        }
        System.out.println(queue);
    }
}