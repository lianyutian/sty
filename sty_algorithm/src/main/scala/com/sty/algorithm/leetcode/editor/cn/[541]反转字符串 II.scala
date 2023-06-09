
//leetcode submit region begin(Prohibit modification and deletion)
object Solution {

  def reverseStr(s: String, k: Int): String = {
    val array = s.toCharArray
    for (i <- s.indices by 2 * k) {
      // 如果i+k大于了res的长度，则需要全部翻转
      if (i + k > s.length) {
        reverseStr(array, i, s.length - 1)
      } else {
        reverseStr(array, i, i + k - 1)
      }
    }

    new String(array)
  }

  private def reverseStr(arr: Array[Char], left: Int, right: Int): Array[Char] = {
    var (l, r) = (left, right)
    while (l < r) {
      val temp = arr(r)
      arr(r) = arr(l)
      arr(l) = temp
      l += 1
      r -= 1
    }

    arr
  }
}
//leetcode submit region end(Prohibit modification and deletion)
