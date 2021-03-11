# 仓库出入库管理

### 2021/3/8 更新
#### 基础开发环境：由于有小伙伴在运行项目时版本号不一致产生的各种问题，这里可以统一下版本号。
- JDK: 1.8
- Maven: 3.5+
- MySql: 5.7+
- Redis: 3.2 +
- Node Js: 10.0 +
- Npm: 5.6.0+
- Yarn: 1.21.1+
### IDE插件 (lombok 插件必装)
<hr/>

### 2021/3/3 更新
`713091413` 物流项目讨论交流群,有问题的小伙伴可以加此群哦！！！

### 介绍
基于Jeecg-boot开发的物流仓储系统，涵盖模块：用户管理、车辆管理、计划管理、仓库管理、库存管理、财务管理、统计报表、系统管理等模块组成

### 软件架构
- jeecg-boot-master 后台项目
- cable.sql 后台管理系统数据库脚本


### 所用技术
- 此系统基于 Jeecg-boot 为脚手架开发的PRD管理系统
- 后端技术：SpringBoot 2.1.3 + Shiro 1.4.0 + Redis + Mysql 5.7 + MyBatis-Plus 3.1.2 + Jwt 3.7.0 + Swagger-ui
- 前端技术：Vue + Ant-design-vue + Webpack
- 其他技术：Druid(数据库连接池)、Logback(日志工具)、poi(Excel工具)、Quartz(定时任务)、lombok(简化代码)
- 项目构建：Maven3.5+、JDK1.8+

### 项目所需软件下载路径及jeecg文档说明
- [JeecgBoot官方文档](http://jeecg-boot.mydoc.io/)

### 新手必看启动教学
##### 1. 数据库配置
#### 1.1 首先在本地创建 cable 数据库，选择好字符集编码
![输入图片说明](https://images.gitee.com/uploads/images/2020/1123/134217_192e4886_5459645.jpeg "1606109571(1).jpg")
#### 1.2 然后在创建好的 cable 数据库下执行 cable.sql 脚本即可
![输入图片说明](https://images.gitee.com/uploads/images/2020/1123/134333_0de565fa_5459645.jpeg "1606109947(1).jpg")
### 2. 前端项目可以使用 WebStorm 开发工具打开,后端项目可以使用 IDEA 开发工具打开,也可以使用 IDEA 一个工具来进行开发。
#### 2.1. 进入 IDEA 工具后设置 Maven 依赖下载设置
更改自己的 Maven 安装路径，用来下载项目所需的 jar 包
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/162303_6d442bd7_5459645.jpeg "2.jpg")
### 3. 选择后台项目的启动环境 -> dev[开发环境] 或者 prod[生产环境]
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/162540_75a31d7f_5459645.png "3.png")
#### 3.1 然后更改对应开发环境的配置文件，如 application-dev.yml 文件
#### 3.2 配置项目启动端口号
![](https://images.gitee.com/uploads/images/2020/0908/163026_f58e544f_5459645.png "屏幕截图.png")
#### 3.3 配置数据库连接信息
![](https://images.gitee.com/uploads/images/2020/0908/163137_81f31777_5459645.png "屏幕截图.png")
#### 3.4 配置 redis 连接信息
![](https://images.gitee.com/uploads/images/2020/0908/163257_ec9d7035_5459645.png "屏幕截图.png")
#### 3.5 配置 jeecg 专用配置文件上传路径
![](https://images.gitee.com/uploads/images/2020/0908/163408_f590b880_5459645.png "屏幕截图.png")
### 4. 找到 JeecgApplication 启动类启动项目即可

<hr>

### 5. 通过访问 `http://localhost:8080/jeecg-boot/` 可以查看后台 API 接口文档
![](https://images.gitee.com/uploads/images/2020/0908/164142_770af197_5459645.png "屏幕截图.png")
### 6. 前端项目使用 IDEA 打开后,通过执行 `cnpm install`,`npm install`,`yarn install` 等命令都可以下载前端依赖（cnpm 命令是安装了淘宝镜像后可以使用，yarn 命令是安装了 yarn 包后才可以使用, npm 是 node.js 原生自带的命令，直接就可以使用下载依赖，只不过速度比较慢）
### 7. 下载成功后目录下会多出一个 node_modules 的包,这个就是管理前端依赖的包
![](https://images.gitee.com/uploads/images/2020/0908/164349_05bb9650_5459645.png "屏幕截图.png")
### 8. 配置 index.html 页面的全局配置 -> 指定后台路径
![](https://images.gitee.com/uploads/images/2020/0908/164608_ca257c76_5459645.png "屏幕截图.png")
### 9. 配置项目根目录下的 vue.config.js 文件，指定后台路径,建立前后端对接
![](https://images.gitee.com/uploads/images/2020/0908/164711_39ac879c_5459645.png "屏幕截图.png")
### 10. 最后配置完成后，需要前端后端同时启动才能访问 `localhost:3000` 

### 项目截图
### 1. 登录界面
![登录截图](https://images.gitee.com/uploads/images/2020/0628/192351_69d1a279_5459645.jpeg "1.jpg")
### 2. 首页
![首页大数据统计](https://images.gitee.com/uploads/images/2020/0916/184700_11bea32e_5459645.png "屏幕截图.png")
### 3. 系统设置 - 可以更改系统主体颜色设置等等
![系统设置](https://images.gitee.com/uploads/images/2020/0829/131611_5c8e13e6_5459645.jpeg "系统设置.jpg")
### 4. 员工管理模块 
![员工管理](https://images.gitee.com/uploads/images/2020/0829/131638_4fd807ec_5459645.jpeg "员工管理.jpg")
### 5. 角色授权 - 通过分配给用户不同的角色，可访问不同的菜单列表
![角色授权](https://images.gitee.com/uploads/images/2020/0829/131718_1e498bac_5459645.jpeg "角色授权.jpg")
### 6. 计划导出 - 通过 excelPoi 技术实现信息导出功能
![计划导出](https://images.gitee.com/uploads/images/2020/0829/131754_11c8c927_5459645.jpeg "计划导出.jpg")
### 7. 定时任务 - 定时指定某些特定的任务
![定时任务](https://images.gitee.com/uploads/images/2020/0829/131834_ebdb6126_5459645.jpeg "定时任务.jpg")
