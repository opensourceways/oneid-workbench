# secGear

## 目录
- [获取用户信息](#获取用户信息)
- [获取用户权限](#获取用户权限)

---

## 获取用户信息
提供数据查询服务的外部接口，支持通过ID精确检索数据记录


### 基础信息
| 项目        | 说明                       |
|------------|--------------------------|
| 接口地址     | `/api/v1/data/{data_id}` |
| 请求方法     | POST                     |
| 认证方式     | Token             |


### 认证方式
1. 在个人中心获取API Token
2. 在请求头中添加：
```http
access_token: your_api_token_here
```

### 参数说明
#### 请求参数
| 参数名    | 类型    | 必选/可选/条件可选 | 说明               |
|-----------|---------|------------|--------------------|
| detail    | boolean | 必选         | 是否返回完整详细信息 |

#### 请求头
```http
Authorization: Bearer <your_access_token>
Content-Type: application/json
```

#### 响应参数
| 字段名     | 类型   | 说明                     |
|------------|--------|--------------------------|
| code       | int    | 状态码                   |
| data       | object | 返回数据主体             |
| message    | string | 操作结果描述             |

### 错误码说明
| 状态码 | 说明                   | 解决方案                |
|--------|------------------------|-------------------------|
| 401    | 未授权的访问            | 检查Token有效性         |
| 404    | 数据不存在              | 验证数据ID是否正确      |
| 429    | 请求频率过高            | 降低调用频率            |
| 500    | 服务器内部错误          | 联系技术支持            |

### 使用实例

```
Request: POST /api/v1/XXX
{
    "sdfs": "fsf"
}

Response: 
{
    "data": ""
}
```

---

## 获取用户权限
提供数据查询服务的外部接口，支持通过ID精确检索数据记录


### 基础信息
| 项目        | 说明                       |
|------------|--------------------------|
| 接口地址     | `/api/v1/data/{data_id}` |
| 请求方法     | POST                     |
| 认证方式     | Token             |


### 认证方式
1. 在个人中心获取API Token
2. 在请求头中添加：
```http
access_token: your_api_token_here
```

### 参数说明
#### 请求参数
| 参数名    | 类型    | 必选/可选/条件可选 | 说明               |
|-----------|---------|------------|--------------------|
| detail    | boolean | 必选         | 是否返回完整详细信息 |

#### 请求头
```http
Authorization: Bearer <your_access_token>
Content-Type: application/json
```

#### 响应参数
| 字段名     | 类型   | 说明                     |
|------------|--------|--------------------------|
| code       | int    | 状态码                   |
| data       | object | 返回数据主体             |
| message    | string | 操作结果描述             |

### 错误码说明
| 状态码 | 说明                   | 解决方案                |
|--------|------------------------|-------------------------|
| 401    | 未授权的访问            | 检查Token有效性         |
| 404    | 数据不存在              | 验证数据ID是否正确      |
| 429    | 请求频率过高            | 降低调用频率            |
| 500    | 服务器内部错误          | 联系技术支持            |

### 使用实例

```
Request: POST /api/v1/XXX
{
    "sdfs": "fsf"
}

Response: 
{
    "data": ""
}
```