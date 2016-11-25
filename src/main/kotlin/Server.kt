import java.io.BufferedInputStream
import java.io.File
import java.net.InetSocketAddress
import java.net.ServerSocket


/**
 * Created by ASemelit on 24.11.2016.
 */
object ServerSocketHolder {
    val serverSocket = ServerSocket()

    init {
        serverSocket.bind(InetSocketAddress(3301))
    }
}

fun main(args: Array<String>) {
    println("Socket server started")
    var socket = ServerSocketHolder.serverSocket.accept()
    var input = BufferedInputStream(socket.inputStream)
    input.reader().use {
        it.forEachLine { line ->
            println(line)
        }
    }
    socket.outputStream.writer().use { writer ->
        File(".").walkTopDown().forEach { file ->
            writer.write(file.canonicalPath + "\n")
            writer.flush()
        }
    }
}


