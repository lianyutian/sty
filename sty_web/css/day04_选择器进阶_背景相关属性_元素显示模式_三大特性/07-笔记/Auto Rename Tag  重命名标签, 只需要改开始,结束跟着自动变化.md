- Auto Rename Tag : 重命名标签, 只需要改开始,结束跟着自动变化
- JS-CSS-HTML Formatter: 格式化代码
- HTML CSS Support : 提示类名, 需要配置
  - vscode的左下角找到设置 - 搜索setting - 在setting.json中编辑 - 粘贴代码

```js
"editor.quickSuggestions": {
    "other": true,
     "comments": true,
     "strings": true
}
```

注意: 如果代码的前后有内容加逗号

- Live Server: 以服务器模式打开网页(不需要刷新)