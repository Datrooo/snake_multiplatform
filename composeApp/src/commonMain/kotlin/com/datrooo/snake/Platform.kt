package com.datrooo.snake

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform