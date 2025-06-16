# INF专用框架

## 简介

```
此为inf-folia插件开发框架
默认配置：1. 配置文件外放
	 2. 保存其他配置文件(saveConfig())
	 3. 注册指令方法(registerCommands())
	 4. 注册监听器方法(registerAListener())
	 5. 设置默认配置文件
	 6. 实例获取
```

## 1. 保存其他配置文件

```
saveConfig()；

使用说明：
默认方法 List<String> fileNames = new ArrayList<>();
向 fileNames中添加文件名即可

fileNames.add("xxx.yml");
```

## 2. 注册指令

```
registerCommands();

使用说明：
在方法内新增代码 getCommand("inf").setExecutor(new 类());
其中inf是你实际的指令
```
