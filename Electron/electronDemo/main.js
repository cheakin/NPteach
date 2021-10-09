const electron = require('electron')
const app = electron.app  //主进程
const BrowserWindow = electron.BrowserWindow  //新窗口
const globalShortcut = electron.globalShortcut  //全局快捷键

var mainWindow = null //声明要打开的主要窗口

app.on('ready', () => {
  mainWindow = new BrowserWindow({
    width:1000, 
    height:800,
    webPreferences:{
      nodeIntegration: true,  //设置开启nodejs环境
      contextIsolation: false,  //解决require问题
      enableRemoteModule: true  //解决remote问题
    }
  })
  //解决remote问题
  require('@electron/remote/main').initialize() //加载remote
  require('@electron/remote/main').enable(mainWindow.webContents)
  // 注册菜单
  require('./menu.js')

  // 注册全局快捷键
  globalShortcut.register('ctrl+b', () => {
    mainWindow.loadURL('https://www.baidu.com')
  })
  //验证是否注册成功
  let isRegister = globalShortcut.isRegistered('ctrl+b')?'Register Success':'Register Fail'
  console.log('isRegister----->', isRegister);

  mainWindow.loadFile('index.html') //加载页面
  mainWindow.openDevTools() //打开控制台

  var BrowserView = electron.BrowserView
  var view = new BrowserView()
  mainWindow.setBrowserView(view)
  view.setBounds({x:0, y:520,width:500,height:680})
  view.webContents.loadURL('https://www.baidu.com')

  mainWindow.on('window-all-closed', ()=>{
    if (process.platform != 'darwin') {
      app.quit
    }
  })
})

// 注销快捷键
app.on('will-quit', () => {
  globalShortcut.unregister('ctrl+b')
  globalShortcut.unregisterAll()
})


//TODO electron-forge

//TODD 插件https://github.com/nklayman/vue-cli-plugin-electron-builder
