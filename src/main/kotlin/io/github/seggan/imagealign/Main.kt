package io.github.seggan.imagealign

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.Color
import java.awt.Component
import java.awt.FileDialog
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.io.File
import java.io.FilenameFilter
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.ProgressMonitor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.exitProcess

fun main(): Unit = runBlocking {
    val files = chooseFile("Select images to align", "png", "jpg", "jpeg")
    if (files.isEmpty()) exitProcess(0)

    val centerpoints = getCenterpoints(files)

    var destination: File
    var counter = 0
    do {
        destination = files.first().resolveSibling("aligned-${counter++}")
    } while (destination.exists())
    destination.mkdir()

    val center = centerpoints.average()
    val xOffset = center.x.toInt()
    val yOffset = center.y.toInt()

    val progressMonitor = ProgressMonitor(null, "Processing images", "", 0, files.size)
    var progress = 0

    withContext(Dispatchers.Default) {
        for ((file, centerpoint) in files.zip(centerpoints)) {
            if (progressMonitor.isCanceled) break
            progressMonitor.note = "Processing ${file.name}"
            val image = ImageIO.read(file)
            val width = image.width
            val height = image.height
            val medianColor = image.medianEdgeColor()
            val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val xNew = x - xOffset + centerpoint.x.toInt()
                    val yNew = y - yOffset + centerpoint.y.toInt()
                    if (xNew in 0 until width && yNew in 0 until height) {
                        newImage.setRGB(x, y, image.getRGB(xNew, yNew))
                    } else {
                        newImage.setRGB(x, y, medianColor.rgb)
                    }
                }
            }
            ImageIO.write(newImage, "png", destination.resolve(file.name))
            progressMonitor.setProgress(++progress)
        }
    }

    exitProcess(0)
}

private fun BufferedImage.medianEdgeColor(): Color {
    val edgeRed = mutableListOf<Int>()
    val edgeGreen = mutableListOf<Int>()
    val edgeBlue = mutableListOf<Int>()
    for (x in 0 until width) {
        val color = Color(getRGB(x, 0))
        edgeRed.add(color.red)
        edgeGreen.add(color.green)
        edgeBlue.add(color.blue)
    }
    for (y in 0 until height) {
        val color = Color(getRGB(0, y))
        edgeRed.add(color.red)
        edgeGreen.add(color.green)
        edgeBlue.add(color.blue)
    }
    for (x in 0 until width) {
        val color = Color(getRGB(x, height - 1))
        edgeRed.add(color.red)
        edgeGreen.add(color.green)
        edgeBlue.add(color.blue)
    }
    for (y in 0 until height) {
        val color = Color(getRGB(width - 1, y))
        edgeRed.add(color.red)
        edgeGreen.add(color.green)
        edgeBlue.add(color.blue)
    }

    val red = edgeRed.sorted()[edgeRed.size / 2]
    val green = edgeGreen.sorted()[edgeGreen.size / 2]
    val blue = edgeBlue.sorted()[edgeBlue.size / 2]
    return Color(red, green, blue)
}

private fun List<Vector>.average(): Vector {
    var x = 0.0
    var y = 0.0
    for (point in this) {
        x += point.x
        y += point.y
    }
    return Vector(x / size, y / size)
}

private suspend fun getCenterpoints(files: List<File>): List<Vector> {
    val centerpoints = mutableListOf<Vector>()

    val frame = JFrame()
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
    for (file in files) {
        val image = ImageComponent(ImageIO.read(file))
        frame.add(image)
        frame.title = file.name

        frame.pack()

        val clickEvent = image.waitForMouseClick()
        var pos = Vector(clickEvent.x, clickEvent.y)
        pos -= image.imagePosition
        pos /= image.imageScale
        centerpoints.add(pos)

        frame.remove(image)
    }
    frame.dispose()

    return centerpoints
}

private suspend fun Component.waitForMouseClick(): MouseEvent {
    return suspendCoroutine { continuation ->
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                removeMouseListener(this)
                continuation.resume(e)
            }

            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
    }
}

private fun chooseFile(title: String, vararg extensions: String): List<File> {
    @Suppress("USELESS_CAST")
    val filePicker = FileDialog(null as? JFrame, title, FileDialog.LOAD)
    filePicker.isMultipleMode = true
    if (System.getProperty("os.name").startsWith("Windows")) {
        filePicker.file = extensions.joinToString(";") { "*.$it" }
    } else {
        filePicker.filenameFilter = FilenameFilter { _, name -> extensions.any { name.endsWith(".$it") } }
    }
    filePicker.isVisible = true
    return filePicker.files.toList()
}

