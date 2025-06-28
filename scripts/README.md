# Shell Scripts

This directory contains convenient shell scripts to run Embabel Agent examples in different modes.

## 🚀 Quick Start Scripts

### **Basic Shell Mode**
Run the application in interactive shell mode with basic agent features:

```bash
# Kotlin examples
cd kotlin && ./shell.sh          # Unix/Linux/macOS
cd kotlin && shell.cmd           # Windows

# Java examples  
cd java && ./shell.sh            # Unix/Linux/macOS
cd java && shell.cmd             # Windows
```

**Features Available:**
- ✅ Interactive command-line interface
- ✅ Basic agent workflows
- ✅ Web search and content generation
- ✅ Star Wars or Severance themed logging
- ⚠️ **Limited toolset** - No Docker integration

### **Enhanced Shell Mode with Docker Tools**
Run the application with advanced Docker integration capabilities:

```bash
# Kotlin examples
cd kotlin && ./shell.sh --docker-tools     # Unix/Linux/macOS
cd kotlin && shell.cmd --docker-tools      # Windows

# Java examples
cd java && ./shell.sh --docker-tools       # Unix/Linux/macOS  
cd java && shell.cmd --docker-tools        # Windows
```

**Additional Features:**
- ✅ All basic shell features
- ✅ **Docker Desktop MCP integration**
- ✅ Container execution capabilities
- ✅ Advanced tool composition
- ✅ Expanded agent capabilities

### **MCP Server Mode**
Run agents as an MCP (Model Context Protocol) server:

```bash
# Kotlin examples
cd kotlin && ./mcp_server.sh     # Unix/Linux/macOS
cd kotlin && mcp_server.cmd      # Windows

# Java examples
cd java && ./mcp_server.sh       # Unix/Linux/macOS
cd java && mcp_server.cmd        # Windows
```

**Features:**
- 🔧 Exposes agents as MCP-compatible tools
- 🌐 JSON-RPC communication via Server-Sent Events
- 🔗 Integration with Claude Desktop and other MCP clients
- 🐳 Docker Desktop integration included

---

## 🎯 When to Use Each Mode

| Mode | Use Case | Best For |
|------|----------|----------|
| **Basic Shell** | Learning, development, testing | New users, simple workflows |
| **Shell + Docker Tools** | Advanced development, container workflows | Complex agents, Docker integration |
| **MCP Server** | Integration with external tools | Production use, Claude Desktop |

---

## 🛠️ Script Architecture

All shell scripts follow a **Template Method Pattern** using shared components:

```
scripts/
├── kotlin/
│   ├── shell.sh              # Unified shell launcher with --docker-tools support
│   └── mcp_server.sh         # MCP server launcher
├── java/
│   ├── shell.sh              # Unified shell launcher with --docker-tools support  
│   └── mcp_server.sh         # MCP server launcher
└── support/
    ├── shell_template.bat     # Shared shell logic (Windows)
    ├── agent.bat             # Application launcher (Windows)
    ├── agent.sh              # Application launcher (Unix)
    └── check_env.sh/.bat     # Environment validation
```

### **How the Template System Works:**

1. **Language-specific scripts** (`kotlin/shell.cmd`, `java/shell.cmd`) set the application path
2. **Shared template** (`support/shell_template.bat`) handles parameter parsing and warnings
3. **Common launcher** (`support/agent.bat`) validates environment and runs Maven

This follows **Domain-Driven Design** principles with clear separation of concerns.

---

## 🔧 Environment Requirements

### **API Keys** (Required)
Set at least one API key before running:

```bash
# OpenAI (recommended)
export OPENAI_API_KEY="your_openai_key"

# Or Anthropic Claude
export ANTHROPIC_API_KEY="your_anthropic_key"

# Or both for maximum compatibility
export OPENAI_API_KEY="your_openai_key"
export ANTHROPIC_API_KEY="your_anthropic_key"
```

### **Docker Integration** (Optional)
For `--docker-tools` functionality:

1. **Install Docker Desktop**
2. **Enable MCP Server** in Docker Desktop settings
3. **Pull required models:**
   ```bash
   docker login
   docker mcp gateway run
   ```

---

## 🚨 Warning System

The scripts include **color-coded warnings** to help you understand feature availability:

**Without `--docker-tools`:**
```
🔴 WARNING: Only Basic Agent features will be available
🟡 Use --docker-tools parameter to enable advanced Docker integration features
```

**With `--docker-tools`:**
```
✅ Starting application with profile: enable-shell-mcp-client
✅ Application path: ../../examples-kotlin
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| **"Environment check failed"** | Set `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` |
| **"AGENT_APPLICATION must be set"** | Use provided scripts, don't call `agent.bat` directly |
| **"POM file not found"** | Run from correct directory (`scripts/kotlin` or `scripts/java`) |
| **Red warning appears** | Add `--docker-tools` parameter for full features |
| **Docker tools fail** | Check Docker Desktop is running and MCP server is enabled |

---

## 🎓 Advanced Usage

### **Custom Maven Profiles**
The scripts use these Maven profiles under the hood:

- `enable-shell` - Basic shell mode
- `enable-shell-mcp-client` - Shell with Docker integration  
- `enable-agent-mcp-server` - MCP server mode

### **Manual Execution**
You can also run applications directly with Maven:

```bash
# From examples-kotlin/ or examples-java/
mvn -P enable-shell spring-boot:run
mvn -P enable-shell-mcp-client spring-boot:run  
mvn -P enable-agent-mcp-server spring-boot:run
```

---

## 📊 Script Comparison

| Script | Platform | Parameters | Profile Used |
|---------|----------|------------|--------------|
| `shell.sh` | Unix/Linux/macOS | `[--docker-tools]` | `enable-shell` or `enable-shell-mcp-client` |
| `shell.cmd` | Windows | `[--docker-tools]` | `enable-shell` or `enable-shell-mcp-client` |
| `mcp_server.sh` | Unix/Linux/macOS | None | `enable-agent-mcp-server` |
| `mcp_server.cmd` | Windows | None | `enable-agent-mcp-server` |

---

## 🤝 Contributing

When adding new scripts:

1. **Follow the template pattern** - Use `shell_template.bat` for shared logic
2. **Add parameter validation** - Check for required environment variables
3. **Include helpful warnings** - Guide users toward correct usage
4. **Support both platforms** - Provide both `.sh` and `.cmd` versions
5. **Test thoroughly** - Verify on different operating systems

---

## 📄 License

Licensed under the Apache License 2.0. See [LICENSE](../LICENSE) for details.