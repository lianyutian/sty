<p>请实现一个函数，把字符串 <code>s</code> 中的每个空格替换成"%20"。</p>

<p>&nbsp;</p>

<p><strong>示例 1：</strong></p>

<pre><strong>输入：</strong>s = "We are happy."
<strong>输出：</strong>"We%20are%20happy."</pre>

<p>&nbsp;</p>

<p><strong>限制：</strong></p>

<p><code>0 &lt;= s 的长度 &lt;= 10000</code></p>

<details><summary><strong>Related Topics</strong></summary>字符串</details><br>

<div>👍 491, 👎 0<span style='float: right;'><span style='color: gray;'><a href='https://github.com/labuladong/fucking-algorithm/discussions/939' target='_blank' style='color: lightgray;text-decoration: underline;'>bug 反馈</a> | <a href='https://labuladong.gitee.io/article/fname.html?fname=jb插件简介' target='_blank' style='color: lightgray;text-decoration: underline;'>使用指南</a> | <a href='https://labuladong.github.io/algo/images/others/%E5%85%A8%E5%AE%B6%E6%A1%B6.jpg' target='_blank' style='color: lightgray;text-decoration: underline;'>更多配套插件</a></span></span></div>

<div id="labuladong"><hr>

**通知：[数据结构精品课](https://aep.h5.xeknow.com/s/1XJHEO) 已更新到 V2.1，[手把手刷二叉树系列课程](https://aep.xet.tech/s/3YGcq3) 上线。**

<details><summary><strong>labuladong 思路</strong></summary>

## 基本思路

很简单，只要注意下不同语言中字符串的操作即可。

**标签：字符串**

## 解法代码

提示：🟢 标记的是我写的解法代码，🤖 标记的是 chatGPT 翻译的多语言解法代码。如有错误，可以 [点这里](https://github.com/labuladong/fucking-algorithm/issues/1113) 反馈和修正。

<div class="tab-panel"><div class="tab-nav">
<button data-tab-item="cpp" class="tab-nav-button btn " data-tab-group="default" onclick="switchTab(this)">cpp🤖</button>

<button data-tab-item="python" class="tab-nav-button btn " data-tab-group="default" onclick="switchTab(this)">python🤖</button>

<button data-tab-item="java" class="tab-nav-button btn active" data-tab-group="default" onclick="switchTab(this)">java🟢</button>

<button data-tab-item="go" class="tab-nav-button btn " data-tab-group="default" onclick="switchTab(this)">go🤖</button>

<button data-tab-item="javascript" class="tab-nav-button btn " data-tab-group="default" onclick="switchTab(this)">javascript🤖</button>
</div><div class="tab-content">
<div data-tab-item="cpp" class="tab-item " data-tab-group="default"><div class="highlight">

```cpp
// 注意：cpp 代码由 chatGPT🤖 根据我的 java 代码翻译，旨在帮助不同背景的读者理解算法逻辑。
// 本代码还未经过力扣测试，仅供参考，如有疑惑，可以参照我写的 java 代码对比查看。

class Solution27 {
public:
    string replaceSpace(string s) {
        string ans;
        for (char c : s) {
            if (c == ' ') {
                ans += "%20";
            } else {
                ans += c;
            }
        }
        return ans;
    }
};
```

</div></div>

<div data-tab-item="python" class="tab-item " data-tab-group="default"><div class="highlight">

```python
# 注意：python 代码由 chatGPT🤖 根据我的 java 代码翻译，旨在帮助不同背景的读者理解算法逻辑。
# 本代码已经通过力扣的测试用例，应该可直接成功提交。

class Solution27:
    def replaceSpace(self, s: str) -> str:
        sb = []
        for c in s:
            if c == ' ':
                sb.append('%20')
            else:
                sb.append(c)
        return ''.join(sb)
```

</div></div>

<div data-tab-item="java" class="tab-item active" data-tab-group="default"><div class="highlight">

```java
class Solution27 {
    public String replaceSpace(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                sb.append("%20");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
```

</div></div>

<div data-tab-item="go" class="tab-item " data-tab-group="default"><div class="highlight">

```go
// 注意：go 代码由 chatGPT🤖 根据我的 java 代码翻译，旨在帮助不同背景的读者理解算法逻辑。
// 本代码已经通过力扣的测试用例，应该可直接成功提交。

/**
 * @author ylx
 * @date 2021/2/2 19:19
 * @description Go 实现的 LeetCode 剑指 Offer 05. 替换空格
 */
func replaceSpace(s string) string {
    var res strings.Builder
    for i := 0; i < len(s); i++ {
        if s[i] == ' ' {
            res.WriteString("%20")
        } else {
            res.WriteByte(s[i])
        }
    }
    return res.String()
}
```

</div></div>

<div data-tab-item="javascript" class="tab-item " data-tab-group="default"><div class="highlight">

```javascript
// 注意：javascript 代码由 chatGPT🤖 根据我的 java 代码翻译，旨在帮助不同背景的读者理解算法逻辑。
// 本代码已经通过力扣的测试用例，应该可直接成功提交。

var replaceSpace = function(s) {
  var sb = [];
  for (var i = 0; i < s.length; i++) {
    var c = s.charAt(i);
    if (c === ' ') {
      sb.push('%20');
    } else {
      sb.push(c);
    }
  }
  return sb.join('');
};
```

</div></div>
</div></div>

<details open><summary><strong>👉 算法可视化 👈</strong></summary><div class="resizable aspect-ratio-container" style="height: 70vh;">
    <iframe src="https://labuladong.github.io/algo-visualize/" width="100%"
    height="100%" scrolling="auto" frameborder="0"
    style="overflow: auto;" id="iframe_ti-huan-kong-ge-lcof"></iframe>
</div>
<div id="data_ti-huan-kong-ge-lcof" data="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"></div></details><hr /><br />

</details>
</div>



