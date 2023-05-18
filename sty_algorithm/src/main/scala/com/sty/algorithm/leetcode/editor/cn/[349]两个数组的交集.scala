//leetcode submit region begin(Prohibit modification and deletion)
import scala.collection.mutable
object Solution {
  def intersection(nums1: Array[Int], nums2: Array[Int]): Array[Int] = {
    val map: mutable.Map[Int, Int] = mutable.HashMap()

    for (elem <- nums1) {
      map.put(elem, 0)
    }

    var res: mutable.HashSet[Int] = mutable.HashSet()
    for (elem <- nums2) {
      if (map.contains(elem)) {
        res = res + elem
      }
    }

    res.toArray
  }
}
//leetcode submit region end(Prohibit modification and deletion)
