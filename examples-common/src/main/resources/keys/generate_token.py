import jwt
import datetime
from cryptography.hazmat.primitives.serialization import load_pem_private_key

private_key = load_pem_private_key(open("private.pem", "rb").read(), password=None)

token = jwt.encode(
    {
        "sub": "test-user",
        "authorities": ["news:read", "market:admin"],
        "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=24)
    },
    private_key,
    algorithm="RS256"
)

print(token)