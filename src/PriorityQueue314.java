import java.util.LinkedList;

public class PriorityQueue314 {
    private LinkedList<TreeNode> con;

    public PriorityQueue314() {
        con = new LinkedList<>();
    }

    public boolean add(TreeNode node) {
        int index = 0;
        for (TreeNode temp : con) {
            if (temp.compareTo(node) > 0) {
                con.add(index, node);
                return true;
            }
            index++;
        }
        con.add(node);
        return true;
    }

    public TreeNode poll() {
        return con.removeFirst();
    }

    public int size() {
        return con.size();
    }
}
