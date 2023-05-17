package com.sty.algorithm.leetcode.editor.cn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

    public boolean isAnagram(String s, String t) {
        AtomicBoolean res = new AtomicBoolean(true);

        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Integer> sMap = f(s);
        Map<Character, Integer> tMap = f(t);

        sMap.forEach((k, v) -> {
            if (!tMap.containsKey(k)) {
                res.set(false);
                return;
            }
            if (!Objects.equals(tMap.get(k), v)) {
                res.set(false);
            }
        });

        return res.get();
    }

    private Map<Character, Integer> f(String s) {
        char[] chars = s.toCharArray();
        Map<Character, Integer> map = new HashMap<>();

        for (char c : chars) {
            if (map.containsKey(c)) {
                Integer v = map.get(c);
                map.put(c, v + 1);
            } else {
                map.put(c, 0);
            }
        }

        return map;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
