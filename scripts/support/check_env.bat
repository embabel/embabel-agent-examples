@echo off
:: Check environment variables
echo Checking environment variables...
set OPENAI_KEY_MISSING=false
set ANTHROPIC_KEY_MISSING=false

if "%OPENAI_API_KEY%"=="" (
    echo OPENAI_API_KEY environment variable is not set
    echo OpenAI models will not be available
    set OPENAI_KEY_MISSING=true
) else (
    echo OPENAI_API_KEY set: OpenAI models are available
)

if "%ANTHROPIC_API_KEY%"=="" (
    echo ANTHROPIC_API_KEY environment variable is not set
    echo Claude models will not be available
    set ANTHROPIC_KEY_MISSING=true
) else (
    echo ANTHROPIC_API_KEY set: Claude models are available
)

if "%OPENAI_KEY_MISSING%"=="true" if "%ANTHROPIC_KEY_MISSING%"=="true" (
    echo Ollama models will be used if available
    echo Please check Ollama installation and set default model in application.properties
    echo For example: embabel.models.default-llm=llama3.1:8b
)