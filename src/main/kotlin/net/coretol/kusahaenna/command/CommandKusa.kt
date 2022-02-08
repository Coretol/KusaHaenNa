package net.coretol.kusahaenna.command

import net.coretol.kusahaenna.task.TaskManager
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * executor of /kusa
 */
class CommandKusa : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (sender is Player) {
            val radius = args.getOrNull(0)?.toIntOrNull() ?: 5//半径
            sender.sendMessage("${ChatColor.GREEN}[草] 草を探しています")
            //草を非同期でチェックする。
            TaskManager.startCheckTask(sender, sender.location, radius).whenComplete = fun(queue) {
                sender.sendMessage("${ChatColor.GREEN}[草] あなたの周りの${radius * radius}個のチャンクの中で${queue.size}個の地中に埋まっている草を見つけました。削除を開始します。")
                TaskManager.startToDirtTask(sender, queue).whenComplete = fun(queue) {
                    sender.sendMessage("${ChatColor.GREEN}[草] 草を削除しました")
                }
            }
            true
        } else {
            sender.sendMessage("This command can be only executed by player")
            false
        }
    }
}