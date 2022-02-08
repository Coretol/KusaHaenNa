package net.coretol.kusahaenna.task

import com.google.common.collect.Queues
import net.coretol.kusahaenna.util.async
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.*
import kotlin.math.pow

/**
 * 地中に埋まっている草を探すタスク
 *
 * @property taskID タスクID
 * @property radius 探す半径
 * @constructor
 *
 * @param center 中心の座標
 */
class KusaCheckTask(override val taskID: Int, center: Location, val radius: Int) : TaskRunnable<Queue<Block>> {
    override var progress = 0
    override val maxProgress = (radius * 2.0 + 1).pow(2).toInt()
    val centerChunkX = center.chunk.x
    val centerChunkZ = center.chunk.z
    val world = center.world

    /**
     * 見つかった草のキュー
     */
    val queue = Queues.newConcurrentLinkedQueue<Block>()

    /**
     * 見つけたブロックの数
     */
    var foundBlocks = 0
    override var isComplete: Boolean = false
    override var whenComplete: (Queue<Block>) -> Unit = fun(_) {}
    fun run() {
        //チャンクを取得
        val chunkX = centerChunkX + progress % (radius * 2 + 1) - radius
        val chunkZ = centerChunkZ + progress / (radius * 2 + 1) - radius
        val chunk = world.getChunkAtAsync(chunkX, chunkZ).get()
        //すべてのブロックに対してチェック
        (0..15).forEach { x ->
            (world.minHeight until world.maxHeight - 1).forEach { y ->//岩盤から空まで
                (0..15).forEach { z ->
                    val block = chunk.getBlock(x, y, z)
                    if (block.type == Material.GRASS_BLOCK) {//草ブロックのとき
                        if (chunk.getBlock(x, y + 1, z).isSolid) {//ひとつ上のブロックが固体のとき
                            queue.add(block)
                            foundBlocks++
                        }
                    }
                }
            }
        }
        if (progress >= maxProgress) {
            whenComplete.invoke(queue)
            isComplete = true
            return
        }

        run()
    }

    override fun start() {
        async { run() }
    }
}