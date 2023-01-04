# EasyExcel

*更多可以参考官方文档*：[alibaba/easyexcel: 快速、简洁、解决大文件内存溢出的java处理Excel工具 (github.com)](https://github.com/alibaba/easyexcel)

## read

### 固定表头类方式读：

固定表头的方式比较简单，确定表头的相关实体，将数据与之映射起来，便可以读到数据：

```java
EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
            for (DemoData demoData : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
            }
        })).sheet().doRead();
```



### 不固定表头的方式读：

不固定表头，可以只读数据（忽略表头），也可以表头和数据都读。实现方式是实现`ReadListener`类，`ReadListener`类有处理异常、处理表头、处理数据、处理额外信息的方法需要实现，如下：

```java
EasyExcel.read(excel, new ReadListener() {
                /** 单次缓存的数据量 */
                public static final int BATCH_COUNT = 100;
                /** 临时存储保存解析到的结果 */
                private List<Map<Integer, String>> cachedDataList = new ArrayList<>(BATCH_COUNT);

                // 异常处理
                @Override
                public void onException(Exception e, AnalysisContext analysisContext) throws Exception {
                    throw new ExcelAnalysisException(e.getMessage());
                }

                // 处理表头
                @Override
                public void invokeHead(Map map, AnalysisContext analysisContext) {
                    Map<Integer, String> head = (Map<Integer, String>) map;
                    System.out.println("head.entrySet() = " + head.entrySet());
                    if (head.size() != columnCount + fixedColumnCount) {
                        throw new ExcelAnalysisException("导入文件内容格式不正确");
                    }
                }

                // 处理数据
                @Override
                public void invoke(Object o, AnalysisContext analysisContext) {
                    Map<Integer, String> map = (Map<Integer, String>) o;
                    cachedDataList.add(map);
                    // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
                    if (cachedDataList.size() >= BATCH_COUNT) {
                        saveData();
                        // 存储完成清理 list
                        cachedDataList.clear();
                    }
                }

                @Override
                public void extra(CellExtra cellExtra, AnalysisContext analysisContext) {
                    //额外信息（批注、超链接、合并单元格信息读取）
                }

                // 解析到最后会进入这个方法，需要重写这个doAfterAllAnalysed方法，然后里面调用自己定义好保存方法
                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    // 这里也要保存数据，确保最后遗留的数据也存储到数据库
                    saveData();
                }

                @Override
                public boolean hasNext(AnalysisContext analysisContext) {
                    return true;
                }

                // 存数据
                private void saveData() {
                    // do something
                }
            }).sheet().doRead();
```

开发中遇到的问题：

* 每次读入数据时掉完表头后就不会继续读取数据了，需要将重写`hasNext()`返回`true`便会读完表头后读数据了
* 此方式同步读取的方式，假如一次性将所有数据读入，内存可以会溢出，故将数据分批处理；再最后一次未满就没有处理，在`doAfterAllAnalysed()`收尾时要剩下的数据也处理了



> 参考：
>
> * [easyexcel导入获取表头并且表头为不固定列 - pz_ww - 博客园 (cnblogs.com)](https://www.cnblogs.com/pzw23/p/13354908.html?utm_source=tuicool)
> * [easyexcel导入获取表头并且表头为不固定列 - 走看看 (zoukankan.com)](http://t.zoukankan.com/pzw23-p-13354908.html)
> * [阿里的Easyexcel读取Excel文件（最新版本）_KANLON的博客-CSDN博客_easyexcel读取excel](https://blog.csdn.net/weixin_37610397/article/details/102657022)
> * [alibaba/easyexcel: 快速、简洁、解决大文件内存溢出的java处理Excel工具 (github.com)](https://github.com/alibaba/easyexcel)

