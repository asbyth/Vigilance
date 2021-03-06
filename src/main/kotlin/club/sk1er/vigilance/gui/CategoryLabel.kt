package club.sk1er.vigilance.gui

import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.ChildBasedMaxSizeConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.vigilance.data.Category
import java.awt.Color

class CategoryLabel(private val gui: SettingsGui, private val category: Category) : UIContainer() {
    private val text = UIText(category.name).constrain {
        color = VigilancePalette.MID_TEXT.asConstraint()
        textScale = (1.5f).pixels()
    } childOf this

    var isSelected = false

    init {
        constrain {
            y = SiblingConstraint(5f)
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        onMouseClick {
            if (!isSelected) {
                select()
            }
        }

        onMouseEnter {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.ACCENT.asConstraint())
                }
            }
        }

        onMouseLeave {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.MID_TEXT.asConstraint())
                }
            }
        }
    }

    fun select() {
        gui.selectCategory(category)

        isSelected = true
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.ACCENT.asConstraint())
        }
    }

    fun deselect() {
        isSelected = false
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, VigilancePalette.MID_TEXT.asConstraint())
        }
    }
}