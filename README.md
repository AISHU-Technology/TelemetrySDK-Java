### 项目介绍
本项目是基于 Opentelemetry 官方定义的数据模型实现的 SDK，包括以下组件
sdk : 基于 Opentelemetry Log API 的具体实现，用于生成程序日志、业务日志
exporters: 数据导出的具体实现，支持标准输出、上报到数据接收器等导出方式


目前包含Metrics和Trace的实现
### 项目 layout 说明
```
├── exporter             数据导出器
│   ├── ar-metric        指标数据导出到 AnyRobot 数据接收器
│   │   ├── pom.xml
│   │   └── src
│   ├── ar-trace        链路数据导出到 AnyRobot 数据接收器
│   │   ├── pom.xml
│   │   └── src
│   └── common           公共包，封装数据导出协议和编码
│       ├── pom.xml
│       └── src
├── README.md
└── sdk                  SDK
    ├── pom.xml
    └── src
        ├── main
        └── test
```
