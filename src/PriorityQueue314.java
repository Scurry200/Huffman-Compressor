import java.util.LinkedList;

public class PriorityQueue314 {
    private LinkedList<TreeNode> con;

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
    public boolean add(TreeNode node) {
        int index = 0;
        for (TreeNode temp : con) {
            if ((temp.compareTo(node) > 0) || (temp.compareTo(node) == 0 &&
                (node.getValue() != -1 && node.getValue() - temp.getValue() < 0))) {
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
    public TreeNode peek() {
        return con.getFirst();
    }

    /**
     * Removes and returns top of queue
     * @return first value in con
     */
    public TreeNode poll() {
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
