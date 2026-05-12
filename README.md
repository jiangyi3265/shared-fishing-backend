# 共享钓场后端服务

共享钓场后端服务负责用户端小程序和管理后台的统一业务接口，覆盖用户登录、钓场计时、订单结算、活动报名、优惠券、权限管理、系统监控与基础数据维护等能力。项目基于 RuoYi 后端结构改造，适合作为共享钓场业务的服务端基础。

## 技术栈

- Java 8
- Spring Boot 2.5.15
- Spring Security + JWT
- MyBatis + PageHelper
- MySQL + Druid
- Redis
- Swagger 3
- Maven 多模块

## 关联仓库

| 子项目 | 仓库 | 说明 |
| --- | --- | --- |
| 后端服务 | [shared-fishing-backend](https://github.com/jiangyi3265/shared-fishing-backend) | 提供业务接口、权限、订单、计时和系统管理能力 |
| 管理后台 | [shared-fishing-admin](https://github.com/jiangyi3265/shared-fishing-admin) | 面向钓场管理员的 Web 管理端 |
| 用户端 | [shared-fishing-app](https://github.com/jiangyi3265/shared-fishing-app) | 面向钓场用户的 uni-app / 微信小程序端 |

## 快速启动

1. 准备 MySQL 和 Redis。
2. 导入数据库脚本：

```bash
mysql -uroot -p ha < sql/fishing-full.sql
```

3. 修改数据库与 Redis 配置：

```text
ruoyi-admin/src/main/resources/application-druid.yml
ruoyi-admin/src/main/resources/application.yml
```

4. 构建并启动服务：

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

5. 默认接口地址：

```text
http://localhost:8080
```

## 简历描述示例

基于 Spring Boot 和 RuoYi 多模块架构实现共享钓场后端服务，支持扫码计时、订单结算、权限控制、后台管理和系统监控等核心业务。通过 MySQL、Redis、JWT 和 Swagger 组合完成业务数据管理、登录鉴权与接口联调支撑。
