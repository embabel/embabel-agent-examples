# Java Examples

This module contains Java implementations of Embabel Agent examples, demonstrating enterprise Java patterns and Spring Boot best practices.

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- API Keys: `OPENAI_API_KEY` or `ANTHROPIC_API_KEY`

### Running Examples

```bash
# Interactive Shell Mode (recommended for development)
cd ../scripts/java
./shell.sh        # Unix/Linux/macOS
shell.cmd         # Windows

# Or run directly with Maven
mvn spring-boot:run -Dspring-boot.run.main-class=com.embabel.example.AgentShellApplication
```

## 📂 Project Structure

```
examples-java/
├── src/main/java/com/embabel/example/
│   ├── AgentShellApplication.java    # Interactive shell with Star Wars logging
│   ├── AgentMcpApplication.java      # MCP server for Claude Desktop integration
│   └── horoscope/                    # 🌟 Horoscope news finder example
│       ├── StarNewsFinder.java       # Main agent implementation
│       ├── model/                    # Domain models
│       └── service/                  # Supporting services
└── src/main/resources/
    └── application.yml               # Configuration
```

## 🎯 Available Example

### **Horoscope News Finder** (Beginner-Friendly)
Find personalized news based on someone's zodiac sign, combining horoscope readings with current events.

**Key Concepts:**
- `@Agent` and `@Action` annotations
- Spring dependency injection
- LLM integration for data extraction
- Web tool usage for news searches
- Goal-oriented workflows

**Implementation Highlights:**
```java
@Agent(description = "Finds personalized news based on zodiac signs")
@Component
public class StarNewsFinder {
    
    @Action
    public Person extractPerson(UserInput userInput) {
        // LLM extracts person's name from natural language
    }
    
    @Action(toolGroups = {CoreToolGroups.WEB})
    public RelevantNewsStories findNewsStories(
            StarPerson person, 
            Horoscope horoscope) {
        // Search web for news matching horoscope themes
    }
    
    @AchievesGoal
    @Action
    public Writeup createWriteup(/* params */) {
        // Generate amusing narrative combining horoscope + news
    }
}
```

**Try it:**
```
shell:> execute "Find horoscope news for Bob who is a Scorpio"
shell:> execute "What's in the stars for Alice today? She's a Gemini"
```

## 🛠️ Configuration

### API Keys
```bash
export OPENAI_API_KEY="your-openai-key"
# or
export ANTHROPIC_API_KEY="your-anthropic-key"
```

### Application Configuration
```yaml
# application.yml
embabel:
  horoscope:
    news-count: 5              # Number of news stories to find
    include-predictions: true   # Include horoscope predictions
    
spring:
  main:
    banner-mode: console       # Show Spring banner
```

## 🏃 Running Modes

### Interactive Shell (Development)

```java
import com.embabel.agent.config.annotation.LoggingThemes;

@SpringBootApplication
@EnableAgentShell
@EnableAgents(loggingTheme = LoggingThemes.STAR_WARS)
public class AgentShellApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentShellApplication.class, args);
    }
}
```

Features:
- Command-line interface for testing
- Star Wars themed logging messages
- Real-time execution feedback
- Debug options with `-p` and `-r` flags

### MCP Server (Integration)
```java
@SpringBootApplication
@EnableAgentMcpServer
@EnableAgents(mcpClients = "docker-desktop")
public class AgentMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentMcpApplication.class, args);
    }
}
```

Features:
- JSON-RPC server implementation
- Claude Desktop compatibility
- Docker integration support
- Concurrent request handling

## 💡 Java Implementation Patterns

### Spring Dependency Injection
```java
@Component
public class StarNewsFinder {
    private final HoroscopeService horoscopeService;
    
    @Autowired
    public StarNewsFinder(HoroscopeService horoscopeService) {
        this.horoscopeService = horoscopeService;
    }
}
```

### Domain Models with Validation
```java
public class StarPerson extends Person {
    @NotNull
    private final StarSign starSign;
    
    public StarPerson(String name, StarSign starSign) {
        super(name);
        this.starSign = Objects.requireNonNull(starSign);
    }
}
```

### Builder Pattern for Complex Objects
```java
public class Writeup {
    private final String title;
    private final String content;
    
    public static WriteupBuilder builder() {
        return new WriteupBuilder();
    }
    
    // Builder implementation...
}
```

### Service Layer Abstraction
```java
@Service
public class HoroscopeService {
    @Cacheable("horoscopes")
    public Horoscope getDailyHoroscope(StarSign sign) {
        // Implementation with caching
    }
}
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=StarNewsFinderTest

```

### Test Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
class StarNewsFinderTest {
    
    @MockBean
    private HoroscopeService horoscopeService;
    
    @Test
    void testExtractPerson() {
        // Test person extraction from user input
    }
}
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| `No API key found` | Set `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` environment variable |
| `ClassNotFoundException` | Run `mvn clean install` from project root |
| `Port already in use` | Change port with `--server.port=8081` |
| `Bean creation error` | Check Spring profile configuration |

## 📊 Comparison with Kotlin Examples

| Feature | Java | Kotlin |
|---------|------|--------|
| **Code Style** | Verbose, explicit | Concise, expressive |
| **Null Safety** | Manual checks | Built-in null safety |
| **Async Support** | CompletableFuture | Coroutines |
| **Data Classes** | Lombok/Records | Native data classes |
| **DSL Support** | Builder pattern | Native DSL support |

## 🎓 Learning Resources

1. **Start Here** - Run the horoscope example to understand basics
2. **Explore Code** - Study the implementation patterns
3. **Modify & Extend** - Try adding new features
4. **Compare with Kotlin** - See how the same concepts differ

## 🤝 Contributing

When adding new Java examples:
1. Follow Java naming conventions
2. Use meaningful variable and method names
3. Add comprehensive JavaDoc
4. Include unit and integration tests
5. Ensure thread safety where applicable
6. Use Optional for nullable returns

## 📄 License

Licensed under Apache License 2.0. See [LICENSE](../../LICENSE) for details.
