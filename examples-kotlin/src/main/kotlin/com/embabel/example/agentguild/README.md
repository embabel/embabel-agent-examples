# Optional Agent Guild evidence tools

This example registers two native Embabel `TypedTool` instances in an opt-in
`ToolGroup`. A caller can observe a newly selected public endpoint or verify a
supplied public Agent Guild passport before making its own counterparty decision.
Neither result approves delegation, attaches an MCP server, pays, registers an
identity, or proves that a DID controls an endpoint.

`AgentGuildConfiguration` is enabled only by the `agent-guild` Spring profile.
The group has role `agent-guild` and declares `INTERNET_ACCESS`. An existing action
can explicitly select it with `.withToolGroup("agent-guild")` on its prompt runner.
That selection permits the model to request either tool; it is not an automatic
check of other tools. Keep any later endpoint connection or task execution in a
separate host-controlled step. Activating this profile does not change the
initialization order or permissions of other configured MCP clients.

For a deterministic call without a model, use the native typed entry point:

```kotlin
val tools = AgentGuildTools()
val evidence = tools.observe.typedCall(EndpointInput(publicEndpointUrl))
// Inspect evidence.status, checks and unknowns before applying your own policy.
```

`publicEndpointUrl` must come from the caller's task and be public. The tools do
not read conversation history, environment secrets, files, or other tool arguments.
The caller must authorize disclosure of that public input; no boolean parameter
or claim inside a credential supplies that authorization. Any public query string
in a selected URL is disclosed too. Do not include tokens or private claims.

## Endpoint observations

`agent_guild_observe_endpoint` accepts only `{ "url": "..." }`. It sends the exact
selected HTTP(S) URL to
`GET https://agent-guild-5d5r.onrender.com/preflight?url=...` using HTTPS to Guild.
Guild then contacts the target to make bounded observations. No paid target task
is executed by this tool.

The result keeps the exact target, local receipt time, six measured check statuses,
failed checks, unknowns, and the service's consistent verdict. It discards arbitrary
remote descriptions and instructions. `agent_card_signed` reports presence of a
signature, not its verification. `independent_evidence` is the service's reported
check; this example does not independently audit the history or bind a card DID
to the endpoint. Unknown checks remain unknown and do not change a
`no_failed_checks` verdict into an endorsement. State may change after observation.

## Supplied public passports

`agent_guild_verify_passport` requires four arguments:

- `credentialJson`: the complete public credential JSON object as text, including
  every signed claim and proof. This string container preserves signed numbers,
  escapes and whitespace without parsing and reserializing them for transport.
- `expectedIssuerDid` and `expectedSubjectDid`: `did:key` values selected
  independently of the supplied credential.
- `maxAgeSeconds`: the caller's accepted maximum snapshot age, from 1 to 86400.

The example accepts the current Guild `AgentGuildPassport` format with a
`DataIntegrityProof` using `eddsa-jcs-2022`. Other proof formats, including the
historical AGI-1 legacy suite, return `invalid_input`; they are not relabeled or
converted. It checks issuer, subject, proof binding, `validFrom`, proof creation,
maximum age, and `validUntil` when present, before and after the HTTP request.
Absent expiry remains absent in the result; the explicit maximum age still applies.

It sends the original validated UTF-8 JSON text as the whole POST body to
`https://agent-guild-5d5r.onrender.com/credentials/verify`. Separately expected DIDs
and the age limit are checked locally and are not added to the signed document.
**Guild logs a `passport_verified` event, including unsuccessful verification.**
The whole public credential is disclosed; do not add private claims or context.
This example neither retrieves nor issues a new passport when one is missing.

`verified: true` requires actual boolean `valid` and `guild_issued` response fields,
matching response identities, and current local time checks. This relies on the
HTTPS verifier's answer; the example does not implement independent offline
cryptography. A valid signature establishes origin and integrity, not independent
truth, endpoint ownership, safety, payment protection, or work quality. A false
signature result is a completed negative check, not an unavailable service.

## Bounds and negative results

Both native `call(String)` and `typedCall(input)` enforce their respective input
checks. Native JSON arguments reject extra/duplicate keys, wrong types and trailing
JSON. The complete argument cap is 128 KiB of UTF-8; credentials and responses are
64 KiB; URLs are 2048 UTF-8 bytes. JSON nesting is at most 32, numeric literals at
most 1000 characters. Raw unpaired UTF-16 surrogates are rejected rather than
silently changing transmitted UTF-8. Oversized values are rejected, never truncated.

The URL check screens syntax, embedded credentials, fragments, common local names
and reserved literal addresses. It performs no DNS lookup for domain names and
cannot prove they resolve publicly. Guild applies its own target DNS/address
policy. A hostname is not an authorization or safety claim.

The fixed-origin Java HTTP client has no supplied authentication, cookies or proxy,
does not follow redirects, requests identity encoding and rejects encoded responses.
It caps response bytes while streaming. Connection timeout is 3 seconds, request
timeout 10 seconds, and the wait for the asynchronous HTTP response is at most
12 seconds. Cancellation is best effort: DNS/transport cleanup and work already
accepted remotely may continue. These bounds cover one HTTP wait, not a deadline
for every local parse or a later framework action. The example makes no application
retries; the JDK transport can retry some connection failures internally.

`status` is `completed`, `invalid_input`, or `unavailable`. Malformed, inconsistent,
wrong-target, wrong-identity, oversized or unavailable responses cannot yield a
positive result. Negative outputs use fixed local codes and omit remote exception
messages. They do not cancel a framework queue or enforce a host's later decision.

## Local tests

From the repository root, with Java 21 and the existing Maven dependencies:

```sh
mvn -pl examples-kotlin -am -Dtest=AgentGuildToolsTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Tests exercise real `TypedTool` serialization/schema, direct typed calls, native
registry resolution, Spring profile gating and streaming byte limits. Only HTTP
responses are substituted. Synthetic passport fixtures test the request contract;
they do not claim cryptographic validity. No live model, Guild endpoint, target,
account, payment or MCP server is needed. The repository's CI command remains
`mvn -U -B test verify`.

Original example contribution by AgentTanuki, under the repository's Apache-2.0
license and required source header. Service contract references:
[preflight](https://github.com/AgentTanuki/agent-guild/blob/9cf6c561468e60afb77acdbddebfc5134a155e4f/live/guild/app/preflight.py),
[credential format](https://github.com/AgentTanuki/agent-guild/blob/9cf6c561468e60afb77acdbddebfc5134a155e4f/live/guild/app/vc.py),
and [verification response](https://github.com/AgentTanuki/agent-guild/blob/9cf6c561468e60afb77acdbddebfc5134a155e4f/live/guild/app/store.py#L8281).
