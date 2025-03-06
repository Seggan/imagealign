package io.github.seggan.imagealign

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import javax.swing.JComponent
import kotlin.math.min

class ImageComponent(private val image: Image) : JComponent() {

    var imageScale: Double = 1.0
        private set

    var imagePosition: Vector = Vector(0, 0)
        private set

    override fun getPreferredSize(): Dimension {
        return Dimension(image.getWidth(this), image.getHeight(this))
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        // Draw the image scaled to the size of the component, making sure aspect ratio is preserved
        val imageWidth = image.getWidth(this)
        val imageHeight = image.getHeight(this)
        val widthScale = this.width.toDouble() / imageWidth.coerceAtLeast(1)
        val heightScale = this.height.toDouble() / imageHeight.coerceAtLeast(1)
        imageScale = min(widthScale, heightScale)
        val scaledWidth = (imageWidth * imageScale).toInt()
        val scaledHeight = (imageHeight * imageScale).toInt()
        val x = (this.width - scaledWidth) / 2
        val y = (this.height - scaledHeight) / 2
        imagePosition = Vector(x, y)
        g.drawImage(image, x, y, scaledWidth, scaledHeight, this)
    }
}