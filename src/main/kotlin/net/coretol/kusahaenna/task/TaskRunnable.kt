package net.coretol.kusahaenna.task

interface TaskRunnable<out T> {
    fun start()
    var isComplete: Boolean
    var progress:Int
    val maxProgress:Int
    var whenComplete: (@UnsafeVariance T) -> Unit
    val taskID:Int
}