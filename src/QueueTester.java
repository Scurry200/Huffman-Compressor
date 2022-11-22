import java.util.ArrayList;

public class QueueTester {
    public static void main(String[] args) {
        ArrayList<TreeNode> list = new ArrayList<>();
        list.add(new TreeNode(0, 1));
        list.add(new TreeNode(1, 4));
        list.add(new TreeNode(2, 1));
        list.add(new TreeNode(3, 1));
        list.add(new TreeNode(4, 2));
        list.add(new TreeNode(5, 2));
        list.add(new TreeNode(6, 1));
        list.add(new TreeNode(7, 4));
        PriorityQueue314 queue = new PriorityQueue314();
        for (TreeNode node : list) {
            queue.add(node);
        }
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, -1, right));
        }
        System.out.println(queue);
    }
}