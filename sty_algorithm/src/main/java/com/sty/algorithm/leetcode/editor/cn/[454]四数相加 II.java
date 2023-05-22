package com.sty.algorithm.leetcode.editor.cn;

import java.util.HashMap;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution454 {
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<Integer, Integer> map1 = new HashMap<>();
        for (int n1 : nums1) {
            for (int n2 : nums2) {
                map1.put(n1 + n2, map1.getOrDefault(n1 + n2, 0) + 1);
            }
        }

        int res = 0;
        for (int n1 : nums3) {
            for (int n2 : nums4) {
                res += map1.getOrDefault(-n1 -n2, 0);
            }
        }

        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
