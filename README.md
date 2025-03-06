# oneid-workbench

#### 介绍

oneid-workbench是用来提供个人工作台功能的服务。

#### 软件架构

* SpringBoot

* mysql


#### 安装教程

##### 基本安装

1. 克隆工程
    > git clone https://github.com/opensourceways/oneid-workbench.git

2. 打包方式
    > mvn clean install package -Dmaven.test.skip

3. 启动应用
    > java -jar target/oneid-workbench.jar


##### 容器安装

1. 克隆工程
    > git clone https://github.com/opensourceways/oneid-workbench.git

2. 打包方式
    * 用Docker打包（到webserver目录中， 执行Dockerfile文件： docker build -t oneid-workbench . ）
    * 注意：DcokerFile中"RUN git clone git@github.com:opensourceways/oneid-workbench.git"仅用于元旦数据获取，自己本地打镜像时可删除

3. 启动应用
    * Docker run -d -v /home/config.properties:/var/lib/oneid-workbench/config.properties 容器名称



#### 使用说明


