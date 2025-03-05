package io.github.seggan.imagealign

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.runBlocking
import javax.imageio.ImageIO

fun main() {
    FileKit.init("ImageAlign")
    val files = runBlocking {
        FileKit.openFilePicker(
            type = FileKitType.Image,
            mode = FileKitMode.Multiple()
        ) ?: emptyList()
    }.map { it.file }
    println(files)
}
