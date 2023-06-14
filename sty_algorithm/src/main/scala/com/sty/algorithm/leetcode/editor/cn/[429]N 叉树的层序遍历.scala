
//leetcode submit region begin(Prohibit modification and deletion)

import java.util
import scala.collection.mutable

/**
 * Definition for a Node.
 * class Node(var _value: Int) {
 * var value: Int = _value
 * var children: List[Node] = List()
 * }
 */

object Solution {
  def levelOrder(root: Node): List[List[Int]] = {
    val res = mutable.ListBuffer[List[Int]]()
    if (root == null) return res.toList
    val queue = mutable.Queue[Node]()
    queue.enqueue(root) // 根节点入队
    while (!queue.isEmpty) {
      val tmp = mutable.ListBuffer[Int]() // 存储每层节点
      val len = queue.size
      for (i <- 0 until len) {
        val curNode = queue.dequeue()
        tmp.append(curNode.value) // 将该节点的值加入tmp
        // 循环遍历该节点的子节点，加入队列
        for (child <- curNode.children) {
          queue.enqueue(child)
        }
      }
      res.append(tmp.toList) // 将该层的节点放到结果集
    }
    res.toList
  }
}

class Node(var _value: Int) {
  var value: Int = _value
  var children: List[Node] = List()
}
//leetcode submit region end(Prohibit modification and deletion)
