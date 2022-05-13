package com.myunidays.router

abstract class RoutingConfig(val key: String, val params: Map<String, String> = emptyMap()) // We use this key to help us deeplink.
