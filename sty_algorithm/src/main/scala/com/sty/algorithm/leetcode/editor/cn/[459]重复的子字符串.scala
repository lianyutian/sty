
//leetcode submit region begin(Prohibit modification and deletion)
object Solution {
    def repeatedSubstringPattern(s: String): Boolean = {
        var str: String = s + s
        str.substring(1, str.length - 1).contains(s)
    }
}
//leetcode submit region end(Prohibit modification and deletion)
