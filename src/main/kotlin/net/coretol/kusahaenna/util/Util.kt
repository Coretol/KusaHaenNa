package net.coretol.kusahaenna.util

import com.google.common.collect.Queues
import net.coretol.kusahaenna.KusaHaenNa
import org.bukkit.Bukkit
import java.util.*

fun async(function: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(KusaHaenNa.instance, function)
}

fun sync(function: () -> Unit) {
    Bukkit.getScheduler().runTask(KusaHaenNa.instance, function)
}

fun <T> Queue<T>.splitQueue(count: Int = 1000): Queue<Queue<T>> {
    val queues = Queues.newConcurrentLinkedQueue<Queue<T>>()
    run loop@{
        while (true) {
            val thousandQueue = Queues.newConcurrentLinkedQueue<T>()
            for (i in 0..count) {
                val t = this.poll() ?: run {
                    queues.add(thousandQueue)
                    return@loop
                }
                thousandQueue.add(t)
            }
            queues.add(thousandQueue)
        }
    }
    return queues
}