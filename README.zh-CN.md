# 🤖 Embabel Agent 示例

<img align="left" src="https://github.com/embabel/embabel-agent/blob/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg?raw=true" width="180">

![Build](https://github.com/embabel/embabel-agent-examples/actions/workflows/maven.yml/badge.svg)

[//]: # ([![Quality Gate Status]&#40;https://sonarcloud.io/api/project_badges/measure?project=embabel_embabel-agent&metric=alert_status&token=d275d89d09961c114b8317a4796f84faf509691c&#41;]&#40;https://sonarcloud.io/summary/new_code?id=embabel_embabel-agent&#41;)

[//]: # ([![Bugs]&#40;https://sonarcloud.io/api/project_badges/measure?project=embabel_embabel-agent&metric=bugs&#41;]&#40;https://sonarcloud.io/summary/new_code?id=embabel_embabel-agent&#41;)

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)
![Jinja](https://img.shields.io/badge/jinja-white.svg?style=for-the-badge&logo=jinja&logoColor=black)
![JSON](https://img.shields.io/badge/JSON-000?logo=json&logoColor=fff)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-black?style=for-the-badge&logo=sonarqube&logoColor=4E9BCD)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)


&nbsp;&nbsp;&nbsp;&nbsp;

<br/>

[English](./README.md) · **简体中文**  

通过 **Spring Framework** 和 **Java** 或 **Kotlin** 学习智能体 AI 开发。
这些示例展示了如何构建能够规划、执行工作流、使用工具并与人类互动的智能体。

> 本仓库使用最新的 Embabel 快照来展示当前的最佳实践，
> 而 [Java](https://github.com/embabel/java-agent-template) 和 
> [Kotlin](https://github.com/embabel/kotlin-agent-template) 模板仓库则使用最新的里程碑版本以获得更好的稳定性。
> 可能存在一些轻微的 API 不兼容性，您在这些模板中看到的并非所有内容都能在您自己的项目中正常工作，
> 除非您在 POM 文件中升级 `embabel-agent.version` 属性，如本仓库所示。



## 🚀 快速开始

### 前提条件
- **Java 21+**
- **API 密钥**（任意一个）：[OpenAI](https://platform.openai.com/api-keys) 
或 [Anthropic](https://www.anthropic.com/api)
- **Maven 3.9+**（可选 - 项目包含 Maven 包装器）

### 1. 克隆并构建
> 请确认您的 Maven 配置可以访问 `repo.embabel.com` 上的 Maven 私服。
```bash
git clone https://github.com/embabel/embabel-agent-examples.git
cd embabel-agent-examples
./mvnw clean install    # Unix/Linux/macOS 
mvnw.cmd clean install  # Windows
```

### 2. 设置 API 密钥
```bash
# 必需（选择一个或两个）
export OPENAI_API_KEY="your_openai_key"
export ANTHROPIC_API_KEY="your_anthropic_key"
```

### 3. 运行示例
#### **Kotlin 示例**
```bash
cd scripts/kotlin
./shell.sh                    # Unix/Linux/macOS - 带有 Docker 工具（默认）
shell.cmd                     # Windows - 带有 Docker 工具（默认）

./shell.sh --no-docker-tools  # Unix/Linux/macOS - 仅基础功能
shell.cmd --no-docker-tools   # Windows - 仅基础功能
```

#### **Java 示例**
```bash
cd scripts/java
./shell.sh                    # Unix/Linux/macOS - 带有 Docker 工具（默认）
shell.cmd                     # Windows - 带有 Docker 工具（默认）

./shell.sh --no-docker-tools  # Unix/Linux/macOS - 仅基础功能
shell.cmd --no-docker-tools   # Windows - 仅基础功能
```

### 创建您自己的项目

您可以从我们的 [Java](https://github.com/embabel/java-agent-template) 或 [Kotlin](https://github.com/embabel/kotlin-agent-template) GitHub 模板创建自己的 Agent 仓库，点击"Use this template"按钮。

您还可以使用我们的快速启动工具在本地创建自己的 Embabel Agent 项目，该工具允许自定义：
```
uvx --from git+https://github.com/embabel/project-creator.git project-creator
```
选择 Java 或 Kotlin，指定您的项目名称和包名称，如果您已经拥有 `OPENAI_API_KEY` 且安装了 Maven，您将在一分钟内启动一个智能体。

## 🆕 **Spring Boot 集成架构**

### **Embabel Agent Starter 指南：**

#### **`embabel-agent-starter`**

- ✅ 应用程序自定义启动模式（控制台、Web应用程序等）
- ✅ 智能体的发现与注册
- ✅ 在智能体平台中Bean可通过依赖注入机制供应用程序按需使用
- ✅ 进度跟踪和日志记录
- ✅ 开发者友好的错误处理

#### **`embabel-agent-starter-shell`**

- ✅ 交互式命令行界面
- ✅ 智能体的发现与注册
- ✅ 人机交互能力
- ✅ 进度跟踪和日志记录
- ✅ 开发者友好的错误处理

#### **`embabel-agent-starter-mcpserver`**

- ✅ MCP协议服务器端实现
- ✅ 工具的注册与发现
- ✅ 通过SSE协议（服务器发送事件）进行JSON-RPC通信
- ✅ 与MCP兼容的客户端集成
- ✅ 安全性与沙箱化


### **应用示例**

#### 带日志主题的 Shell 模式

```kotlin
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.embabel.example"])
class KotlinAgentShellApplication


fun main(args: Array<String>) {
    runApplication<KotlinAgentShellApplication>(*args) {
        setDefaultProperties(
            mapOf("embabel.agent.logging.personality" to LoggingThemes.STAR_WARS)
        )
    }
}
```

```java
// Java version
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"com.embabel.example"})
public class JavaAgentShellApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JavaAgentShellApplication.class);
        app.setDefaultProperties(Map.of(
            "embabel.agent.logging.personality", LoggingThemes.STAR_WARS
        ));
        app.run(args);
    }
}
```

#### Docker 工具支持的 Shell 模式

```kotlin
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.embabel.example"])
class KotlinAgentShellApplication

fun main(args: Array<String>) {
    runApplication<KotlinAgentShellApplication>(*args) {
        setAdditionalProfiles(McpServers.DOCKER)  // Activates application-docker-ce.yml
        setDefaultProperties(
            mapOf("embabel.agent.logging.personality" to LoggingThemes.SEVERANCE)
        )
    }
}
```

#### MCP 服务器模式

```kotlin
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.embabel.example"])
class KotlinAgentMcpServerApplication

fun main(args: Array<String>) {
    runApplication<KotlinAgentMcpServerApplication>(*args) {
        setAdditionalProfiles(McpServers.DOCKER, McpServers.DOCKER_DESKTOP)
    }
}
```

### **配置指南**

#### 日志个性

通过 `embabel.agent.logging.personality` 属性设置：

* `"starwars"` - 愿原力与你的日志同在！
* `"severance"` - 欢迎来到卢蒙工业（默认）

#### MCP 客户端集成

通过在 `application-{profile}.yml` 中定义 Spring AI MCP 客户端连接的 Spring Profile 进行配置：

```yaml
# application-docker-ce.yml
spring:
  ai:
    mcp:
      client:
        type: SYNC
        stdio:
          connections:
            docker-mcp:
              command: docker
              args: [mcp, gateway, run]
```

## 设置 MCP 工具

一些示例使用模型上下文协议（MCP）来访问工具和服务。

默认源是与 Docker Desktop 一起安装的 Docker Desktop MCP 服务器。

为了确保工具可用且启动不会超时，请首先通过以下方式获取模型：
```bash
docker login
docker mcp gateway run
```
当网关启动后，您可以终止其运行并启动 Embabel 服务器。

## 📚 按学习阶段分类的示例

### 🌟 **初学者: 星座新闻智能体**
> **可用语言:** Java 和 Kotlin | **概念:** 基本智能体工作流

一个有趣的智能体开发入门，能够根据某人的星座找到个性化新闻。

**学习内容:**
- 📋 **基于动作的工作流**，使用 `@Action` 注解
- 🔍 **从用户输入中提取数据**，使用 LLM
- 🌐 **网络工具集成**，寻找新闻故事
- 📝 **带有个性和上下文的内容生成**
- 🎯 **通过 `@AchievesGoal` 达成目标**

**工作原理:**
1. 从用户输入中提取姓名
2. 获取他们的星座（如有必要通过表单）
3. 检索每日星座运势
4. 在网上搜索相关新闻故事
5. 创建结合星座运势和新闻的有趣写作

**试试看:**

启动智能体 shell，然后输入：
```bash
x "Find horoscope news for Alice who is a Gemini"
```
`x` 是 `execute` 的缩写，触发智能体运行其工作流。

**代码位置:**
- **Kotlin:** `examples-kotlin/src/main/kotlin/com/embabel/example/horoscope/StarNewsFinder.kt`
- **Java:** `examples-java/src/main/java/com/embabel/example/horoscope/StarNewsFinder.java`

**核心模式:**
```kotlin
@Agent(description = "Find news based on a person's star sign")
class StarNewsFinder {
    
    @Action
    fun extractPerson(userInput: UserInput, context: OperationContext): Person?
    
    @Action(toolGroups = [CoreToolGroups.WEB])
    fun findNewsStories(person: StarPerson, horoscope: Horoscope, context: OperationContext): RelevantNewsStories
    
    @AchievesGoal(description = "Create an amusing writeup")
    @Action
    fun starNewsWriteup(/* params */): Writeup
}
```

### 🔬 **专家: 多LLM研究智能体**
> **可用语言:** Kotlin | **概念:** 自我改进的 AI 工作流

一个先进的研究智能体，使用多个 AI 模型并具有自我批评能力。

**学习内容:**
- 🧠 **多模型共识**（GPT-4 + Claude 协同工作）
- 🔍 **自我改进循环**，带有批评和重试
- ⚙️ **基于配置的行为**，使用 Spring Boot 属性
- 🌊 **并行处理**研究任务
- 📝 **质量控制**通过自动审核

**架构:**
```kotlin
@ConfigurationProperties(prefix = "embabel.examples.researcher")
data class ResearcherProperties(
    val maxWordCount: Int = 300,
    val claudeModelName: String = AnthropicModels.CLAUDE_HAIKU_4_5,
    val openAiModelName: String = OpenAiModels.GPT_41_MINI
)
```

**自我改进模式:**
```kotlin
@Action(outputBinding = "gpt4Report")
fun researchWithGpt4(/* params */): SingleLlmReport

@Action(outputBinding = "claudeReport") 
fun researchWithClaude(/* params */): SingleLlmReport

@Action(outputBinding = "mergedReport")
fun mergeReports(gpt4: SingleLlmReport, claude: SingleLlmReport): ResearchReport

@Action
fun critiqueReport(report: ResearchReport): Critique

@AchievesGoal(description = "Completes research with quality assurance")
fun acceptReport(report: ResearchReport, critique: Critique): ResearchReport
```

**试试看:**
```bash
"Research the latest developments in renewable energy adoption"
```
**代码位置:** `examples-kotlin/src/main/kotlin/com/embabel/example/researcher/`

### ✅ **专家: 事实核查智能体（DSL 风格）**
> **可用语言:** Kotlin | **概念:** 函数式智能体构建

一个基于 Embabel 的函数式 DSL 方法构建的事实验证智能体。

**学习内容:**
- 🔧 **基于函数的 DSL 构建** 智能体
- 🔍 **多个声明的并行事实验证**
- 📊 **置信度评分** 和来源信任评估
- 🌐 **网络研究集成**以进行验证
- ⚡ **智能体设计中的函数式编程模式**

**DSL 构建:**
```kotlin
fun factCheckerAgent(llms: List<LlmOptions>, properties: FactCheckerProperties) = 
agent(name = "FactChecker", description = "Check content for factual accuracy") {
    
    flow {
        aggregate<UserInput, FactualAssertions, RationalizedFactualAssertions>(
            transforms = llms.map { llm ->
                { context -> /* extract assertions with this LLM */ }
            },
            merge = { list, context -> /* rationalize overlapping claims */ }
        )
    }
    
    transformation<RationalizedFactualAssertions, FactCheck> { 
        /* parallel fact-checking */
    }
}
```

**数据模型:**
```kotlin
data class FactualAssertion(
    val claim: String,
    val reasoning: String
)

data class AssertionCheck(
    val assertion: FactualAssertion,
    val isFactual: Boolean,
    val confidence: Double,
    val sources: List<String>
)
```

**试试看:**
```bash
"Check these facts: The Earth is flat. Paris is the capital of France."
```
**代码位置:** `examples-kotlin/src/main/kotlin/com/embabel/example/factchecker/`

### 🔒 **专家：安全受控的 Agent**

> **适用语言：** Java & Kotlin | **核心概念：** MCP 工具访问控制

演示了如何使用 `@SecureAgentTool` 实现基于 JWT 保护的 MCP 工具暴露。每个 Agent 类都会声明调用其任何操作所需的 Spring Security SpEL 表达式。

**主要内容：**

* 🔒 **类级别的访问控制**：使用 `@SecureAgentTool`
* 🔑 **JWT 承载令牌（Bearer token）**：在 HTTP 传输层进行验证
* 🛡️ **双层安全防护** — 过滤器链拒绝未认证的请求；`@SecureAgentTool` 强制执行按 Agent 的权限检查
* ⚡ **快速失败安全机制** — 被拒绝的调用会返回清晰的 MCP 错误，而不会重试或消耗大语言模型（LLM）Token

**Agent 列表：**

| Agent | 所需权限 | 功能描述 |
| --- | --- | --- |
| `NewsDigestAgent` | `news:read` | 通过网络搜索研究某个主题并返回精选摘要 |
| `MarketIntelligenceAgent` | `market:admin` | 生成 SWOT 加竞争情报报告 |

**核心模式：**

```kotlin
@Agent(description = "研究一个主题并返回新闻摘要")
@SecureAgentTool("hasAuthority('news:read')")  // 保护该 Agent 中的每一个 @Action
class NewsDigestAgent {

    @Action   // 同样需要 news:read 权限
    fun extractTopic(userInput: UserInput, context: OperationContext): NewsTopic

    @AchievesGoal(description = "生成精选新闻摘要",
                  export = Export(remote = true, name = "newsDigest",
                                  startingInputTypes = [UserInput::class]))
    @Action   // 同样需要 news:read 权限
    fun produceDigest(topic: NewsTopic, context: OperationContext): NewsDigest
}

```

**运行方式：**

```bash
cd scripts/kotlin && ./mcp_secured_server.sh

```

有关令牌生成和 MCP Inspector 的设置，请参阅[安全 MCP 服务器模式](https://www.google.com/search?q=%23secured-mcp-server-mode)。

**源码位置：**

* **Kotlin：** `examples-kotlin/src/main/kotlin/com/embabel/example/secured/`
* **Java：** `examples-java/src/main/java/com/embabel/example/secured/`

---

## 🛠️ 您将学习的核心概念
### **Spring 框架集成**
- **自动配置:** Starter（起步依赖）自动处理所有配置
- **基于 Profile 的配置:** 通过 Spring Profile 配置 MCP 客户端
- **Maven 配置:** `enable-shell`、`enable-shell-mcp-client`、`enable-agent-mcp-server`
- **依赖注入:** 采用构造函数注入，智能体作为 Spring beans
- **配置属性:** 使用 `@ConfigurationProperties` 进行类型安全配置
- **条件 Bean:** 具有 `@ConditionalOnBean` 的环境特定组件
- **仓储模式:** Spring Data 集成用于领域实体

### **现代 Spring Boot 模式**
- **自动配置:** Starter（起步依赖）自动处理所有配置
- **基于 Profile 的配置:** 通过 Spring Profile 配置 MCP 客户端
- **基于配置文件的执行:** Maven 配置控制哪个应用程序类运行
- **自动配置类:** 理解 Spring Boot 的自动配置
- **条件配置:** 特定模式的 Bean 加载
- **基于主题的自定义:** 基于特性的动态行为

### **现代 Kotlin 特性**
- **数据类:** 带计算属性的丰富领域模型
- **类型别名:** 特定领域的类型（`typealias OneThroughTen = Int`）
- **扩展函数:** 增强现有类型的功能
- **委托:** 清晰的组合模式
- **DSL 构建:** 功能性智能体构建
- **协程:** 采用结构化并发的并行执行

### **智能体设计模式**
- **工作流编排:** 使用 `@Action` 链的多步骤过程
- **黑板模式:** 动作之间共享数据的工作空间
- **人机协作:** 用户确认和表单提交
- **自我改进:** 质量的批评和重试循环
- **多模型共识:** 整合不同 LLM 的结果
- **条件流程:** 使用 `@Condition` 控制工作流
- **进度监控:** 事件发布用于监控

### 其他示例

我们的一些示例是独立的项目，因此位于单独的仓库中。

参考项目:

- [Coding Agent](https://www.github.com/embabel/coding-agent): 一款开源的编程智能体。
- [Flicker](https://www.github.com/embabel/flicker): 一款电影推荐引擎，根据用户的喜好和其在流媒体服务上可用的内容进行推荐。使用外部 API 及通过 JPA 的 PostgreSQL。展示了一个复杂的工作流，在该工作流中，推荐会不断生成，直到找到足够可用的电影。
- [Decker](https://www.github.com/embabel/decker): 一个使用 Embabel 构建演示文稿的智能体
- [Tripper](https://www.github.com/embabel/tripper): 旅行规划智能体。使用地图 API 查找路线和感兴趣的地点，并生成旅行行程。同时执行对感兴趣的地点研究。

## 🔧 运行特定示例
### **交互式 Shell 模式**（默认)
```bash
cd scripts/kotlin && ./shell.sh         # 带有 Docker 工具（默认）
cd scripts/kotlin && shell.cmd            # 带有 Docker 工具（Windows）
# 或者
cd scripts/java && ./shell.sh             # 带有 Docker 工具（默认）
cd scripts/java && shell.cmd              # 带有 Docker 工具（Windows）

```

使用 Maven Profile：`enable-shell-mcp-client`

### **不带 Docker 工具的 Shell**

```bash
cd scripts/kotlin && ./shell.sh --no-docker-tools      # 仅基础功能
cd scripts/kotlin && shell.cmd --no-docker-tools      # 仅基础功能（Windows）
# 或者
cd scripts/java && ./shell.sh --no-docker-tools       # 仅基础功能
cd scripts/java && shell.cmd --no-docker-tools        # 仅基础功能（Windows）

```

使用 Maven 配置文件：`enable-shell`

### **可观测性 (Zipkin 追踪)**

通过添加 `--observability` 标志来启用 Zipkin 分布式追踪：

```bash
cd scripts/kotlin && ./shell.sh --observability    # 启用可观测性
cd scripts/kotlin && shell.cmd --observability     # 启用可观测性 (Windows)
# 或者
cd scripts/java && ./shell.sh --observability      # 启用可观测性
cd scripts/java && shell.cmd --observability       # 启用可观测性 (Windows)
```

请确保在本项目根目录运行 `docker compose up` 来启动 Zipkin 分布式追踪收集器:

```bash
docker compose up
```
启动后您应该能访问到 Zipkin 控制台：http://127.0.0.1:9411/zipkin/

### **MCP 服务器模式**
```bash
cd scripts/kotlin && ./mcp_server.sh
cd scripts/kotlin && mcp_server.cmd      # Windows
# 或
cd scripts/java && ./mcp_server.sh
cd scripts/java && mcp_server.cmd        # Windows
```
使用 Maven 配置文件：`enable-agent-mcp-server`

### **安全受控的 MCP 服务器模式**

```bash
cd scripts/kotlin && ./mcp_secured_server.sh
cd scripts/kotlin && mcp_secured_server.cmd  # Windows

```

使用 Maven Profile：`enable-secured-agent-mcp-server`

有关令牌设置和连接说明，请参阅[安全 MCP 服务器模式](https://www.google.com/search?q=%23secured-mcp-server-mode)。

您可以将 Embabel 智能体平台作为 MCP 服务器从 UI（如 Claude Desktop）使用。Embabel MCP 服务器通过 SSE 提供。

在 `claude_desktop_config.yml` 中将 Claude Desktop 配置如下：
```json
{
  "mcpServers": {
    "embabel-examples": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:8080/sse"
      ]
    }
  }
}
```
请参考 [Claude Desktop 用户的 MCP 快速入门](https://modelcontextprotocol.io/quickstart/user) 了解如何配置 Claude Desktop。

在 Claude Desktop 中创建一个项目以使用 Embabel 示例。这样您就可以添加自定义系统提示。

Embabel 服务器将每个目标暴露为一个 MCP 工具，使 Claude Desktop 能够像这样调用它们：

<img src="images/Claude_Desktop_StarNews.jpg" alt="Claude Desktop 调用 Embabel 星座新闻发现智能体" width="600"/>

[MCP Inspector](https://github.com/modelcontextprotocol/inspector) 是一个有助于与您的 Embabel SSE 服务器交互的工具，可以手动调用工具并检查暴露的提示和资源。

使用以下命令启动 MCP Inspector：
```bash
npx @modelcontextprotocol/inspector
```

### **手动执行**
```bash
# 带 MCP 客户端的 Kotlin shell 模式（默认）
cd examples-kotlin
mvn -P enable-shell-mcp-client spring-boot:run

# 不带 MCP 客户端的 Kotlin shell 模式
cd examples-kotlin
mvn -P enable-shell spring-boot:run

# Kotlin MCP 服务器模式
cd examples-kotlin  
mvn -P enable-agent-mcp-server spring-boot:run

# Java 的对应命令使用相同模式
cd examples-java
mvn -P enable-shell-mcp-client spring-boot:run
```

### **测试**
```bash
# 运行所有测试
./mvnw test             # Unix/Linux/macOS
mvnw.cmd test           # Windows

# 模块特定测试
cd examples-kotlin && ../mvnw test
cd examples-java && ../mvnw test
```

## 🌐 **MCP（模型上下文协议）支持**
### **什么是 MCP?**
MCP（模型上下文协议）是一个开放协议，使 AI 助手和应用程序能够安全地连接到数据源和工具。Embabel 通过两种方式支持 MCP：

1. **MCP 服务器模式**: 您的智能体成为 MCP 客户端可以调用的工具
2. **MCP 客户端支持**: 您的智能体可以调用外部 MCP 服务器（如 Docker Desktop）

### **MCP 服务器模式**

以 MCP 服务器身份运行您的智能体，通过服务器推送事件 (SSE) 暴露工具：
```bash
# 启动 Kotlin 智能体作为 MCP 服务器
cd scripts/kotlin && ./mcp_server.sh

# 启动 Java 智能体作为 MCP 服务器  
cd scripts/java && ./mcp_server.sh
```

您的智能体将作为工具可用：
- **星座新闻发现** - `find_horoscope_news`
- **研究者** - `research_topic`
- **事实核查者** - `check_facts`

### **安全受控的 MCP 服务器模式**

运行一个由 JWT 保护的 MCP 服务器，通过 `@SecureAgentTool` 强制执行基于每个 Agent 的访问控制。
该模式要求每个 MCP 请求都必须包含有效的 Bearer 令牌。

```bash
# 启动受保护的 Kotlin Agent 作为 MCP 服务器
cd scripts/kotlin && ./mcp_secured_server.sh
mcp_secured_server.cmd     # Windows

```

使用 Maven Profile：`enable-secured-agent-mcp-server`

受保护的服务器暴露了两个 Agent，每个 Agent 都需要特定的 JWT 权限：

| Agent | 工具名称 | 所需权限 |
| --- | --- | --- |
| `NewsDigestAgent` | `newsDigest` | `news:read` |
| `MarketIntelligenceAgent` | `marketIntelligenceReport` | `market:admin` |

每个 Agent 中的所有 `@Action` 方法都受到保护——不仅限于实现目标的 Action。
诸如主题提取和网络研究等中间步骤同样需要权限，
因此未经授权的调用者无法在部分执行时消耗大语言模型（LLM）Token。

#### 生成 JWT 令牌

本地开发时附带了一个密钥对和令牌生成脚本：

```bash
# 生成 RSA 密钥对（一次性设置，在 keys/ 目录下）
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem

```

`keys` 目录位于：

```
examples-common/src/main/resources/keys/

```

生成令牌：

```bash
pip install pyjwt cryptography
cd examples-common/src/main/resources/keys
python generate_token.py

```

生成的令牌包含 `news:read` 和 `market:admin` 权限。
将其作为 `Bearer <token>` 授权标头粘贴到你的 MCP 客户端中。

#### 连接 MCP Inspector

启动 MCP Inspector 并使用 **SSE** 传输类型连接，URL 为 `http://localhost:8443/sse`，
以及授权标头：

```
Bearer <your_token>

```

#### 工作原理

安全防护在两个层面上强制执行：

1. **HTTP 过滤器链** — 对 `/sse/**` 和 `/mcp/**` 的所有请求都要求提供有效的 JWT（缺失或无效时返回 401）
2. **`@SecureAgentTool`** — 每个 Agent 类都声明所需的权限；被拒绝的调用会返回清晰的 MCP 错误响应，而不会重试

`secured` Spring Profile 同时激活了这两个层面。没有该 Profile 时，服务器将在无认证状态下运行（标准 MCP 服务器模式）。

### **MCP 客户端支持**

Docker 工具默认处于启用状态。如需禁用，请运行：

```bash
# 禁用 Docker MCP 集成
cd scripts/kotlin && ./shell.sh --no-docker-tools
cd scripts/java && ./shell.sh --no-docker-tools

```

启用 Docker 工具后，你的 Agent 可以：
- 在 Docker 容器中执行命令
- 访问容器化服务
- 与其他兼容 MCP 的工具集成

### **MCP 的优点**
- **🔄 工具互操作性** - 智能体可以用作工具并被用作工具
- **🎯 领域专业知识** - 适用于特定任务的专业智能体
- **🛠️ 工具组合** - 在工作流中合并多个工具
- **🔒 安全访问** - MCP 处理身份验证和沙箱
- **📈 可扩展架构** - 添加新工具而无需更改代码

## 🎯 **创建您自己的智能体应用程序**
### **基本 Shell 应用程序**
```kotlin
@SpringBootApplication
class MyAgentApplication

fun main(args: Array<String>) {
    runApplication<MyAgentApplication>(*args)
}
```

将 shell starter 添加到你的 `pom.xml` 中：
```xml
<dependency>
    <groupId>com.embabel.agent</groupId>
    <artifactId>embabel-agent-starter-shell</artifactId>
</dependency>
```

### **带主题和 MCP 客户端的 Shell**
```kotlin
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.example"])

class MyThemedAgentApplication

fun main(args: Array<String>) {
    runApplication<MyThemedAgentApplication>(*args) {
        setAdditionalProfiles("docker-ce")  // Enable MCP client via profile
        setDefaultProperties(
            mapOf("embabel.agent.logging.personality" to LoggingThemes.STAR_WARS)
        )
    }
}
```

### **MCP 服务器应用程序**
```kotlin
@SpringBootApplication
class MyMcpServerApplication

fun main(args: Array<String>) {
    runApplication<MyMcpServerApplication>(*args)
}
```

将 shell starter 添加到你的 `pom.xml` 中：
```xml
<dependency>
    <groupId>com.embabel.agent</groupId>
    <artifactId>embabel-agent-starter-mcpserver</artifactId>
</dependency>
```

---

## 🎯 入门建议
### **对智能体不熟悉?**
1. 从 **星座新闻智能体**（Java 或 Kotlin）开始
2. 比较 Java 和 Kotlin 实现
3. 尝试不同的提示，观察智能体规划不同的工作流
4. 查看 Profile 和属性是如何配置 MCP 客户端的

### **Spring 开发者?**
1. 查看配置类和仓储集成
2. 研究数据模型设计和服务架构
3. 探索不同的应用模式和 Maven 配置文件
4. 了解如何配置主题和 MCP 客户端

### **Kotlin 爱好者?**
1. 进阶到 **研究者**，了解多模型模式
2. 探索 **事实核查者** 的函数式 DSL 方法

### **AI/ML 开发者?**
1. 学习任何示例中的提示工程技术
2. 查看 **研究者** 以学习多模型共识模式
3. 查看 **事实核查者** 以学习置信评分和来源评估
4. 探索 MCP 集成以实现工具组合

## 🚨 常见问题与解决方案

| 问题                          | 解决方法                                                                                 |
|-------------------------------|------------------------------------------------------------------------------------------|
| **"未找到 API 密钥"**       | 配置 `OPENAI_API_KEY` 或 `ANTHROPIC_API_KEY`                                            |
| **错误示例加载**             | 使用正确的脚本: `kotlin/shell.sh` 与 `java/shell.sh`                                   |
| **构建失败**                 | 从项目根目录运行 `./mvnw clean install`（Unix/macOS）或 `mvnw.cmd clean install`（Windows） |
| **未找到应用程序类**         | 检查 Maven 配置文件与应用程序类是否匹配                                               |
| **MCP 客户端无法连接**       | 检查端口可用性和 Docker Desktop 状态。获取模型的说明请参考前面的配置指南。        |

在出现问题时查看日志输出，其中可能包含解决问题的提示。

## 📁 项目结构

```text
embabel-agent-examples/
├── examples-kotlin/                 # 🏆 Kotlin 实现
│   ├── src/main/kotlin/com/embabel/example/
│   │   ├── KotlinAgentShellApplication.kt           # Shell + MCP 客户端（默认）
│   │   ├── KotlinAgentSimpleShellApplication.kt     # 不含 MCP 的 Shell
│   │   ├── KotlinAgentMcpServerApplication.kt       # MCP 服务器模式
│   │   ├── KotlinAgentSecuredMcpServerApplication.kt# 🔒 安全受控的 MCP 服务器模式
│   │   ├── horoscope/              # 🌟 初学者：星座新闻 Agent
│   │   ├── secured/                # 🔒 专家：JWT 安全受控的 Agent
│   ├── pom.xml                     # 各模式的 Maven Profile
│   └── README.md                   # 📖 Kotlin 专项文档
│
├── examples-java/                    # ☕ Java 实现  
│   ├── src/main/java/com/embabel/example/
│   │   ├── JavaAgentShellApplication.java       # 带主题的 Shell
│   │   ├── JavaAgentSimpleShellApplication.java # 不含 MCP 的 Shell
│   │   ├── JavaMcpServerApplication.java        # MCP 服务器模式
│   │   └── horoscope/              # 🌟 初学者：星座新闻 Agent
│   └── README.md                   # 📖 Java 专项文档
│
├── examples-common/                 # 🔧 共享服务与工具
├── scripts/                        # 🚀 快速启动脚本
│   ├── kotlin/
│   │   ├── shell.sh               # 启动 Shell（使用 --no-docker-tools 禁用）
│   │   ├── mcp_server.sh          # 启动 MCP 服务器
│   │   └── mcp_secured_server.sh  # 启动 JWT 安全受控的 MCP 服务器
│   ├── java/
│   │   ├── shell.sh               # 启动 Shell（使用 --no-docker-tools 禁用）
│   │   └── mcp_server.sh          # 启动 MCP 服务器
│   ├── support/                    # 共享脚本工具
│   └── README.md                   # 📖 脚本说明文档
└── pom.xml                         # 父 Maven 配置
```

## 📄 开源协议

基于 Apache 许可证 2.0 授权。详情请参考 [LICENSE](LICENSE) 文件。

**🎉 使用 Spring 框架和 Agentic AI 享受编码的乐趣！**

### 🌟 愿原力与你的智能体同在！🌟

## 贡献者

[![Embabel 贡献者](https://contrib.rocks/image?repo=embabel/embabel-agent-examples)](https://github.com/embabel/embabel-agent-examples/graphs/contributors)
