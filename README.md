# FoodHub Demo App

A demonstration Android application showcasing the integration of **GatekeeperX DeviceX SDK** for device fingerprinting and event tracking.

## About DeviceX

DeviceX is GatekeeperX's advanced SDK for secure device identification and user behavior analytics. This demo app demonstrates real-world implementation across multiple user flows including login, product browsing, cart management, and checkout.

## Features

- 🔐 **Login tracking** - Monitor authentication events
- 🛍️ **Product views** - Track user browsing behavior
- 🛒 **Add to cart events** - Monitor shopping cart interactions
- 💳 **Checkout tracking** - Capture transaction events
- 📦 **Order delivery** - Track fulfillment lifecycle

## Getting Started

### Prerequisites

To use this demo app, you need to obtain credentials from GatekeeperX:

1. **API Key** - Your unique application identifier
2. **Tenant ID** - Your organization's tenant identifier

Contact [GatekeeperX](https://gatekeeperx.com) to request your credentials.

### Integration

The DeviceX SDK is integrated using Gradle:

```gradle
dependencies {
    implementation("com.gatekeeperx:devicex:1.2.9")
}
```

### Configuration

1. Add your credentials to the app configuration
2. Initialize the SDK in your `Application` class:

```kotlin
Devicex.initialize(
    context = this,
    apiKey = "YOUR_API_KEY",
    tenantId = "YOUR_TENANT_ID"
)
```

3. Track events throughout your app:

```kotlin
Devicex.sendEventAsync(
    name = "event_name",
    properties = mapOf("key" to "value")
) { result ->
    when (result) {
        is EventResult.Success -> { /* Handle success */ }
        is EventResult.Failure -> { /* Handle failure */ }
    }
}
```

## Demo Credentials

Use these credentials to test the app:

- **Email**: `demo@foodhub.com`
- **Password**: `demo123`

## Requirements

- Android SDK 24+
- Kotlin 1.9.25
- Jetpack Compose

## License

This is a demonstration application by GatekeeperX for DeviceX SDK integration examples.

---

**GatekeeperX** | [Website](https://gatekeeperx.com) | [Documentation](https://docs.gatekeeperx.com)
