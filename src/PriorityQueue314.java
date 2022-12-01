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
