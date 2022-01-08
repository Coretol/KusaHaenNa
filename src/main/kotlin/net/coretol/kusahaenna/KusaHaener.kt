package net.coretol.kusahaenna

import com.google.common.collect.Queues
import net.coretol.kusahaenna.util.runLater
import net.coretol.kusahaenna.util.splitQueue
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.pow

/**
 * 地中にある草を消すやつ
 *
 * @property center 中心の座標
 * @property radius 半径（チャンク）
 * @property sender ログを出す用のCommandSender
 * @constructor Create empty Kusa haener
 */
class KusaHaener(val center: Location, val radius: Int, val sender: CommandSender? = null) {
    /**
     * 地中に埋まっている草を調べてQueueとして返す
     *
     * @return queue
     */
    fun check(): CompletableFuture<Queue<Block>> = CompletableFuture.supplyAsync {
        /**
         * 草ブロックのQueue
         */
        val queue = Queues.newConcurrentLinkedQueue<Block>()

        /**
         * 中心のチャンク
         */
        val centerChunk = center.chunk
        val world = centerChunk.world

        /**
         * 読み込み終わったチャンクの数
         */
        var chunkCount = 0

        /**
         * 読み込むべきチャンクの数
         */
        val maxChunkCount = (radius * 2.0 + 1).pow(2).toInt()
        //範囲内のチャンクに対して地中に埋まっている草ブロックを探す
        (centerChunk.x - radius..centerChunk.x + radius).forEach { chunkX ->
            (centerChunk.z - radius..centerChunk.z + radius).forEach { chunkZ ->
                val chunk = world.getChunkAtAsync(chunkX, chunkZ).get()

                /**
                 * 地中に埋まっている草ブロックの数
                 */
                var count = 0
                //チャンク内の全ブロックをチェック
                (0..15).forEach { x ->
                    (0..15).forEach { z ->
                        (world.minHeight until world.maxHeight).forEach { y ->
                            val block = chunk.getBlock(x, y, z)
                            if (block.type == Material.GRASS_BLOCK) {//草ブロックのとき
                                if (chunk.getBlock(x, (y + 1).coerceAtMost(world.maxHeight - 1), z).isSolid) {
                                    queue.add(block)
                                    count++
                                }
                            }
                        }
                    }
                }
                chunkCount++
                if (count > 0) {
                    sender?.sendMessage("進行状況:${chunkCount}/${maxChunkCount} (${chunkX},${chunkZ})で${count}個見つけました")
                }
            }
        }
        return@supplyAsync queue
    }

    /**
     * ブロックを1チックあたり1000個消す
     *
     * @param queue キュー
     * @return
     */
    fun deleteKusa(queue: Queue<Block>): CompletableFuture<Unit> {
        /**
         * queueを1000こずつに区切る
         */
        val queues = queue.splitQueue()
        var future = CompletableFuture<Unit>()
        queues.forEach { queue ->
            future = future.thenCompose {
                runLater(1) {//1チック毎
                    while (true) {
                        val block = queue.poll() ?: break
                        block.type = Material.DIRT
                    }
                }
            }
        }
        return future
    }

}