## 添加遮罩

```javascript
function onload() {
        var loading = document.getElementById("loading");
        if (loading != null) {
          loading.style.display = 'block';
        } else {
          var loadCss = document.createElement("style");
          loadCss.type = "text/css";
          loadCss.innerHTML = "@keyframes loop { 0%{transform: rotate(0deg);} 100%{ transform: rotate(360deg);}";
          document.head.appendChild(loadCss);

          var mask_bg = document.createElement("div");
          mask_bg.id = "loading"
          mask_bg.style.cssText = "position:absolute;top:0;left:0;width:100%;height:100%;background-color:#777;opacity:0.6;z-index:10001;"
          document.body.appendChild(mask_bg);


          var mask_content = document.createElement("div");
          mask_content.style.cssText = "position:absolute;top:35%;left:42%;color: #fff;text-align:center;padding:z-index:10001;"
          mask_bg.appendChild(mask_content);

          var mask_pic = document.createElement("picture");
          mask_pic.style.cssText = "display: block;width: 50px;height: 50px;border-radius: 50%;box-shadow:#fff 0 2px 0; animation: loop 1s infinite;-webkit-animation: loop 1s infinite;";
          mask_content.appendChild(mask_pic);

          var mask_disc = document.createElement("div");
          mask_disc.style.cssText = "transform: translate(-25%, 70%);"
          mask_disc.innerText = "正在保存,请稍后...";
          mask_content.appendChild(mask_disc);
          
        }
      }
      function loaded() {
        var loading = document.getElementById("loading");
        if (loading != null) loading.style.display = 'none';
      }
```

> [js实现的简单遮罩层 - WmW - 博客园 (cnblogs.com)](https://www.cnblogs.com/luludongxu/p/5909548.html)



## 监听节点的值的变化

```js
<script type="text/javascript">
    // 选择将观察突变的节点
    var targetNode = document.getElementById('el-test');
 
    // 观察者的选项(要观察哪些突变)
    var config = { attributes: true, childList: true, subtree: true };
 
    // 当观察到突变时执行的回调函数
    var callback = function(mutationsList) {
        mutationsList.forEach(function(item,index){
            if (item.type == 'childList') {
                console.log('有节点发生改变，当前节点的内容是：');
                console.log(item.target.innerHTML);
            } else if (item.type == 'attributes') {
                console.log('修改了'+item.attributeName+'属性');
            }
        });
    };
 
    // 创建一个链接到回调函数的观察者实例
    var observer = new MutationObserver(callback);
 
    // 开始观察已配置突变的目标节点
    observer.observe(targetNode, config);
 
    // 停止观察
    //observer.disconnect();
</script>
```

> 参考：[javascript 监听DOM内容改变事件_黄河爱浪-CSDN博客_js监听dom变化](https://blog.csdn.net/u013350495/article/details/90755115)





## 操作iframe中的元素

[iframe的操作-Js/Jquery获取iframe中的元素 - AlisonGavin - 博客园 (cnblogs.com)](https://www.cnblogs.com/alisonGavin/p/8315634.html)





## 淡入淡出

https://www.cnblogs.com/aimyfly/archive/2013/06/14/3135233.html

https://blog.csdn.net/weixin_43732987/article/details/104132004

### CSS过渡

[深入理解CSS过渡transition - 小火柴的蓝色理想 - 博客园 (cnblogs.com)](https://www.cnblogs.com/xiaohuochai/p/5347930.html)



## Tips

* ### JS迭代器中，foreach多层循环中无法break（单层未知），建议使用forof

* 
