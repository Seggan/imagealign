package io.github.seggan.imagealign

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    FileKit.init("ImageAlign")
    val files = FileKit.openFilePicker(
        type = FileKitType.Image,
        mode = FileKitMode.Multiple()
    )?.map { it.file } ?: return
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

    println(centerpoints)
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

