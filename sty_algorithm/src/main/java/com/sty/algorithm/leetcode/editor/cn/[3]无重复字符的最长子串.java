package com.sty.algorithm.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution3 {
    public int lengthOfLongestSubstring(String s) {
        if (s == null) {
            return 0;
        }
        char[] chars = s.toCharArray();
        int len = s.length();
        int left = 0;
        int right = 1;
        int max = 0;

        while (right <= len) {

            String sub = s.substring(left, right);
            if (max < sub.length()) {
                max = sub.length();
            }
            if (right == len) {
                break;
            }
            String rihgtChar = String.valueOf(chars[right]);
            while (sub.contains(rihgtChar)) {
                left++;
                if (left == right) {
                    break;
                }
                sub = s.substring(left, right);
            }

            right++;
        }

        return max;
    }

//    public static void main(String[] args) {
//        String s = "au";
//        System.out.println(lengthOfLongestSubstring(s));
//
//    }
}
//leetcode submit region end(Prohibit modification and deletion)
