const { Menu, BrowserWindow } = require('electron')
  var template = [
    {
      label: '栏目一',
      submenu: [
        {label: '栏目1.1'},
        {label: '栏目1.2'},
        {
          label: 'chile.html',
          accelerator: 'ctrl+n',
          click: ()=>{
            var win = new BrowserWindow({
              width: 500,
              height: 500,
              webPreferences: {
                nodeIntegration: true,  //设置开启nodejs环境
                contextIsolation: false,  //解决require问题
              }
            })
            win.loadFile('child.html')
            win.on('close', () => {
              win = null
            })
          }
        }
      ]
    },
    {
      label: '操作',
      submenu: [
        {label: '操作1'},
        {label: '操作2'}
      ]
    }
  ]
  var menu = Menu.buildFromTemplate(template)

  Menu.setApplicationMenu(menu)