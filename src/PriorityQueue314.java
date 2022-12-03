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
import java.util.LinkedList;

public class PriorityQueue314<E extends Comparable<E>> {
    private LinkedList<E> con;

    /**
     * Constructor using LinkedList as container
     */
    public PriorityQueue314() {
        con = new LinkedList<>();
    }

    /**
     * Adds TreeNodes in order using frequencies or alphabetical order
     * @param node TreeNode being added in order
     * @return If value has been added
     */
    public boolean add(E node) {
        int index = 0;
        for (E temp : con) {
            if (temp.compareTo(node) > 0) {
                con.add(index, node);
                return true;
            }
            index++;
        }
        con.add(node);
        return true;
    }

    /**
     * Returns top of queue
     * @return first value in con
     */
    public E peek() {
        return con.getFirst();
    }

    /**
     * Removes and returns top of queue
     * @return first value in con
     */
    public E poll() {
        return con.removeFirst();
    }

    /**
     * Returns con size
     * @return con size
     */
    public int size() {
        return con.size();
    }

}
