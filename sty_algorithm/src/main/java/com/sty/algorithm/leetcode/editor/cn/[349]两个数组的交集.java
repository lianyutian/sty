package com.sty.algorithm.leetcode.editor.cn;

import java.util.HashSet;
import java.util.Set;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution349 {
    public int[] intersection(int[] nums1, int[] nums2) {

        Set<Integer> set = new HashSet<>(nums1.length);
        for (int i : nums1) {
            set.add(i);
        }

        Set<Integer> res = new HashSet<>(nums1.length);
        for (int i : nums2) {
            if (set.contains(i)) {
                res.add(i);
            }
        }

        return res.stream().mapToInt(Integer::intValue).toArray();
    }
}
//leetcode submit region end(Prohibit modification and deletion)
