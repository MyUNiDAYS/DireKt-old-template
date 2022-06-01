<h1 align="left">DireKT Kotlin SDK <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/myunidays/direkt?style=flat-square"> <a href="https://git.live"><img src="https://img.shields.io/badge/collaborate-on%20gitlive-blueviolet?style=flat-square"></a></h1>

The DireKT Kotlin SDK is a completely decoupled Routing library that is not platform specific.

## Installation

### KMM

```
implementation("com.myunidays:direkt:0.0.4")
```

## How to use

We recommend using the coordinator pattern but it's not neccessary. 

Create a subclass of the RoutingConfig, expose the screens you want to route and what constructor params they need. 
```kotlin
sealed class RootConfig(key: String): RoutingConfig(key) {
    object Dashboard: RootConfig("Dashboard")
    object Standard: RootConfig("Standard")
}
```

Create a function to create those screens.
```kotlin
fun createChild(config: RootConfig): ScreenInterface = when (config) {
        RootConfig.Dashboard -> DashboardViewModel()
        RootConfig.Standard -> StandardViewModel()
}
```

Then create an instance of the RouterImpl or implement the interface Router.
```kotlin
val router = RouterImpl<RootConfig, ScreenInterface>(
    RootConfig.Dashboard,
    ::createChild
)
```

To listen to route changes
```kotlin
router.stack.collect { (transition, config) ->
    if (transition == Transition.Push) {
        println("Pushed route $config")   
    }
```

To request a route change, where RootConfig.Standard is an entry in the config defined before.
```kotlin
router.push(RootConfig.Standard)
```

## Examples

In the Examples folder, there is an example using KMM with coordinators targeting iOS and Android.

## Known Issues

Currently, yet to implement support for Deeplinking.

## Contributing

This project is set up as an open source project. As such, if there are any suggestions that you have for features, for improving the code itself, or you have come across any problems; you can raise them and/or suggest changes in implementation.

If you are interested in contributing to this codebase, please follow the contributing guidelines. This contains guides on both contributing directly and raising feature requests or bug reports. Please adhere to our code of conduct when doing any of the above.
