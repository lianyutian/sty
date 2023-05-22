//leetcode submit region begin(Prohibit modification and deletion)
import scala.collection.mutable


object Solution {
    def fourSumCount(nums1: Array[Int], nums2: Array[Int], nums3: Array[Int], nums4: Array[Int]): Int = {
        val map: mutable.HashMap[Int, Int] = mutable.HashMap()
        for (n1 <- nums1) {
            for (n2 <- nums2) {
                map.put(n1 + n2, map.getOrElse(n1 + n2, 0) + 1)
            }
        }

        var res = 0

        for (n1 <- nums3) {
            for (n2 <- nums4) {
                res = res + map.getOrElse(-n1 - n2, 0)
            }
        }

        res
    }
}
//leetcode submit region end(Prohibit modification and deletion)
