package com.sty.algorithm.leetcode.editor.cn;

import java.util.Deque;
import java.util.LinkedList;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution1047 {

    public String removeDuplicates(String s) {
        Deque<Character> stack = new LinkedList<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        StringBuilder res = new StringBuilder();
        for (Character character : stack) {
            res.insert(0, character);
        }

        return res.toString();
    }
}
//leetcode submit region end(Prohibit modification and deletion)
