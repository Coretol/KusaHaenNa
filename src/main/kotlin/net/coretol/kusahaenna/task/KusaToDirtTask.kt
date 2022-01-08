package net.coretol.kusahaenna.task

import net.coretol.kusahaenna.util.async
import net.coretol.kusahaenna.util.splitQueue
import net.coretol.kusahaenna.util.sync
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class KusaToDirtTask(override val taskID: Int, queue: Queue<Block>):TaskRunnable<Unit>,BukkitRunnable() {
    override var isComplete: Boolean = false
    var queueIndex = 0
    override var progress: Int = 0
    override val maxProgress: Int = queue.count()
    override var whenComplete: (Unit) -> Unit = fun(_) {}
    val queues:Queue<Queue<Block>>

    init {
        queues=queue.splitQueue()
    }

    override fun start() {
        async { run() }
    }

    override fun run() {
        val queue = queues.poll()?:return
        while(true) {
            val block = queue.poll() ?: break
            sync { block.type = Material.DIRT }
            progress++
        }

        if(maxProgress<=progress) {
            whenComplete.invoke(Unit)
            isComplete=true
            return
        }

        queueIndex++

        run()
    }

}