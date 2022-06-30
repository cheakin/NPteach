# Git

## 常用

* **查看仓库当前的状态**

  `git status`

* **初始化仓库(本地)**

  `git init`

* **添加文件到git仓库(本地)**	

  提交整个文件夹：`git add .`

  提交指定文件：`git add <file>`

* **提交添加的文件到Git仓库(本地)**

  `git commit`

  或

  ` git commit -m "提交说明"`

* **关联远程仓库**

  `git remote add origin https://gitee.com/ckisaboy/NPtool.git`

* **同步到本地**

  `git pull origin master`

* **推送到远程仓库**

  `git push -u origin master`(第一次加-u，以后提交不需要)
  
  `git push origin <分支名>`

## 创建(本地仓库)

* **初始化仓库(本地)**

  `git init`

* **添加文件到git仓库(本地)**	

  提交整个文件夹：`git add .`

  提交指定文件：`git add <file>`

* **提交添加的文件到Git仓库(本地)**

  `git commit`

  或

  ` git commit -m "提交说明"`

* **克隆一个远程库**

  `git clone https://gitee.com/ckisaboy/NPtool.git`

## 远程仓库

* **关联远程仓库**

  `git remote add origin https://gitee.com/ckisaboy/NPtool.git`

* **删除本地仓库与远程库的关联**

  `git remote add gitee https://gitee.com/ckisaboy/NPtool.git`

* **推送到远程仓库**

  `git push gitee master`(gitee)

  `git push github master`(github)

  *强制提交本地仓库覆盖远程仓库*

  `git push origin master --force`

* **推送到远程仓库**

  `git push -u origin master`(第一次加-u，以后提交不需要)

* **克隆一个远程库**

  `git clone https://gitee.com/ckisaboy/NPtool.git`

## 查看

* **查看仓库当前的状态**

  `git status`

* **比较当前文件的修改**

  `git diff <file>`

* **查看历史提交的记录**

  `git log`

  *这条命令可以看到分支合并图：*

  `git log --graph`

* **查看远程库的信息**

  `git remote`

  或

  `git remote -v`

* **查看分支**

  `git branch`

  *提示：显示的结果中，其中有一个分支前有个*号，表示的是当前所在的分支*

* **查看操作的历史命令记录**

  `git reflog`

## 分支

* **创建并切换到branch1分支**

  `git checkout -b branch1`

  * **创建一个分支branch1(本地）**

    `git branch branch1`

  * **切换到branch1分支**

    `git checkout branch1`

* **创建本地分支**

  `git checkout -b branch1 origin/branch1`

  *如果远程库中有分支，clone之后默认只有master分支的，所以需要执行如上命令来创建本地分支才能与远程的分支关联起来*

* **指定本地branch1分支与远程origin/branch1分支的链接**

  `git branch --set-upstream branch1 origin branch1`

  *如果你本地新建的branch1分支，远程库中也有一个branch1分支(别人创建的)，而刚好你也没有提交过到这个分支，即没有关联过，会报一个`no tracking information`信息，通过上面命令关联即可*

  或

  `git push -u origin branch1`(在本地分支下运行)

  *推送本地branch1分支到远程的branch1分支中（远程会自动创建次分支则)*

* **查看分支**

  `git branch`

  *提示：显示的结果中，其中有一个分支前有个*号，表示的是当前所在的分支*

* **推送分支**

  推送master到远程库

  `git push origin master`

  推送branch1到远程库

  `git push origin branch1`

* **合并branch1分支到master**

  `git merge branch1`

  *本地合并后仍要同步到远程`git push`

* **删除分支**

  `git branch -d branch1`

* **丢弃一个没有被合并过的分支**

  `git branch -D <name>`

  *即强行删除。实际开发中，添加一个新feature，最好新建一个分支，如果要丢弃这个没有被合并过的分支，可以通过上面的命令强行删除。*

## 修改

* **回退版本**

  `git reset --hard HEAD^`

  *在Git中，用HEAD表示当前版本，上一个版本就是HEAD^，上上一个版本就是HEAD^^，以此类推*

* **删除文件**

  `rm <file>`

  *如果不小心删错了，如果还没有提交的话使用下面命令即可恢复删除，注意的是它只能恢复最近版本提交的修改，你工作区的修改是不能被恢复的！*

  `git checkout -- <file>`

* **删除本地仓库与远程库的关联**

  `git remote add gitee https://gitee.com/ckisaboy/NPtool.git`

* **合并branch1分支到master**

  `git merge branch1`

* **删除分支**

  `git branch -d branch1`

* **丢弃一个没有被合并过的分支**

  `git branch -D <name>`

  *即强行删除。实际开发中，添加一个新feature，最好新建一个分支，如果要丢弃这个没有被合并过的分支，可以通过上面的命令强行删除。*

* **丢弃工作区的修改**

  `git checkout -- <file>`

* **丢弃暂存区的文件**

  `git reset HEAD <file>`

## 标签

* **创建标签**

  `git tag <name>`

  *例如：git tag v1.0*

  * 创建带有说明的标签，用-a指定标签名，-m指定说明文字，123456为commit id：

    `git tag -a v1.0 -m "V1.0 released" 123456`

  * 用私钥签名一个标签

    `git tag -s v2.0 -m "signed V2.0 released" 345678`

* **查看所有标签**

  `git tag`

* **对历史提交打tag**

  *先使用`git log --pretty=oneline --abbrev-commit`命令找到历史提交的commit id。例如对commit id 为123456的提交打一个tag：*

  `git tag v0.9 123456`

* **查看标签信息**

  `git show <tagname>`

* **删除标签**

  `git tag -d <tagname>`

  * 删除远程库中的标签

    *比如要删除远程库中的 **V1.0** 标签，分两步*

    [1] 先删除本地标签：` git tag -d V1.0`

    [2] 再推送删除即可：`git push origin :refs/tags/V1.0`

## 设置

* **配置全局用户Name和E-mail**

  ```git config --global user.name "Your Name"
  git config --global user.name "Your Name"
  git config --global user.email "email@example.com"
  ```

* **创建SSH key**

  `ssh-keygen -t rsa -C "youremail@example.com"`

* **Git显示颜色** (会让命令输出看起来更清晰、醒目)

  `git config --global color.ui true`

* **设置命令别名**

  `git config --global alias.st status`

  *global表示全局，即设置完之后全局生效，st表示别名，status表示原始名。此时，现在敲`git st`就相当于是`git status`命令了*

* **忽略文件规则**

  原则：

  ​	忽略系统自动生成的文件等；

  ​	忽略编译生成的中间文件、可执行文件等，比如Java编译产生的.class文件，自动生成的文件就没必要提交；

  ​	忽略你自己的带有敏感信息的配置文件，个人相关配置文件；

  ​	忽略与自己相关开发环境相关的配置文件；

  使用：

  ​	在Git工作区的根目录下创建一个特殊的 **.gitignore** 文件，然后把要忽略的文件名或者相关规则填进去，Git就会自动忽略这些文件，不知道怎么写的可参考：[github.com/github/giti…](https://github.com/github/gitignore),这里提供了一些忽略的规则，可供参考；

  如果你想添加一个被 **.gitignore** 忽略的文件到Git中，但发现是添加不了的，所以我们可以使用强制添加:

  ` git add -f <file>`

  *或者我们可以检查及修改 **.gitignore** 文件的忽略规则*

  `git check-ignore -v <file>`

* **忽略已经提交到远程库中的文件**

  比如说：我们要忽略.idea目录，先删除已经提交到本地库的文件目录

  `git rm --cached .idea`

  加个参数 -r 即可强制删除`git rm -r --cached .idea`。执行`git status`会提示你已经删除.idea目录了。

附图：

![160c049ccf1e2bd9](E:%5C%E5%9B%BE%E7%89%87%5C%E4%B8%B4%E6%97%B6%E5%9B%BE%E7%89%87%5C160c049ccf1e2bd9.jpg)

## 常见问题

1. **Reinitialized existing Git repository**

   “git init” 的时候出现：Reinitialized existing Git repository

   

2. 解决方法：可以使用 rm -rf .git，删除.git，然后在 git init 即可

1. 