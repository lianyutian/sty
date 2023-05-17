import scala.collection.mutable

//leetcode submit region begin(Prohibit modification and deletion)
object Solution {

    def isAnagram(s: String, t: String): Boolean = {
        if (s.length != t.length) {
            return false
        }

        val sMap = fun(s)
        val tMap = fun(t)

        var res = true

        for (elem <- sMap) {
            if (!tMap.contains(elem._1)) {
                res = false
                return res
            }
            if (tMap(elem._1) != elem._2) {
                res = false
                return res
            }
        }

        res

    }

    def fun(s: String) = {
        val array = s.toCharArray
        val map: mutable.HashMap[Char, Int] =  mutable.HashMap()
        array.foreach(c => {
            if (map.contains(c)) {
                val v = map.get(c)
                map.put(c, v.get + 1)
            } else {
                map.put(c, 0)
            }
        })
        map
    }
}
//leetcode submit region end(Prohibit modification and deletion)
