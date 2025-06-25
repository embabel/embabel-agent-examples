![Build](https://github.com/embabel/embabel-agent/actions/workflows/maven.yml/badge.svg)

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

<img align="left" src="https://github.com/embabel/embabel-agent/blob/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg?raw=true" width="180">

&nbsp;&nbsp;&nbsp;&nbsp;

&nbsp;&nbsp;&nbsp;&nbsp;

# 🤖 Embabel Agent Kotlin Examples

Learn agentic AI development with **Spring Framework** and **Kotlin/Java**. These examples demonstrate building intelligent agents that can plan, execute workflows, use tools, and interact with humans.

## 🚀 Quick Start

### Prerequisites
- **Java 21+**
- **API Key** (at least one): [OpenAI](https://platform.openai.com/api-keys) or [Anthropic](https://www.anthropic.com/api)
- **Maven 3.9+** (optional - project includes Maven wrapper)

### 1. Clone & Build
```bash
git clone https://github.com/embabel/embabel-agent-examples.git
cd embabel-agent-examples
./mvnw clean install    # Unix/Linux/macOS
mvnw.cmd clean install  # Windows
```

### 2. Set API Keys
```bash
# Required (choose one or both)
export OPENAI_API_KEY="your_openai_key"
export ANTHROPIC_API_KEY="your_anthropic_key"

```

### 3. Run Examples

#### **Kotlin Examples**
```bash
cd scripts/kotlin
./shell.sh          # Unix/Linux/macOS
shell.cmd           # Windows
```

---

## 🆕 **Spring Boot Integration Architecture**

### **Three Application Modes**
The Embabel Agent framework provides three distinct application modes through dedicated starter classes:

```kotlin
// 1. Interactive Shell Mode with Star Wars themed logging
@SpringBootApplication
@EnableAgentShell
@EnableAgents(loggingTheme = LoggingTheme.STAR_WARS)
class AgentShellApplication

// 2. Shell Mode with MCP Client Support (Docker Desktop integration)
@SpringBootApplication
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingTheme.SEVERANCE,
    mcpServers = [McpServers.DOCKER_DESKTOP]
)
class AgentShellMcpClientApplication

// 3. MCP Server Mode  
@SpringBootApplication
@EnableAgentMcpServer
@EnableAgents(mcpServers = [McpServers.DOCKER_DESKTOP])
class AgentMcpServerApplication
```

### **Annotation Guide:**

#### **`@EnableAgentShell`**
- ✅ Interactive command-line interface
- ✅ Agent discovery and registration
- ✅ Human-in-the-loop capabilities
- ✅ Progress tracking and logging
- ✅ Development-friendly error handling

#### **`@EnableAgentMcpServer`**
- ✅ MCP protocol server implementation
- ✅ Tool registration and discovery
- ✅ JSON-RPC communication via SSE (Server-Sent Events)
- ✅ Integration with MCP-compatible servers
- ✅ Security and sandboxing

#### **`@EnableAgents`**
- 🎨 **loggingTheme**: Customize your agent's logging personality
  - `LoggingThemes.STAR_WARS` - May the Force be with your logs!
  - `LoggingThemes.SEVERANCE` - Welcome to Lumon Industries (default)
- 🐳 **mcpServers**: Enable MCP server integrations
  - `McpServers.DOCKER_DESKTOP` - Docker Desktop AI capabilities
  - Custom servers can be added

---

## 📚 Examples by Learning Level

### 🌟 **Beginner: Horoscope News Agent**
> **Available in:** Java & Kotlin | **Concept:** Basic Agent Workflow

A fun introduction to agent development that finds personalized news based on someone's star sign.

**What It Teaches:**
- 📋 **Action-based workflows** with `@Action` annotations
- 🔍 **Data extraction** from user input using LLMs
- 🌐 **Web tool integration** for finding news stories
- 📝 **Content generation** with personality and context
- 🎯 **Goal achievement** with `@AchievesGoal`

**How It Works:**
1. Extract person's name from user input
2. Get their star sign (via form if needed)
3. Retrieve daily horoscope
4. Search web for relevant news stories
5. Create amusing writeup combining horoscope + news

**Try It:**

Start the agent shell, then type:

```bash
x "Find horoscope news for Alice who is a Gemini"
```

`x` is short for `execute`, which triggers the agent to run its workflow.

**Code Comparison:**
- **Kotlin:** `examples-kotlin/src/main/kotlin/com/embabel/example/horoscope/StarNewsFinder.kt`
- **Java:** `examples-java/src/main/java/com/embabel/example/horoscope/StarNewsFinder.java`

**Key Patterns:**
```kotlin
@Agent(description = "Find news based on a person's star sign")
class StarNewsFinder {
    
    @Action
    fun extractPerson(userInput: UserInput): Person?
    
    @Action(toolGroups = [CoreToolGroups.WEB])
    fun findNewsStories(person: StarPerson, horoscope: Horoscope): RelevantNewsStories
    
    @AchievesGoal(description = "Create an amusing writeup")
    @Action
    fun starNewsWriteup(/* params */): Writeup
}
```

---

### 🔬 **Expert: Multi-LLM Research Agent**
> **Available in:** Kotlin | **Concept:** Self-Improving AI Workflows

A sophisticated research agent using multiple AI models with self-critique capabilities.

**What It Teaches:**
- 🧠 **Multi-model consensus** (GPT-4 + Claude working together)
- 🔍 **Self-improvement loops** with critique and retry
- ⚙️ **Configuration-driven behavior** with Spring Boot properties
- 🌊 **Parallel processing** of research tasks
- 📝 **Quality control** through automated review

**Architecture:**
```kotlin
@ConfigurationProperties(prefix = "embabel.examples.researcher")
data class ResearcherProperties(
    val maxWordCount: Int = 300,
    val claudeModelName: String = AnthropicModels.CLAUDE_35_HAIKU,
    val openAiModelName: String = OpenAiModels.GPT_41_MINI
)
```

**Self-Improvement Pattern:**
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

**Try It:**
```bash
"Research the latest developments in renewable energy adoption"
```

**Location:** `examples-kotlin/src/main/kotlin/com/embabel/example/researcher/`

---

### ✅ **Expert: Fact-Checking Agent (DSL Style)**
> **Available in:** Kotlin | **Concept:** Functional Agent Construction

A fact-verification agent built using Embabel's functional DSL approach instead of annotations.

**What It Teaches:**
- 🔧 **Functional DSL construction** for agents
- 🔍 **Parallel fact verification** across multiple claims
- 📊 **Confidence scoring** and source trust evaluation
- 🌐 **Web research integration** for verification
- ⚡ **Functional programming patterns** in agent design

**DSL Construction:**
```kotlin
fun factCheckerAgent(llms: List<LlmOptions>, properties: FactCheckerProperties) = 
agent(name = "FactChecker", description = "Check content for factual accuracy") {
    
    flow {
        aggregate<UserInput, FactualAssertions, RationalizedFactualAssertions>(
            transforms = llms.map { llm ->
                { context -> /* extract assertions with this LLM */ }
            },
            merge = { list, context -> /* rationalize overlapping claims */ }
        ).parallelize()
    }
    
    transformation<RationalizedFactualAssertions, FactCheck> { 
        /* parallel fact-checking */
    }
}
```

**Domain Model:**
```kotlin
data class FactualAssertion(
    val claim: String,
    val reasoning: String
)

data class AssertionCheck(
    val assertion: String,
    val isTrue: Boolean,
    val confidence: ZeroToOne,
    val reasoning: String,
    val links: List<InternetResource>
)
```

**Try It:**
```bash
"Check these facts: The Earth is flat. Paris is the capital of France."
```

**Location:** `examples-kotlin/src/main/kotlin/com/embabel/example/factchecker/`

---

## 🛠️ Core Concepts You'll Learn

### **Spring Framework Integration**
- **Multiple Application Classes:** Dedicated starters for different modes
- **Maven Profiles:** `enable-shell`, `enable-shell-mcp-client`, `enable-agent-mcp-server`
- **Dependency Injection:** Constructor-based injection with agents as Spring beans
- **Configuration Properties:** Type-safe configuration with `@ConfigurationProperties`
- **Conditional Beans:** Environment-specific components with `@ConditionalOnBean`
- **Repository Pattern:** Spring Data integration for domain entities

### **Modern Spring Boot Patterns**
- **Multi-Annotation Architecture:** Combining multiple `@Enable*` annotations
- **Profile-Based Execution:** Maven profiles control which application class runs
- **Auto-Configuration Classes:** Understanding Spring Boot's auto-configuration
- **Conditional Configuration:** Mode-specific bean loading
- **Theme-Based Customization:** Dynamic behavior based on configuration

### **Modern Kotlin Features**
- **Data Classes:** Rich domain models with computed properties
- **Type Aliases:** Domain-specific types (`typealias OneThroughTen = Int`)
- **Extension Functions:** Enhanced functionality for existing types
- **Delegation:** Clean composition patterns
- **DSL Construction:** Functional agent building
- **Coroutines:** Parallel execution with structured concurrency

### **Agent Design Patterns**
- **Workflow Orchestration:** Multi-step processes with `@Action` chains
- **Blackboard Pattern:** Shared workspace for data between actions
- **Human-in-the-Loop:** User confirmations and form submissions
- **Self-Improvement:** Critique and retry loops for quality
- **Multi-Model Consensus:** Combining results from different LLMs
- **Condition-Based Flow:** Workflow control with `@Condition`
- **Progress Tracking:** Event publishing for monitoring

---

## 🔧 Running Specific Examples

### **Interactive Shell Mode** (Default)
```bash
cd scripts/kotlin && ./shell.sh
# or
cd scripts/java && ./shell.sh
```

Uses Maven profile: `enable-shell`

### **Shell with MCP Client Support**
```bash
cd scripts/kotlin && ./shell_mcp_client.sh
# or
cd scripts/java && ./shell_mcp_client.sh     # Unix/Linux/macOS
cd scripts/java && shell_mcp_client.cmd      # Windows
```

Uses Maven profile: `enable-shell-mcp-client`

### **MCP Server Mode**
```bash
cd scripts/kotlin && ./mcp_server.sh
# or
cd scripts/java && ./mcp_server.cmd
```

## 🌐 **MCP (Model Context Protocol) Support**

### **What is MCP?**
MCP (Model Context Protocol) is an open protocol that enables AI assistants and applications to securely connect to data sources and tools. Embabel supports MCP in two ways:

1. **MCP Server Mode**: Your agents become tools that can be called by MCP clients
2. **MCP Server Support**: Your agents can connect to external MCP servers (like Docker Desktop)

### **MCP Server Mode**

Run your agents as an MCP server that exposes tools over Server-Sent Events (SSE):

```bash
# Start Kotlin agents as MCP server
cd scripts/kotlin && ./mcp_server.sh
```

Your agents become available as tools:
- **StarNewsFinder** - `find_horoscope_news`
- **MovieFinder** - `suggest_movies` 
- **Researcher** - `research_topic`
- **FactChecker** - `check_facts`

### **MCP Server Support**

Enable your agents to connect to external MCP servers:

```kotlin
@EnableAgents(mcpServers = [McpServers.DOCKER_DESKTOP])
```

This allows your agents to:
- Execute commands in Docker containers
- Access containerized services
- Integrate with other MCP-compatible servers

### **Benefits of MCP**
- **🔄 Tool Interoperability** - Agents can use and be used as tools
- **🎯 Domain Expertise** - Specialized agents for specific tasks
- **🛠️ Tool Composition** - Combine multiple tools in workflows
- **🔒 Secure Access** - MCP handles authentication and sandboxing
- **📈 Scalable Architecture** - Add new tools without changing code

---

## 🎯 **Creating Your Own Agent Application**

### **Basic Shell Application**
```kotlin
@SpringBootApplication
@EnableAgentShell
@EnableAgents
class MyAgentApplication

fun main(args: Array<String>) {
    runApplication<MyAgentApplication>(*args)
}
```

### **Shell with Theme and MCP Server**
```kotlin
@SpringBootApplication
@EnableAgentShell
@EnableAgents(
    loggingTheme = LoggingThemes.STAR_WARS,
    mcpServers = [McpServers.DOCKER_DESKTOP]
)
class MyThemedAgentApplication

fun main(args: Array<String>) {
    runApplication<MyThemedAgentApplication>(*args)
}
```

### **MCP Server Application**  
```kotlin
@SpringBootApplication
@EnableAgentMcpServer
@EnableAgents
class MyMcpServerApplication

fun main(args: Array<String>) {
    runApplication<MyMcpServerApplication>(*args)
}
```

---

## 🎯 Getting Started Recommendations

### **New to Agents?**
1. Start with **Horoscope News Agent** (Java or Kotlin)
2. Compare the Java vs Kotlin implementations
3. Experiment with different prompts and see how the agent plans different workflows
4. Try different logging themes to make development more fun!

### **Spring Developer?**
1. Examine the **Star News Finder** for Spring integration patterns
2. Look at the configuration classes in **Researcher** and **Fact Checker**
3. Study how agents work as Spring beans
4. Explore the different application modes and Maven profiles
5. See how themes and MCP servers are configured

### **Kotlin Enthusiast?**
1. Start with **Star News Finder** for basic Kotlin patterns
2. Progress to **Researcher** for multi-model patterns
3. Explore **Fact Checker** for functional DSL approaches

### **AI/ML Developer?**
1. Study prompt engineering techniques in any example
2. Examine the **Researcher** for multi-model consensus patterns
3. Look at **Fact Checker** for confidence scoring and source evaluation
4. Explore MCP integration for tool composition

---

## 🚨 Common Issues & Solutions

| Problem | Solution |
|---------|----------|
| **"No API keys found"** | Set `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` |
| **Wrong examples load** | Use correct script: `kotlin/shell.sh` vs `java/shell.sh` |
| **Build failures** | Run `./mvnw clean install` (Unix/macOS) or `mvnw.cmd clean install` (Windows) from project root |
| **Tests fail** | Check API keys are set in test environment |
| **Application class not found** | Check Maven profile matches application class |
| **MCP server fails to start** | Check port availability and Docker Desktop status |

---

## 📁 Project Structure

```
embabel-agent-examples/
├── examples-kotlin/                 # 🏆 Kotlin implementations
│   ├── src/main/kotlin/com/embabel/example/
│   │   ├── AgentShellApplication.kt         # Basic shell mode
│   │   ├── AgentShellMcpClientApplication.kt # Shell + MCP client
│   │   ├── AgentMcpServerApplication.kt     # MCP server mode  
│   │   ├── horoscope/              # 🌟 Beginner: Star news agent
│   │   ├── researcher/             # 🔬 Expert: Multi-LLM researcher
│   │   └── factchecker/            # ✅ Expert: Fact checker (DSL)
│   ├── pom.xml                     # Maven profiles for each mode
│   └── README.md                   # 📖 Kotlin-specific documentation
├── examples-common/                 # 🔧 Shared services & utilities
├── scripts/                        # 🚀 Quick-start scripts
│   ├── kotlin/
│   │   ├── shell.sh               # Launch basic shell
│   │   ├── shell_mcp_client.sh    # Launch shell with MCP client
│   │   └── mcp_server.sh          # Launch MCP server
│   ├── support/                   # Shared script utilities
│   └── README.md                  # 📖 Scripts documentation
└── pom.xml                         # Parent Maven configuration
```

---

## 📄 License

Licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

**🎉 Happy coding with Spring Framework and agentic AI!**

### 🌟 May the Force be with your agents! 🌟