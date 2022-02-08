package net.coretol.kusahaenna.task

import net.coretol.kusahaenna.util.async
import net.coretol.kusahaenna.util.splitQueue
import net.coretol.kusahaenna.util.sync
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * 草をすべて土に変えるタスク
 *
 * @property taskID タスクID
 * @constructor
 *
 * @param queue 草ブロックのキュー
 */
class KusaToDirtTask(override val taskID: Int, queue: Queue<Block>) : TaskRunnable<Unit>, BukkitRunnable() {
    override var isComplete: Boolean = false
    override var progress: Int = 0
    override val maxProgress: Int = queue.count()
    override var whenComplete: (Unit) -> Unit = fun(_) {}

    /**
     * 1000個ずつ分割されたキュー
     */
    val queues: Queue<Queue<Block>>

    init {
        //キューを1000個ずつ分割
        queues = queue.splitQueue()
    }

    override fun start() {
        async { run() }
    }

    override fun run() {
        // 1000個に分割されたキューを一つ取り出す
        val queue = queues.poll() ?: return
        //すべて消すまでループ
        while (true) {
            val block = queue.poll() ?: break
            sync { block.type = Material.DIRT }
            progress++
        }

        //全て消し終わったら処理を終了
        if (maxProgress <= progress) {
            whenComplete.invoke(Unit)
            isComplete = true
            return
        }

        run()
    }

}