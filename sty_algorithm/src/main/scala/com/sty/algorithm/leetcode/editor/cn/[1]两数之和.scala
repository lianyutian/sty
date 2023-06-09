//leetcode submit region begin(Prohibit modification and deletion)
import scala.collection.mutable
object Solution1 {
    // 导入包
    import scala.collection.mutable

    def twoSum(nums: Array[Int], target: Int): Array[Int] = {
        // key存储值，value存储下标
        val map = new mutable.HashMap[Int, Int]()
        for (i <- nums.indices) {
            val tmp = target - nums(i) // 计算差值
            // 如果这个差值存在于map，则说明找到了结果
            if (map.contains(tmp)) {
                return Array(map(tmp), i)
            }
            // 如果不包含把当前值与其下标放到map
            map.put(nums(i), i)
        }
        // 如果没有找到直接返回一个空的数组，return关键字可以省略
        new Array[Int](2)
    }
}
//leetcode submit region end(Prohibit modification and deletion)
