package com.sty.algorithm.leetcode.editor.cn;
//leetcode submit region begin(Prohibit modification and deletion)

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode() {}
 * TreeNode(int val) { this.val = val; }
 * TreeNode(int val, TreeNode left, TreeNode right) {
 * this.val = val;
 * this.left = left;
 * this.right = right;
 * }
 * }
 */
class Solution226 {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return root;
        }
        invertTree2(root);
        return root;
    }

    public void invertTree2(TreeNode root) {
        TreeNode left = root.left;
        TreeNode right = root.right;
        root.left = right;
        root.right = left;
        if (left != null) {
            invertTree2(left);
        }
        if (right != null) {
            invertTree2(right);
        }
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
