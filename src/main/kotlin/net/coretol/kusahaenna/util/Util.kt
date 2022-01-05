package net.coretol.kusahaenna.util

import net.coretol.kusahaenna.KusaHaenNa
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

fun async(function:() -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(KusaHaenNa.instance,function)
}
fun <T> runLater(delay:Long,function: () -> T):CompletableFuture<T> {
    val future = CompletableFuture<T>()
    Bukkit.getScheduler().runTaskLater(KusaHaenNa.instance, Runnable {
        future.complete(function.invoke())
    },delay)
    return future
}