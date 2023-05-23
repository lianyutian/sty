

//leetcode submit region begin(Prohibit modification and deletion)
import scala.collection.mutable
object Solution {
    def canConstruct(ransomNote: String, magazine: String): Boolean = {
        val map: mutable.Map[Char, Int] = new mutable.HashMap()
        for (elem <- magazine.toArray) {
            if (map.contains(elem)) {
                map.put(elem, map(elem) + 1)
            } else {
                map.put(elem, 1)
            }
        }

        var res = true
        for (elem <- ransomNote.toArray) {
            if (!map.contains(elem)) {
                res = false
                return res
            } else if (map(elem) == 0) {
                res = false
                return res
            }
            map.put(elem, map(elem) - 1)
        }

        res
    }
}
//leetcode submit region end(Prohibit modification and deletion)
