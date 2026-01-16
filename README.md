# Contract Management

合同管理服务是 ContractReviewer 平台的核心微服务，负责合同全生命周期管理。基于领域驱动设计（DDD）架构，提供合同元数据管理、标签分类、文件附件关联和审计追踪等功能。

## 开源协议

本项目采用 [Apache License 2.0](LICENSE) 开源协议。

## 技术栈

- **Java 17** + Spring Boot 3.5.0
- **PostgreSQL** + MyBatis Plus
- **Redis** 缓存 + **RabbitMQ** 消息队列
- **Flyway** 数据库迁移
- **MapStruct** 对象映射
- **Resilience4j** 熔断降级

## 配置说明

在启动服务前，您需要配置以下环境变量或修改配置文件中的默认值：

### 必须配置的环境变量

| 环境变量 | 说明 | 示例 |
|---------|------|------|
| `DB_URL` | PostgreSQL 数据库连接地址 | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USERNAME` | 数据库用户名 | `postgres` |
| `DB_PASSWORD` | 数据库密码 | `your-db-password` |
| `COS_SECRET_ID` | 腾讯云 COS Secret ID | `your-cos-secret-id` |
| `COS_SECRET_KEY` | 腾讯云 COS Secret Key | `your-cos-secret-key` |
| `JWT_SECRET` | JWT 认证密钥 | `your-jwt-secret-key` |

### 可选配置

| 环境变量 | 说明 | 默认值 |
|---------|------|--------|
| `NACOS_SERVER` | Nacos 服务地址 | `localhost:18848` |
| `NACOS_USERNAME` | Nacos 用户名 | `nacos` |
| `NACOS_PASSWORD` | Nacos 密码 | `your-nacos-password` |
| `REDIS_HOST` | Redis 主机地址 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | 空 |
| `RABBITMQ_HOST` | RabbitMQ 主机地址 | `localhost` |
| `RABBITMQ_PORT` | RabbitMQ 端口 | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ 用户名 | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ 密码 | `guest` |
| `IMAGE_BASE_URL` | 图片服务地址 | `https://your-image-server.com/load_image/` |

### 配置方式

您可以通过以下方式配置：

1. **环境变量**（推荐）：
   ```bash
   export DB_PASSWORD=your-db-password
   export COS_SECRET_ID=your-cos-secret-id
   export COS_SECRET_KEY=your-cos-secret-key
   export JWT_SECRET=your-jwt-secret-key
   ```

2. **直接修改配置文件**：
   编辑 `src/main/resources/application.yml`，将 `your-*` 占位符替换为实际值。

## 快速开始

### 环境依赖

确保本地已安装 Java 17、Maven 3.8+、PostgreSQL、Redis 和 RabbitMQ。

### 编译运行

```bash
# 编译项目
mvn clean install

# 执行数据库迁移
mvn flyway:migrate

# 启动服务
mvn spring-boot:run
```

服务启动后访问 `http://localhost:11000/contract-management/swagger-ui.html` 查看 API 文档。
