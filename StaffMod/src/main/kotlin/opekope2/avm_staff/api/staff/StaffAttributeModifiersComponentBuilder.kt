/*
 * AvM Staff Mod
 * Copyright (c) 2024 opekope2
 *
 * This mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this mod. If not, see <https://www.gnu.org/licenses/>.
 */

package opekope2.avm_staff.api.staff

import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.registry.entry.RegistryEntry
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import opekope2.avm_staff.util.interactionRange

/**
 * An [AttributeModifiersComponent] builder with the option to add staff defaults.
 */
class StaffAttributeModifiersComponentBuilder {
    private val builder = AttributeModifiersComponent.builder()

    /**
     * Adds an attribute modifier of the given entity attribute for the given slot.
     *
     * @param attribute The entity attribute to add
     * @param modifier  The attribute's modifier
     * @param slot      The slot to modify
     * @see AttributeModifiersComponent.Builder.add
     */
    fun add(
        attribute: RegistryEntry<EntityAttribute>,
        modifier: EntityAttributeModifier,
        slot: AttributeModifierSlot
    ): StaffAttributeModifiersComponentBuilder {
        builder.add(attribute, modifier, slot)
        return this
    }

    /**
     * Adds the default staff modifier of the given entity attribute as [main hand][AttributeModifierSlot.MAINHAND] modifier.
     *
     * @param attribute The entity attribute to add the default value of
     * @see EntityAttributes.GENERIC_ATTACK_DAMAGE
     * @see EntityAttributes.GENERIC_ATTACK_SPEED
     * @see EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE
     * @see EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE
     */
    fun addDefault(attribute: RegistryEntry<EntityAttribute>): StaffAttributeModifiersComponentBuilder {
        add(
            attribute,
            when (attribute) {
                EntityAttributes.GENERIC_ATTACK_DAMAGE -> attackDamage(4.0)
                EntityAttributes.GENERIC_ATTACK_SPEED -> attackSpeed(2.0)
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE -> interactionRange(1.0)
                EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE -> interactionRange(1.0)
                else -> throw IllegalArgumentException("Attribute has no default value")
            },
            AttributeModifierSlot.MAINHAND
        )
        return this
    }

    /**
     * Builds an [AttributeModifiersComponent] from the previously supplied attribute modifiers.
     *
     * @see AttributeModifiersComponent.Builder.build
     */
    fun build(): AttributeModifiersComponent = builder.build()

    companion object {
        /**
         * Creates an [AttributeModifiersComponent] with every default staff attribute.
         */
        @JvmStatic
        fun default() = StaffAttributeModifiersComponentBuilder()
            .addDefault(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            .addDefault(EntityAttributes.GENERIC_ATTACK_SPEED)
            .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
            .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
            .build()
    }
}
