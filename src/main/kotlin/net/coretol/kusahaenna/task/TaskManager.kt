package net.coretol.kusahaenna.task

import net.coretol.kusahaenna.KusaHaenNa
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

/**
 * タスクのマネージャー
 */
object TaskManager {
    /**
     * 直近で最後のタスクID
     */
    var lastID = 0

    /**
     * タスクのマップ
     */
    val tasks = mutableMapOf<Player, MutableList<TaskRunnable<Any>>>()

    init {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            KusaHaenNa.instance,
            Runnable {
                Bukkit.getOnlinePlayers().forEach {
                    renderActionBar(it)
                }
            }, 0, 1
        )
    }

    /**
     * [KusaCheckTask]を開始する
     *
     * @param player プレイヤー
     * @param center 中心
     * @param radius 半径
     * @return [KusaCheckTask]のインスタンス
     */
    fun startCheckTask(player: Player, center: Location, radius: Int): KusaCheckTask {
        return KusaCheckTask(++lastID, center, radius).also {
            tasks.computeIfAbsent(player) { mutableListOf() }.add(it)
            it.start()
        }
    }

    /**
     * [KusaToDirtTask]を開始する
     *
     * @param player プレイヤー
     * @param queue 草のキュー
     * @return [KusaToDirtTask]のインスタンス
     */
    fun startToDirtTask(player: Player, queue: Queue<Block>): KusaToDirtTask {
        return KusaToDirtTask(++lastID, queue).also {
            tasks.computeIfAbsent(player) { mutableListOf() }.add(it)
            it.start()
        }
    }

    /**
     * アクションバーを描画
     *
     * @param player プレイヤー
     */
    fun renderActionBar(player: Player) {
        val tasks = tasks[player] ?: return
        var str = "[${ChatColor.GREEN}草${ChatColor.WHITE}] "
        ArrayList(tasks).forEach { task ->
            if (task.isComplete) {
                return@forEach
            }
            val progress = task.progress.toDouble() / task.maxProgress.toDouble()
            val finishedCount = ((progress) * 20).toInt()
            val notFinishedCount = 20 - finishedCount
            str += "  "
            str += "Task${task.taskID} "
            str += "%.2f%%".format(progress * 100)
            str += "[${ChatColor.GREEN}"
            (0..finishedCount).forEach {
                str += "|"
            }
            str += "${ChatColor.GRAY}"
            (0..notFinishedCount).forEach {
                str += "|"
            }
            str += "${ChatColor.WHITE}]"
        }
        player.sendActionBar(Component.text(str))
    }
}