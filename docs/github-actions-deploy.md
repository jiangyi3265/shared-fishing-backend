# GitHub Actions 部署说明

这个项目拆成两个 GitHub 仓库部署：

- 后端仓库：`shared-fishing-backend`，部署 `dy.jar` 到 `/www/wwwroot/dyjava`
- 管理端仓库：`shared-fishing-admin`，部署 `dist` 到 `/www/wwwroot/ht.diaoyuus.cn`

推送到 `main` 会自动部署，也可以在 GitHub 的 Actions 页面手动运行 workflow。

## 服务器准备

服务器需要先准备好 Java 8、MySQL、Redis、Nginx，以及后端运行目录：

```bash
mkdir -p /www/wwwroot/dyjava
mkdir -p /www/wwwroot/ht.diaoyuus.cn
```

后端默认会用仓库里的 `ry.sh` 重启。如果你已经配置了 systemd，可以在 Secrets 里设置 `BACKEND_SERVICE`，例如 `fishing-backend`。

## 后端 Secrets

在后端 GitHub 仓库进入 `Settings -> Secrets and variables -> Actions`，添加：

| Secret | 必填 | 说明 |
| --- | --- | --- |
| `SSH_HOST` | 是 | 服务器 IP 或域名 |
| `SSH_USER` | 是 | SSH 用户 |
| `SSH_PORT` | 否 | SSH 端口，默认 `22` |
| `SSH_PASSWORD` | 二选一 | SSH 密码 |
| `SSH_PRIVATE_KEY` | 二选一 | SSH 私钥，推荐用这个替代密码 |
| `BACKEND_ENV` | 否 | `.env.production` 的完整内容；不填时需要提前放到服务器 `/www/wwwroot/dyjava/.env.production` |
| `BACKEND_SERVICE` | 否 | systemd 服务名，不带 `.service`；不填则执行 `ry.sh restart` |
| `BACKEND_HEALTH_URL` | 否 | 探活地址，默认 `http://127.0.0.1:${SERVER_PORT}/app/venue/default` |
| `WX_PAY_PRIVATE_KEY_B64` | 否 | 微信支付商户私钥文件的 base64 内容 |
| `WX_PAY_PUBLIC_KEY_B64` | 否 | 微信支付公钥文件的 base64 内容 |

为了兼容旧版部署方式，如果没有配置 `BACKEND_ENV`，workflow 会自动从下面这些独立 Secrets 生成 `.env.production`：

```text
SERVER_PORT, DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD,
REDIS_HOST, REDIS_PORT, REDIS_DATABASE, REDIS_PASSWORD,
TOKEN_SECRET, CORS_ALLOWED_ORIGINS, DRUID_STAT_USER, DRUID_STAT_PASSWORD,
WX_APPID, WX_SECRET, WX_PAY_MCH_ID, WX_PAY_APIV3, WX_PAY_NOTIFY,
WX_PAY_CERT_SERIAL, WX_PAY_PUBLIC_KEY_ID, WEATHER_QWEATHER_KEY
```

如果使用 `WX_PAY_PRIVATE_KEY_B64` 和 `WX_PAY_PUBLIC_KEY_B64`，workflow 会把证书写到：

```text
/www/wwwroot/dyjava/certs/apiclient_key.pem
/www/wwwroot/dyjava/certs/wechatpay_public_key.pem
```

对应的 `BACKEND_ENV` 里建议这样写：

```env
WX_PAY_PRIVATE_KEY=/www/wwwroot/dyjava/certs/apiclient_key.pem
WX_PAY_PUBLIC_KEY=/www/wwwroot/dyjava/certs/wechatpay_public_key.pem
```

## 管理端 Secrets

管理端仓库也添加同一组 SSH Secret：

| Secret | 必填 | 说明 |
| --- | --- | --- |
| `SSH_HOST` | 是 | 服务器 IP 或域名 |
| `SSH_USER` | 是 | SSH 用户 |
| `SSH_PORT` | 否 | SSH 端口，默认 `22` |
| `SSH_PASSWORD` | 二选一 | SSH 密码 |
| `SSH_PRIVATE_KEY` | 二选一 | SSH 私钥 |

管理端生产环境接口前缀来自 `.env.production`：

```env
VITE_APP_BASE_API = '/prod-api'
```

Nginx 需要把 `/prod-api/` 反代到后端端口。

## Nginx 示例

```nginx
server {
    listen 443 ssl http2;
    server_name ht.diaoyuus.cn;

    root /www/wwwroot/ht.diaoyuus.cn;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        rewrite ^/prod-api/(.*)$ /$1 break;
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```
