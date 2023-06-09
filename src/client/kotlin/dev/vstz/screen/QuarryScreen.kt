package dev.vstz.screen

import com.mojang.blaze3d.systems.RenderSystem
import dev.vstz.block.BasicQuarryEntity
import dev.vstz.world.QuarryChunk
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class QuarryScreen(handler: QuarryScreenHandler?, inventory: PlayerInventory?, title: Text?) :
    HandledScreen<QuarryScreenHandler>(handler, inventory, title) {

    data class Color(val r: Float, val g: Float, val b: Float, val a: Float)
    companion object {
        val RED = Color(1.0f, 0.0f, 0.3f, 1.0f)
        val BG = Color(0.3f, 0.3f, 0.3f, 1.0f)
        val GREY = Color(0f, 0f, 0f, 0.3f)
        val BLUE = Color(0.0f, 0.3f, 1.0f, 1.0f)
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        fill(matrices, 0, 0, backgroundWidth, 130, BLUE)
        fill(matrices, 5, 5, backgroundWidth - 5, 130 - 5, BG)
        textRenderer.draw(matrices, title, titleX.toFloat(), titleY.toFloat() + 10, 0xffffff)

        val energyCurrent = handler.delegate.get(0)
        val energyMax = BasicQuarryEntity.MAX_ENERGY
        val maxWidth = backgroundWidth - 40
        val fillState = (energyCurrent.toFloat() / energyMax.toFloat()) * maxWidth
        drawBox(matrices, 20, 40, maxWidth, 20, GREY)
        drawBox(matrices, 20, 40, fillState.toInt(), 20, RED)
        drawCenteredText(matrices, "${energyCurrent}E / ${energyMax}E", 40f, 20f)

        // Blocks
        val blocks = handler.delegate.get(1)
        val blocksMax = QuarryChunk.MAX_CAPACITY
        val fillStateBlock = (blocks.toFloat() / blocksMax.toFloat()) * maxWidth
        drawBox(matrices, 20, 80, maxWidth, 20, GREY)
        drawBox(matrices, 20, 80, fillStateBlock.toInt(), 20, BLUE)
        drawCenteredText(matrices, "$blocks Left", 80f, 20f)
    }

    fun drawCenteredText(matrices: MatrixStack?, text: String, y: Float, height: Float, color: Int = 0xffffff) {
        val tmpX = (backgroundWidth - textRenderer.getWidth(text)).toFloat() * 0.5f
        textRenderer.draw(matrices, text, tmpX, y + (height - textRenderer.fontHeight) / 2, color)
    }


    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    fun drawBox(matrices: MatrixStack?, x: Int, y: Int, width: Int, height: Int, color: Color) {
        fill(matrices, x, y, x + width, y + height, color)
    }


    private fun fill(matrices: MatrixStack?, _x1: Int, _y1: Int, _x2: Int, _y2: Int, color: Color) {
        val matrix = matrices!!.peek().positionMatrix
        var x1 = _x1
        var y1 = _y1
        var x2 = _x2
        var y2 = _y2
        var i: Int
        if (x1 < x2) {
            i = x1
            x1 = x2
            x2 = i
        }
        if (y1 < y2) {
            i = y1
            y1 = y2
            y2 = i
        }
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(color.r, color.g, color.b, color.a).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(color.r, color.g, color.b, color.a).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(color.r, color.g, color.b, color.a).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(color.r, color.g, color.b, color.a).next()
        BufferRenderer.drawWithShader(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }
}