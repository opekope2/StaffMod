# Staff

## Staff item

* **Attack Damage**: 4 (❤️❤️)
* **Attack Speed**: 2 (faster than any vanilla weapon)

Every staff has these stats, unless noted otherwise

!!! experiment "Heads up"
    If you have suggestions about the staff item, or ideas about balancing it for survival gameplay,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/6).

### Add an item to the staff

1. Put the staff in one of your hands
2. Put an item in your other hand
3. Press `R` (default keybind)

### Remove an item from the staff

1. Put the staff in one of your hands
2. Make sure your other hand has space for the item in the staff
3. Press `R` (default keybind)

## Supported items in the staff

### Anvil, chipped anvil, damaged anvil

* **Attack Damage**: 40 (❤️×20)
* **Attack Speed**: 0.25
* **Movement Speed**: -100% (also applies, if held in off hand)

When attacking an entity, the anvil has a 12% chance of being damaged. On each attack, the staff plays the [`anvil landed`](https://minecraft.wiki/w/Anvil#Unique) sound.

!!! experiment "Heads up"
    More features are planned for anvils, like instabreaking some blocks, while unable to breaking every other block, or reducing jumping strength.
    If you have suggestions about bone block features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/16).

### Bell

* **Attack Damage**: 8 (❤️❤️❤️❤️)
* **Attack Speed**: 1.5

* **Attack entity**: Vanilla attack behavior + rings the bell
* **Right click**: Rings the bell. Please note that as of now, this doesn't send villagers home, and doesn't make raiders glow, the bell just plays an audible noise.

!!! experiment "Heads up"
    More features are planned for bell, like the features a vanilla bell block has.
    If you have suggestions about bell features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/21).

### Bone block

* **Right click**: Fertilizes the crop or ground the same way a bone meal does.

!!! experiment "Heads up"
    More features are planned for bone block, like increasing its range using the *Efficiency* enchantment.
    If you have suggestions about bone block features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/7).

### Furnace, blast furnace, smoker

* **Attack Damage**: 5 (❤️❤️💔)
* **Attack Speed**: 2

* **Right click (hold)**: Ignites the furnace, and start a timer called `BurnTime`. This timer will increase by 1 each game tick.  
  Each game tick, the furnace tries to smelt the closest item 1.75 blocks in front of the player (±0.5 blocks in each axis).
  The smelting will only be successful, if the item can be smelted in the given furnace, and its count doesn't exceed `BurnTime`.
  If the smelting is successful, the whole stack will be smelted, XP will be dropped according to the vanilla furnace algorithm, and
  `BurnTime` will be decreased by the amount of items smelted.  
  If right click is released, `BurnTime` will be reset.

!!! experiment "Heads up"
    If you have suggestions about furnaces, [join the discussion here](https://github.com/opekope2/StaffMod/discussions/14).

### Lightning rod

* **Right click**: Right click on a block to summon a lightning bolt during thunder. The block (specifically, the block neighboring it
  on the cicked side) needs to have sky light level 15 (just like vanila lightning rod) to summon the lightning.

!!! experiment "Heads up"
    There are more features of the lightning bolt, like sending villagers to their houses, or making raiders glow.
    If you have suggestions about lightning rod, [join the discussion here](https://github.com/opekope2/StaffMod/discussions/23).

### Magma block

* **Attack Damage**: 5 (❤️❤️💔) + 8s of burning (same as [Fire Aspect II](https://minecraft.wiki/w/Fire_Aspect))
* **Attack Speed**: 2

* **Left click**: Shoots a single fireball.
* **Right click (hold)**: Starts shooting fireballs every other game tick.

!!! experiment "Heads up"
    If you have suggestions about magma block features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/17).

### Snow block

* **Left click**: Throws a single snowball at 80m/s (4m/gt).
* **Right click (hold)**: Starts throwing snowballs every game tick at 80m/s (4m/gt) speed.

!!! experiment "Heads up"
    The speed is not yet finalized, and will be balanced in a later release.
    If you have suggestions about snow block features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/4).

### TNT

* **Left click**: Launches a TNT forward at 20m/s (1m/gt), which explodes, if it comes into contact with a block or entity, except its launcher.

!!! experiment "Heads up"
    There are more features of the TNT to discuss, like manually triggering an explosion. If you have suggestions about TNT features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/4).

### Wool (any color)

* **Attack Damage**: 2 (❤️)
* **Attack Speed**: 2

* **Right click (click or hold)**: Places a wool on the wall or ceiling at the crosshair, or a carpet on the ground.

!!! note
    A staff with wool block can't be used to place wool or carpet on existing wool blocks or carpets. This is by design.

!!! experiment "Heads up"
    If you have suggestions about wool features or balancing,
    [join the discussion here](https://github.com/opekope2/StaffMod/discussions/5).

### More to come

Each beta update, I'm adding a new item to the staff. You can see the [GitHub discussions](https://github.com/opekope2/StaffMod/discussions/4)
about the items I'll possibly add in the next update.
