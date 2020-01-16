package club.sk1er.vigilance.gui.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.StencilEffect
import club.sk1er.vigilance.data.PropertyData
import net.minecraft.client.Minecraft
import java.awt.Color

class Slider(private val prop: PropertyData) : UIComponent() {
    private var grabbed = false
    var value = 0.5f

    private val slide = UIRoundedRectangle(1f).constrain {
        y = CenterConstraint()
        width = RelativeConstraint()
        height = 2.pixels()
        color = Color(120, 120, 120, 0).asConstraint()
    }.enableEffect(StencilEffect()) childOf this

    private val slideBackground = UIBlock().constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
        color = Color(0, 170, 165, 0).asConstraint()
    } childOf slide

    private val knob = Knob(10)

    private val minText = UIText(prop.property.min.toString()).constrain {
        x = RelativeConstraint(1.25f) - Minecraft.getMinecraft().fontRendererObj.getStringWidth(prop.property.min.toString()).pixels()
        y = CenterConstraint()
    } childOf this

    private val maxText = UIText(prop.property.max.toString()).constrain {
        x = RelativeConstraint(2.25f) + Minecraft.getMinecraft().fontRendererObj.getStringWidth(prop.property.max.toString()).pixels()
        y = CenterConstraint()
    } childOf this

    private val currentText = (UIText(prop.getValue<Int>().toString()).constrain {
        y = CenterConstraint() + (10).pixels()
    } childOf this) as UIText


    init {

        knob.constrain {
            x = 100.pixels(true)
        }.onMouseEnter {
            if (!grabbed) knob.hover()
        }.onMouseLeave {
            if (!grabbed) knob.unHover()
        }.onMouseClick { _, _, _ ->
            knob.grab()
            grabbed = true
        }.onMouseRelease {
            if (!grabbed) return@onMouseRelease
            knob.release()
            grabbed = false
        } childOf this

        onMouseDrag { mouseX, _, _ ->
            if (!grabbed) return@onMouseDrag
            knob.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, (mouseX - knob.getRadius() / 2).pixels().minMax(0.pixels(), 0.pixels(true)))
            }
            slideBackground.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, mouseX.pixels().minMax(0.pixels(), RelativeConstraint()))
            }



            value = slideBackground.getWidth() / (slide.getRight() - slide.getLeft())
            val tmp = (prop.property.min + ((prop.property.max - prop.property.min) * value)).toInt()
            println(tmp)
            prop.setValue(tmp)
            currentText.setText(tmp.toString())
            currentText.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, (mouseX - knob.getRadius() / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(tmp.toString()) / 2).pixels().minMax(0.pixels(), 0.pixels(true)))
            }
        }

        constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            width = RelativeConstraint(0.75f)
            height = 20.pixels()
        }
    }

    fun fadeIn() {
        knob.fadeIn()
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 255).asConstraint()) }
        slideBackground.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 255).asConstraint()) }
    }

    fun fadeOut() {
        knob.fadeOut()
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 0).asConstraint()) }
        slideBackground.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 0).asConstraint()) }
    }
}