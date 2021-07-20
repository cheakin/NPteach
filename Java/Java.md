

## 实体字段校验@NotNull、@NotEmpty、@NotBlank

1.@NotNull：
不能为null，但可以为empty(""," “,” ") ，一般用在基本数据类型的非空校验上，而且被其标注的字段可以使用 @size/@Max/@Min对字段数值进行大小的控制

2.@NotEmpty：
不能为null，而且长度必须大于0(" “,” ")，一般用在集合类上面

3.@NotBlank：
这玩意只能作用在接收的String类型上，注意是只能，不能为null，而且调用trim()后，长度必须大于0





## swagger的json路径

`localhost:8086/v2/api-docs?group=1.x`







