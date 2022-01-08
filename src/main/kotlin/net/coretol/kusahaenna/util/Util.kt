package net.coretol.kusahaenna.util

import com.google.common.collect.Queues
import net.coretol.kusahaenna.KusaHaenNa
import org.bukkit.Bukkit
import org.bukkit.block.Block
import java.util.*
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
fun <T> Queue<T>.splitQueue(count:Int = 1000):List<Queue<T>> {
    val queues = mutableListOf<Queue<T>>()
    run loop@{
        while (true)  {
            val thousandQueue = Queues.newConcurrentLinkedQueue<T>()
            for (i in 0..count) {
                val t = this.poll() ?: return@loop
                thousandQueue.add(t)
            }
            queues.add(thousandQueue)
        }
    }
    return queues
}