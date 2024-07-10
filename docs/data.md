# Data pack guide

## Game rules

### `throwableCakes`

**Staff Mod 0.16.0+**{.chip-darkgreen}

When enabled, players can throw cakes by right-clicking, and dispensers will shoot cakes instead of dropping them as an item.

## Loot tables

### `avm_staff:loot_tables/add_loot_pool/chests/trial_chambers/reward_unique.json`

**Staff Mod 0.15.0+**{.chip-darkgreen}

| Item                                                                                                                      | Weight |
|---------------------------------------------------------------------------------------------------------------------------|--------|
| Nothing                                                                                                                   | 5      |
| ![Staff infustion smithing template](images/staff_infusion_smithing_template.png){.x16} Staff infustion smithing template | 1      |

When generating loot from `minecraft:loot_tables/chests/trial_chambers/reward_unique.json` loot table, Minecraft will also draw an item from this loot table.

### `avm_staff:loot_tables/add_loot_pool/chests/bastion_treasure.json`

**Staff Mod 0.15.0+**{.chip-darkgreen}

| Item                                                                                | Weight |
|-------------------------------------------------------------------------------------|--------|
| ![Crown of King Orange](images/crown_of_king_orange.png){.x16} Crown of King Orange | 1      |

When generating loot from `minecraft:loot_tables/chests/bastion_treasure.json` loot table, Minecraft will also draw an item from this loot table.

## Tags

### `avm_staff:staffs`

**Staff Mod 0.12.0-beta+**{.chip-darkgreen}
**Removed in Staff Mod 0.16.0**{.chip-red}

Any item, which extends [`StaffItem`](kdoc/latest/-staff%20-mod/opekope2.avm_staff.api.item/-staff-item/index.html) is treated as a functional staff by Staff Mod. It is no longer needed to tag it with `avm_staff:staffs`.
