package com.sty.algorithm.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution344 {
    public void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left <= right) {
            char temp = s[right];
            s[right] = s[left];
            s[left] = temp;
            left++;
            right--;
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
