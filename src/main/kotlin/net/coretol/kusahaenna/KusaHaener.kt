package net.coretol.kusahaenna

import com.google.common.collect.Queues
import net.coretol.kusahaenna.util.runLater
import net.kyori.adventure.text.Component
import net.minecraft.server.v1_16_R3.HeightMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.SynchronousQueue
import kotlin.math.pow

/**
 * 地中にある草を消すやつ
 *
 * @property center 中心の座標
 * @property radius 半径（チャンク）
 * @property sender ログを出す用のCommandSender
 * @constructor Create empty Kusa haener
 */
class KusaHaener(val center:Location,val radius:Int,val sender:CommandSender? = null) {
    /**
     * 地中に埋まっている草を調べてQueueとして返す
     *
     * @return queue
     */
    fun check(): CompletableFuture<Queue<Block>> = CompletableFuture.supplyAsync {
            val queue = Queues.newConcurrentLinkedQueue<Block>()
            val centerChunk = center.chunk
            val world = centerChunk.world
            var chunkCount = 0
            val maxChunkCount = (radius*2.0+1).pow(2).toInt()
            (centerChunk.x-radius..centerChunk.x+radius).forEach { chunkX ->
                (centerChunk.z-radius..centerChunk.z+radius).forEach { chunkZ ->
                    val chunk = world.getChunkAtAsync(chunkX,chunkZ).get()
                    var count = 0
                    (0..15).forEach { x->
                        (0..15).forEach { z->
                            (world.minHeight until world.maxHeight).forEach { y ->
                                val block = chunk.getBlock(x,y,z)
                                if(block.type == Material.GRASS_BLOCK){
                                    if(chunk.getBlock(x,(y+1).coerceAtMost(world.maxHeight-1),z).isSolid) {
                                        queue.add(block)
                                        count++
                                    }
                                }
                            }
                        }
                    }
                    chunkCount++
                    if(count>0) {
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
    fun deleteKusa(queue:Queue<Block>): CompletableFuture<Unit> {
        var count = 0
        val queues = mutableListOf<Queue<Block>>()
        run loop@{
            while (true)  {
                val thousandQueue = Queues.newConcurrentLinkedQueue<Block>()
                for (i in 0..1000) {
                    val block = queue.poll() ?: return@loop
                    thousandQueue.add(block)
                    count++
                }
                queues.add(thousandQueue)
            }
        }
        var future = CompletableFuture<Unit>()
        queues.forEach {queue->
            future = future.thenCompose {
                runLater(1) {
                    while(true) {
                        val block = queue.poll() ?: break
                        block.type=Material.DIRT
                    }
                }
            }
        }
        return future
    }

}