package com.sty.algorithm.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution27 {
    public int removeElement(int[] nums, int val) {
        int k = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[k++] = nums[i];
            }
        }

        return k;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
