# Pydantic Bank Support Example

See https://ai.pydantic.dev/examples/bank-support/#running-the-example

Our version demonstrates:

- Integration with Spring Data for production-quality data access
- A true domain model with LLM tools on domain objects.

Run within Spring Shell with:

```bash
bank-support --id 123 --query "What's my balance?"

bank-support --id 123 --query "What's my balance, including pending amounts. Also, I have lost my card. Can you help?"
```

