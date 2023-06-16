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
class Solution101 {
    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return compare(root.left, root.right);
    }

    /**
     * 比较左右两棵数是否翻转
     *
     * 递归三部曲
     *
     * 1.确定递归函数的参数和返回值
     * 2.确定终止条件
     *  1)left或right为null
     *  2)left和right不为null但值不同
     * 3.确定单层递归的逻辑
     */
    private boolean compare(TreeNode left, TreeNode right) {
        if (left != null && right == null) {
            return false;
        } else if (left == null && right != null) {
            return false;
        } else if (left == null && right == null) {
            return true;
        } else if (left.val != right.val) {
            return false;
        }
        // 剩下情况为左、右节点都不为空且值相同得场景
        // 比较外层是否一样
        boolean out = compare(left.left, right.right);
        // 比较里层是否一样
        boolean in = compare(left.right, right.left);
        // 里外都要为true才为true
        return out && in;
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
