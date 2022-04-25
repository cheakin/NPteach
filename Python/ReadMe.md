# Python

## 自动修改密码脚本

> 启发：通过[水哥]([不高兴就喝水的个人空间_哔哩哔哩_bilibili](https://space.bilibili.com/412704776))的视频了解到Python自动化的门槛并不高，恰好我有自动修改密码的一个需求

水哥自动点赞脚本：实现程序按照excel中指定的图片名，顺序的去操作

```python
import pyautogui	#处理鼠标操作
import time #处理等待
import xlrd	#处理excel
import pyperclip	#剪切板

#定义鼠标事件

#pyautogui库其他用法 https://blog.csdn.net/qingfengxd1/article/details/108270159

def mouseClick(clickTimes,lOrR,img,reTry):
    if reTry == 1:
        while True:
            location=pyautogui.locateCenterOnScreen(img,confidence=0.9)
            if location is not None:
                pyautogui.click(location.x,location.y,clicks=clickTimes,interval=0.2,duration=0.2,button=lOrR)
                break
            print("未找到匹配图片,0.1秒后重试")
            time.sleep(0.1)
    elif reTry == -1:
        while True:
            location=pyautogui.locateCenterOnScreen(img,confidence=0.9)
            if location is not None:
                pyautogui.click(location.x,location.y,clicks=clickTimes,interval=0.2,duration=0.2,button=lOrR)
            time.sleep(0.1)
    elif reTry > 1:
        i = 1
        while i < reTry + 1:
            location=pyautogui.locateCenterOnScreen(img,confidence=0.9)
            if location is not None:
                pyautogui.click(location.x,location.y,clicks=clickTimes,interval=0.2,duration=0.2,button=lOrR)
                print("重复")
                i += 1
            time.sleep(0.1)




# 数据检查
# cmdType.value  1.0 左键单击    2.0 左键双击  3.0 右键单击  4.0 输入  5.0 等待  6.0 滚轮
# ctype     空：0
#           字符串：1
#           数字：2
#           日期：3
#           布尔：4
#           error：5
def dataCheck(sheet1):
    checkCmd = True
    #行数检查
    if sheet1.nrows<2:
        print("没数据啊哥")
        checkCmd = False
    #每行数据检查
    i = 1
    while i < sheet1.nrows:
        # 第1列 操作类型检查
        cmdType = sheet1.row(i)[0]
        if cmdType.ctype != 2 or (cmdType.value != 1.0 and cmdType.value != 2.0 and cmdType.value != 3.0 
        and cmdType.value != 4.0 and cmdType.value != 5.0 and cmdType.value != 6.0):
            print('第',i+1,"行,第1列数据有毛病")
            checkCmd = False
        # 第2列 内容检查
        cmdValue = sheet1.row(i)[1]
        # 读图点击类型指令，内容必须为字符串类型
        if cmdType.value ==1.0 or cmdType.value == 2.0 or cmdType.value == 3.0:
            if cmdValue.ctype != 1:
                print('第',i+1,"行,第2列数据有毛病")
                checkCmd = False
        # 输入类型，内容不能为空
        if cmdType.value == 4.0:
            if cmdValue.ctype == 0:
                print('第',i+1,"行,第2列数据有毛病")
                checkCmd = False
        # 等待类型，内容必须为数字
        if cmdType.value == 5.0:
            if cmdValue.ctype != 2:
                print('第',i+1,"行,第2列数据有毛病")
                checkCmd = False
        # 滚轮事件，内容必须为数字
        if cmdType.value == 6.0:
            if cmdValue.ctype != 2:
                print('第',i+1,"行,第2列数据有毛病")
                checkCmd = False
        i += 1
    return checkCmd

#任务
def mainWork(img):
    i = 1
    while i < sheet1.nrows:
        #取本行指令的操作类型
        cmdType = sheet1.row(i)[0]
        if cmdType.value == 1.0:
            #取图片名称
            img = sheet1.row(i)[1].value
            reTry = 1
            if sheet1.row(i)[2].ctype == 2 and sheet1.row(i)[2].value != 0:
                reTry = sheet1.row(i)[2].value
            mouseClick(1,"left",img,reTry)
            print("单击左键",img)
        #2代表双击左键
        elif cmdType.value == 2.0:
            #取图片名称
            img = sheet1.row(i)[1].value
            #取重试次数
            reTry = 1
            if sheet1.row(i)[2].ctype == 2 and sheet1.row(i)[2].value != 0:
                reTry = sheet1.row(i)[2].value
            mouseClick(2,"left",img,reTry)
            print("双击左键",img)
        #3代表右键
        elif cmdType.value == 3.0:
            #取图片名称
            img = sheet1.row(i)[1].value
            #取重试次数
            reTry = 1
            if sheet1.row(i)[2].ctype == 2 and sheet1.row(i)[2].value != 0:
                reTry = sheet1.row(i)[2].value
            mouseClick(1,"right",img,reTry)
            print("右键",img) 
        #4代表输入
        elif cmdType.value == 4.0:
            inputValue = sheet1.row(i)[1].value
            pyperclip.copy(inputValue)
            pyautogui.hotkey('ctrl','v')
            time.sleep(0.5)
            print("输入:",inputValue)                                        
        #5代表等待
        elif cmdType.value == 5.0:
            #取图片名称
            waitTime = sheet1.row(i)[1].value
            time.sleep(waitTime)
            print("等待",waitTime,"秒")
        #6代表滚轮
        elif cmdType.value == 6.0:
            #取图片名称
            scroll = sheet1.row(i)[1].value
            pyautogui.scroll(int(scroll))
            print("滚轮滑动",int(scroll),"距离")                      
        i += 1

if __name__ == '__main__':
    file = 'cmd.xls'
    #打开文件
    wb = xlrd.open_workbook(filename=file)
    #通过索引获取表格sheet页
    sheet1 = wb.sheet_by_index(0)
    print('欢迎使用不高兴就喝水牌RPA~')
    #数据检查
    checkCmd = dataCheck(sheet1)
    if checkCmd:
        key=input('选择功能: 1.做一次 2.循环到死 \n')
        if key=='1':
            #循环拿出每一行指令
            mainWork(sheet1)
        elif key=='2':
            while True:
                mainWork(sheet1)
                time.sleep(0.1)
                print("等待0.1秒")    
    else:
        print('输入有误或者已经退出!')

```

这段代码用到的一些包：`pyautogui`鼠标操作， `time`处理等待， `xlrd`处理excel操作, `pyperclip`处理剪切板， 对鼠标操作的扩展[详解Python中pyautogui库的最全使用方法_studyer_domi的博客-CSDN博客_pyautogui scroll](https://blog.csdn.net/qingfengxd1/article/details/108270159)

### 填写验证码

在我写到末尾时发现需要填写验证码， 参考： [sml2h3/ddddocr: 带带弟弟 通用验证码识别OCR pypi版 (github.com)](https://github.com/sml2h3/ddddocr)， 可以实现验证码识别为字符的操作

### 内容输入

#### 方式一：`pyautogui`

`pyautogui`包可以实现点击指定图片位置的操作，但是这有一个问题， 在不同分辨率下识别程度识别率有限。

* 可以通过`pyautogui.locateCenterOnScreen(img,confidence)`的第二个参数可以调节容错率，这样可以解决部分问题；
* 不然就需要每次重新截图， 针对不同的分别率截一次图。这样做未免太傻了

#### 方式二: `selenium`

考虑再三决定换一种方式去实现。使用`selenium`包配合上`chormedriver`实现自动操作，精确到找到页面中的元素。

* 查找元素：文档中展示了在页面中查找元素的方式。参考：[4. 查找元素 — Selenium-Python中文文档 2 documentation (selenium-python-zh.readthedocs.io)](https://selenium-python-zh.readthedocs.io/en/latest/locating-elements.html)

  ```python
  # 查找单个元素
  find_element_by_id
  find_element_by_name
  find_element_by_xpath
  find_element_by_link_text
  find_element_by_partial_link_text
  find_element_by_tag_name
  find_element_by_class_name
  find_element_by_css_selector
  
  # **一次查找多个元素**
  find_elements_by_name
  find_elements_by_xpath
  find_elements_by_link_text
  find_elements_by_partial_link_text
  find_elements_by_tag_name
  find_elements_by_class_name
  find_elements_by_css_selector
  ```

  *注意：在页面中存在多个符合条件的元素时，若仍使用查找单个元素的方法只会去找第一次出现的位置*

* 等待：在某些跳转页面操作后，若进行报错：会找不到对象，所以需要等待`time.sleep(1)`

* 输入内容：在找到指定位置后，可以选择`click()`点击操作；或是`send_keys(content)`输入指定内容，组合键可以使用`send_keys(Keys.CONTROL, 'a')`方法。参考：[driver.find_element_by_xpath.clear()无法清空输入框默认值 - jjstrip - 博客园 (cnblogs.com)](https://www.cnblogs.com/jjstrip/p/11990447.html?ivk_sa=1024320u)

* 动态等待：有那么一种情况，一些元素需要经过等待才能出现，可以使用`WebDriverWait`来实现，如：`WebDriverWait(driver, 5).until(lambda driver: driver.find_element_by_xpath("xxx"))`, 意为5秒内使用`find_element_by_xpath("xxx")`查找指定元素，若找不到会抛异常TimeoutException。参考：1. [selenium WebDriverWait - 致远方 - 博客园 (cnblogs.com)](https://www.cnblogs.com/fcc-123/p/10942883.html)，2. [python selenium-webdriver 等待时间（七） - 梦雨情殇 - 博客园 (cnblogs.com)](https://www.cnblogs.com/mengyu/p/6972968.html), 3. [WebDriverWait等设置等待时间和超时时间 - 知行Lee - 博客园 (cnblogs.com)](https://www.cnblogs.com/BigFishFly/p/6337153.html)
* 其他：对标签页的关闭和切换参考：[python selenium 对浏览器标签页进行关闭和切换 - mapuboy - 博客园 (cnblogs.com)](https://www.cnblogs.com/mapu/p/8533817.html)

### 异常处理

在发生异常后需要停止，或走其他分支。同其他语言一样使用`try..except...else`来处理异常。

*注意python3总获取异常时需要使用`except Exception as ex`, python2中是使用`except Exception as ex`来捕获*

### 读写本地文件

使用时要注意流的关闭，可以使用自动管理的方式，如读文件：`with open('123.txt', 'r')`，参考：https://[python文件读写小结 - 周洋 - 博客园 (cnblogs.com)](https://www.cnblogs.com/zywscq/p/5441145.html)

### 判断/创建目录

判断/创建目录需要使用`os`包。判断目录：`os.path.exists(dirs)`，创建目录：`os.makedirs(dirs)`。参考：[python 判断目录和文件、文件夹 是否存在，若不存在即创建 - lucky8492 - 博客园 (cnblogs.com)](https://www.cnblogs.com/carey9420/p/12710990.html)

### 随机字符串

这个页比较简单，参考：[python生成随机数、随机字符串 - zqifa - 博客园 (cnblogs.com)](https://www.cnblogs.com/zqifa/p/python-random-1.html)

### 成果

最后贴上我的最终代码

```python
from selenium import webdriver  # 用于打开网站
from selenium.webdriver.support.wait import WebDriverWait   
import pyautogui    # 图片识别
from PIL import Image  # 用于打开图片和对图片处理
import ddddocr  # 用于识别验证码
import time # 代码运行停顿
import random   # 随机数

filename = 'account.txt'

if __name__ == '__main__':
    print('begin!')
    
    with open(filename, 'r') as f1:
            account = f1.readlines()
    # account
    for i in range(0, len(account)):
        account[i] = account[i].rstrip('\n')
    email = account[0]
    password = account[1]
    seedNum = str(random.randint(10000000,9999999999))
    seedStr = random.sample('abcdefghijkmnopqrstuvwxy', random.randint(0,4)) #从里边随机取6-16个元素
    pwd = seedNum + ''.join(seedStr)
    
    try:
        # 准备工作
        # 打开浏览器
        driver = webdriver.Chrome('C:\Program Files\Google\Chrome\Application\chromedriver.exe')
        # 打开登录页
        driver.get("http://127.0.0.1") 
        time.sleep(1)
        
        # 操作
        # 登录
        driver.find_element_by_xpath("//button[@class='layui-btn layui-btn-opacity layui-btn-radius btn-login phone-login']").click()
        time.sleep(1)
        # 账号
        ele = driver.find_element_by_xpath("//input[@type='text']")
        ele.send_keys(email)
        time.sleep(0.5)
        # 密码
        ele = driver.find_element_by_xpath("//input[@type='password']")
        ele.send_keys(password)
        time.sleep(0.5)
        # 登录
        driver.find_element_by_xpath("//button[@class='layui-btn layui-btn-lg layui-btn-fluid']").click()
        time.sleep(1.5)
        # 跳转至安全设置
        driver.get("http://127.0.0.1/security")
        time.sleep(1)
        # 旧密码
        ele = driver.find_element_by_xpath("//input[@class='layui-input']")
        ele.send_keys(password)
        time.sleep(0.5)
        # 新密码
        ele = driver.find_element_by_xpath("//input[@placeholder='6-23位非空字符']")
        ele.send_keys(pwd)
        time.sleep(0.5)
        # 确认密码
        ele = driver.find_elements_by_xpath("//input")[2]
        ele.send_keys(pwd)
        time.sleep(0.5)
        print(pwd)
        # 获取验证码
        driver.find_element_by_link_text("点击获取验证码").click()
        time.sleep(0.5)
        
        #识别验证码
        # 截屏
        driver.save_screenshot('./temp/screen.png')
        # 打开截屏
        screen = Image.open('./temp/screen.png')
        # 获取验证码的大小参数及坐标
        captch = driver.find_element_by_xpath("//body//div[@class='center-main']//img")
        location = captch.location
        size = captch.size
        left = location['x']
        top = location['y']
        right = left + size['width']
        bottom = top + size['height']
        # 按照验证码的长宽，切割验证码，然后保存验证码图片
        screen.crop((left, top, right, bottom)).save('./temp/captch.png')
        time.sleep(0.5)
        # 识别出验证码
        ocr = ddddocr.DdddOcr()
        with open('./temp/captch.png', 'rb') as f:
            img_bytes = f.read()
        code = ocr.classification(img_bytes)
        print(code)
        
        # 输入验证码
        ele = driver.find_element_by_xpath("//input[@placeholder='不区分大小写']")
        ele.send_keys(code)
        # 点击‘修改密码’
        driver.find_element_by_xpath("//button[@class='layui-btn layui-btn-theme']").click()
        print(pwd)
        
        #判断是否成功
        WebDriverWait(driver, 5).until(lambda driver: driver.find_element_by_link_text("点击获取验证码"))
        
    except Exception as e:
        print("修改失败!!!", e)
        print(password)
        print(pwd)
    else:
        # 记录账号密码
        with open(filename, 'w') as f:
            f.write(email)
            f.write('\n')
            f.write(pwd)
        print("修改成功,密码已经记录至 account.txt ")
        
        # 关闭
        #driver.close()
        #driver.quit()
```





> 参考：
>
> 1. [5分钟，教你做个自动化软件拿来办公、刷副本、回微信 | 源码公开，开箱即用_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1T34y1o73U?p=1&share_medium=android_i&share_plat=android&share_source=COPY&share_tag=s_i&timestamp=1648282253&unique_k=QZBrxXS)
> 2. [详解Python中pyautogui库的最全使用方法_studyer_domi的博客-CSDN博客_pyautogui scroll](https://blog.csdn.net/qingfengxd1/article/details/108270159)
> 3. [sml2h3/ddddocr: 带带弟弟 通用验证码识别OCR pypi版 (github.com)](https://github.com/sml2h3/ddddocr)
> 4. [4. 查找元素 — Selenium-Python中文文档 2 documentation (selenium-python-zh.readthedocs.io)](https://selenium-python-zh.readthedocs.io/en/latest/locating-elements.html)
> 5. [driver.find_element_by_xpath.clear()无法清空输入框默认值 - jjstrip - 博客园 (cnblogs.com)](https://www.cnblogs.com/jjstrip/p/11990447.html?ivk_sa=1024320u)
> 6. [selenium WebDriverWait - 致远方 - 博客园 (cnblogs.com)](https://www.cnblogs.com/fcc-123/p/10942883.html)
> 7. [python selenium-webdriver 等待时间（七） - 梦雨情殇 - 博客园 (cnblogs.com)](https://www.cnblogs.com/mengyu/p/6972968.html)
> 8. [WebDriverWait等设置等待时间和超时时间 - 知行Lee - 博客园 (cnblogs.com)](https://www.cnblogs.com/BigFishFly/p/6337153.html)
> 9. [python selenium 对浏览器标签页进行关闭和切换 - mapuboy - 博客园 (cnblogs.com)](https://www.cnblogs.com/mapu/p/8533817.html)
> 10. [python文件读写小结 - 周洋 - 博客园 (cnblogs.com)](https://www.cnblogs.com/zywscq/p/5441145.html)
> 11. [python 判断目录和文件、文件夹 是否存在，若不存在即创建 - lucky8492 - 博客园 (cnblogs.com)](https://www.cnblogs.com/carey9420/p/12710990.html)
> 12. [python生成随机数、随机字符串 - zqifa - 博客园 (cnblogs.com)](https://www.cnblogs.com/zqifa/p/python-random-1.html)

