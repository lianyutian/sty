
//leetcode submit region begin(Prohibit modification and deletion)
object Solution {
    def replaceSpace(s: String): String = {
        var count = 0
        s.foreach(c => if (c == ' ') count += 1) // 统计空格的数量
        val sOldSize = s.length // 旧数组字符串长度
        val sNewSize = s.length + count * 2 // 新数组字符串长度
        val res = new Array[Char](sNewSize) // 新数组
        var index = sNewSize - 1 // 新数组索引
        // 逆序遍历
        for (i <- (0 until sOldSize).reverse) {
            if (s(i) == ' ') {
                res(index) = '0'
                index -= 1
                res(index) = '2'
                index -= 1
                res(index) = '%'
            } else {
                res(index) = s(i)
            }
            index -= 1
        }
        res.mkString
    }
}
//leetcode submit region end(Prohibit modification and deletion)
