# Supported items in a staff

Each update, I'm adding a new item to the staff, see the [roadmap](https://github.com/opekope2/StaffMod/discussions/31).

Throughout this page, [use](https://minecraft.wiki/w/Controls#Use_Item/Place_Block), [attack](https://minecraft.wiki/w/Controls#Attack/Destroy), and [sneak](https://minecraft.wiki/w/Controls#Sneak) keys refer to the linked key bindings.

## No item

**Staff Mod 0.1.0 alpha+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

Every staff has these stats, unless noted otherwise

## Anvil, chipped anvil, damaged anvil

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/16)
**Staff Mod 0.6.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 40 (:mc-heart:×20)
* **Attack Speed**: 0.25/s
* **Movement Speed**: -100% (in either hand)
* **Jump Strength**: -100% (in either hand)

* **Attack Entity**: not possible
* **Attack Entity (while falling)**: Deal up to 40 (:mc-heart:×20) damage to entities within a sphere around the attacked entity's feet with a radius of fall distance/20. The damage dealt is directly proportional to the fall distance (same as a falling anvil), and inversely proportional to the distance to the attacked entity. See [`AnvilHandler.aoeAttack`](https://github.com/opekope2/StaffMod/blob/fe437e857f22405ffef3cc039a5dbcb650cdc7f7/StaffMod/src/main/kotlin/opekope2/avm_staff/internal/staff_handler/AnvilHandler.kt#L88-L105) for the alorithm. Upon successful attack, the anvil has 5% chance to degrade for every block fallen after the 1st block, the attacker's fall distance is reset to 0 (but the velocity will be unaffected), and the [`anvil landed`](https://minecraft.wiki/w/Anvil#Unique) sound is played
* **Break Block**: not possible

## Bell

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/21)
**Staff Mod 0.8.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 8 (:mc-heart::mc-heart::mc-heart::mc-heart:)
* **Attack Speed**: 1.5/s
* **Range**: +1

* **Attack Entity**: Ring the bell without sending villagers home or making raiders glow
* **Use**: Ring the bell without sending villagers home or making raiders glow

## Bone block

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/7)
**Staff Mod 0.3.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 5 (:mc-heart::mc-heart::mc-halfheart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Use on Block**: Fertilize the crop, ground, or water the same way bone meal does

## Cake

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/40)
**Staff Mod 0.16.0+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Attack (air)**: Throw a cake
* **Use**: Throw a cake every game tick, twice as fast

## Campfire, soul campfire

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/32)
**Staff Mod 0.13.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Use (hold)**: Throw flame like a Flammenwerfer. Every entity touching the flame will be set on fire. The longer they stand in fire, the longer they are going to burn for. The flame stops when it reaches a block, and has a chance to set it on fire (see table below)
* **Use (hold, while sneaking midair)**: Propel yourself backwards. Look down to fly up. Soul campfire can go faster than campfire. Sneaking is only required to start the flight, it doesn't need to be held down to fly

Item in staff | Fire duration     | Damage, when standing in fire    | Inflicted burning time/tick     | Flame chance/tick to cause fire
--------------|-------------------|----------------------------------|---------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------
Campfire      | 4s, when attacked | 1 (:mc-halfheart:) / 10 ticks \* | +1 tick, when standing in fire  | 5% ([non-flammable](https://minecraft.wiki/w/Fire#Non-flammable_blocks)), 25% ([flammable](https://minecraft.wiki/w/Fire#Flammable_blocks))
Soul campfire | 6s, when attacked | 2 (:mc-heart:) / 10 ticks \*     | +2 ticks, when standing in fire | 10% ([non-flammable](https://minecraft.wiki/w/Fire#Non-flammable_blocks)), 50% ([flammable](https://minecraft.wiki/w/Fire#Flammable_blocks))

\* [Immunity](https://minecraft.wiki/w/Damage#Immunity) lasts for 10 game ticks (0.5s)

## Furnace, blast furnace, smoker

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/14)
**Staff Mod 0.7.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 10 (:mc-heart::mc-heart::mc-heart::mc-heart::mc-heart:)
* **Attack Speed**: 1.25/s
* **Range**: +1

* **Use (hold)**: Ignite the furnace, and start a timer, which increases by 1 each game tick.
  Each game tick, the furnace tries to smelt the closest item to the furnace in the staff or the tip of the staff (±0.5 blocks in each axis).
  If the item can be smelted in the given furnace, and its count doesn't exceed the timer's value, the whole stack will be smelted, XP will be dropped (same amount as a vanilla furnace), and the timer will be decreased by the amount of items smelted. The timer is reset when use key is released

## Gold Block

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/43)
**Staff Mod 0.17.0+**{.chip-darkgreen}

* **Attack Damage**: 14 (:mc-heart::mc-heart::mc-heart::mc-heart::mc-heart::mc-heart::mc-heart:)
* **Attack Speed**: 1/s
* **Range**: +1

* **Attack (block)**: Break blocks that are not stronger than Obsidian in a 3x3x2 area in front of the player, except for the 4 corners facing away from the player

## Lightning rod

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/23)
**Staff Mod 0.9.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +2

* **Attack Entity**: If thundering and not on cooldown, summon a lightning bolt at the target's position. The target's block position needs to have sky light level 15 (just like vanila lightning rod) to summon the lightning bolt
* **Use on Entity**: If thundering and not on cooldown, summon a lightning bolt at the target's position. The target's block position needs to have sky light level 15 (just like vanila lightning rod) to summon the lightning bolt
* **Use on Block**: If thundering and not on cooldown, summon a lightning bolt at the block's clicked side. This side needs to have sky light level 15 (just like vanila lightning rod) to summon the lightning bolt

## Magma block

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/17)
**Staff Mod 0.5.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 10 (:mc-heart::mc-heart::mc-heart::mc-heart::mc-heart:) + 8s of burning (same as [Fire Aspect II](https://minecraft.wiki/w/Fire_Aspect))
* **Attack Speed**: 1.25/s
* **Range**: +1

* **Attack (air)**: Shoot a single fireball
* **Use (hold)**: Shoot a fireball every other game tick

## Snow block

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/4)
**Staff Mod 0.2.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Attack (air)**: Throw a single snowball
* **Use (hold)**: Throw a snowball every game tick

## TNT

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/25)
**Staff Mod 0.10.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Attack (air)**: Throw a TNT forwards, which explodes when it collides with a block or entity

## Wither Skeleton Skull

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/36)
**Staff Mod 0.14.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 4 (:mc-heart::mc-heart:) + 10s of [Wither I](https://minecraft.wiki/w/Wither_(effect)) ([Wither II](https://minecraft.wiki/w/Wither_(effect)) on [hard difficulty](https://minecraft.wiki/w/Difficulty#Hard))
* **Attack Speed**: 2/s
* **Range**: +1

* **Attack (air)**: Shoot a black wither skull
* **Use (hold)**: Shoot wither skulls up to 1 second, with 10% chance for being a blue skull

## Wool (any color)

[**Discuss**{.chip-darkblue}](https://github.com/opekope2/StaffMod/discussions/5)
**Staff Mod 0.4.0-beta+**{.chip-darkgreen}

* **Attack Damage**: 2 (:mc-heart:)
* **Attack Speed**: 2/s
* **Range**: +1

* **Use (click or hold)**: Place a wool on the wall or ceiling at the crosshair, or a carpet on the ground. Can't place wool or carpet against other wool or carpet
