# 📋 医院管理系统

## Hospital Management System · 2025 Web 框架编程期末大作业

🚀 **基于 Spring Boot 3 + Vue 3 的前后端分离医院管理系统**



## 📖 项目简介

本项目采用当下主流的 **前后端分离架构** 开发，适合作为
**《Web 框架编程》课程期末大作业 / 综合实训项目**。

* 后端负责业务逻辑与数据处理
* 前端负责页面展示与交互
* 前后端通过 RESTful API 进行通信



## 🛠️ 技术栈与版本（必看）

> ⚠️ **请各位组员严格保证本地环境符合以下版本要求，否则项目可能无法运行**

### 🔧 后端（Backend）

| 模块  | 技术           | 版本要求             | 备注               |
| --- | ------------ | ---------------- | ---------------- |
| JDK | Java         | **JDK 21 (LTS)** | 必须安装             |
| 框架  | Spring Boot  | **3.2.0**        | 已在 `pom.xml` 固定  |
| ORM | MyBatis-Plus | **3.5.7**        | 数据持久层            |
| 数据库 | MySQL        | **8.0**          | 推荐 ≥ 8.0（最低 5.7） |



### 🎨 前端（Frontend）

| 模块      | 技术           | 版本要求      | 备注                         |
| ------- | ------------ | --------- | -------------------------- |
| Node.js | Node         | **18.0+** | 推荐 v18 / v20 (LTS)         |
| 框架      | Vue          | **3.3+**  | 组合式 API (`<script setup>`) |
| 构建工具    | Vite         | **5.x**   | 极速开发体验                     |
| UI 组件   | Element Plus | 最新版       | Vue 3 官方组件库                |



## 🚀 快速启动（Quick Start）



### 1️⃣ 准备数据库

启动 MySQL，创建数据库：

```sql
CREATE DATABASE hospital
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

然后在 **`backend/src/main/resources/application.yml`** 中
修改你本地的数据库用户名和密码。



### 2️⃣ 启动后端（Backend）

1. 使用 **IntelliJ IDEA** 打开 `backend` 文件夹
   （建议以 **Maven 项目** 导入）
2. 等待 Maven 依赖下载完成
3. 找到并运行：

```java
HospitalApplication.java
```

4. 看到如下日志表示启动成功：

```text
Started HospitalApplication in x.xx seconds
```

📌 **后端访问地址：**

```
http://localhost:8080
```



### 3️⃣ 启动前端（Frontend）

1. 使用 **VS Code** 打开 `frontend` 文件夹
2. 打开终端，安装依赖：

```bash
npm install
```

3. 启动开发服务器：

```bash
npm run dev
```

4. 浏览器访问：

```
http://localhost:5173
```


## 📂 项目结构

```plaintext
Hospital-System-Project
├── backend/                 # 后端代码 (Spring Boot)
│   ├── src/main/java/com/example/hospital
│   │   ├── config/          # 配置类 (CORS / Swagger 等)
│   │   ├── controller/      # 控制层 (API 接口)
│   │   ├── entity/          # 实体类 (数据库表映射)
│   │   ├── mapper/          # DAO 层 (MyBatis 接口)
│   │   └── service/         # 业务逻辑层
│   └── src/main/resources
│       └── application.yml # 核心配置文件
│
└── frontend/                # 前端代码 (Vue 3)
    ├── src
    │   ├── api/             # API 请求封装 (如 patient.js)
    │   ├── utils/           # 工具类 (request.js 拦截器)
    │   └── views/           # 页面视图
    │       └── patient/
    │           └── index.vue
    └── package.json
```



## ⚠️ 开发规范（致组员）

为了避免代码冲突，请 **严格遵守以下规则**：



### ✅ 1️⃣ 统一模块（强烈建议）

目前 **病人管理（Patient）模块** 已由 **Member A** 完成并测试通过。

开发新模块时，请 **直接复制 Patient 模块进行修改**：

#### 后端

```text
PatientController  →  DoctorController
PatientMapper      →  DoctorMapper
PatientEntity      →  DoctorEntity
```

#### 前端

```text
api/patient.js            → api/doctor.js
views/patient/index.vue  → views/doctor/index.vue
```



### ✅ 2️⃣ 接口命名规范（RESTful）

| 功能   | 请求方式   | 接口示例           |
| ---- | ------ | -------------- |
| 列表查询 | GET    | `/doctor/list` |
| 新增   | POST   | `/doctor`      |
| 修改   | PUT    | `/doctor`      |
| 删除   | DELETE | `/doctor/{id}` |



### ✅ 3️⃣ 遇到报错怎么办？

**排查顺序：**

1. 看 **IDEA 后端控制台** 是否有红色报错
2. 看 **浏览器 F12 → Console / Network**
3. 如果前端显示 `Network Error`
   → 检查后端是否启动
4. 如果数据库连接失败
   → 检查 `application.yml` 中数据库密码


### ✅ 4️⃣ 怎么更改数据库？

**操作流程：**

**保留原文件： ** query.sql 留在那里不动，仅用于第一次搭建环境。

**新建补丁文件：**

如果队友 B 要写医生模块，需要建一张 doctor 表。

不要动别人的文件，而是新建一个文件：sql/doctor_table.sql。

如果队友 C 发现 patient 表少了个“身份证号”字段，需要加一列。

新建一个文件：sql/update_patient_idcard.sql (里面写 ALTER TABLE)。

执行： 大家都只运行自己缺的那个 .sql 文件。

优点： 历史清晰，不会覆盖数据，不会打架。


## 📅 任务分配

* [x] 基础架构搭建（Member A）✅
* [x] 病人管理模块（Member A）✅
* [ ] 医生管理模块（Member B）🚧
* [ ] 挂号 / 排班模块（Member C）🕒
* [ ] 药品 / 收费模块（Member D）🕒
* [ ] 系统登录 / 权限模块（Member E）🕒



## ✨ 特别提示

> 本项目仅用于课程学习与教学演示。



