package net.coretol.kusahaenna.command

import net.coretol.kusahaenna.KusaHaener
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKusa:CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if(sender is Player) {
            val radius = args.getOrNull(0)?.toIntOrNull() ?: 5//半径
            val kusa = KusaHaener(sender.location,radius,sender)//草を消すやつのインスタンス
            sender.sendMessage("${ChatColor.GREEN}[草] 草を探しています")
            kusa.check().whenComplete { queue, exception ->
                exception?.let {//error
                    it.printStackTrace()
                    sender.sendMessage("${ChatColor.RED}[草] エラー:${it.message}")
                } ?: run {
                    sender.sendMessage("${ChatColor.GREEN}[草] あなたの周りの${radius * radius}個のチャンクの中で${queue.size}個の地中に埋まっている草を見つけました。削除を開始します。")
                    kusa.deleteKusa(queue).whenComplete { _, exception ->
                        exception?.let {
                            it.printStackTrace()
                            sender.sendMessage("${ChatColor.RED}[草] エラー:${it.message}")
                        } ?: run {
                            sender.sendMessage("${ChatColor.GREEN}[草] 草を削除しました")
                        }
                    }
                }
            }
            true
        } else {
            sender.sendMessage("This command can be only executed by player")
            false
        }
    }
}