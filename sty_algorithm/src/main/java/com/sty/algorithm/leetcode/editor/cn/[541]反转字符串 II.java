package com.sty.algorithm.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution541 {
    public String reverseStr(String s, int k) {
        char[] charArray = s.toCharArray();

        for (int i = 0; i < s.length(); i += 2 * k) {
            reverse(charArray, i, Math.min(i + k, s.length()) - 1);
        }

        return new String(charArray);
    }

    public void reverse(char[] charArray, int left, int right) {
        while (left <= right) {
            char temp = charArray[right];
            charArray[right] = charArray[left];
            charArray[left] = temp;
            left++;
            right--;
        }
    }


}
//leetcode submit region end(Prohibit modification and deletion)
