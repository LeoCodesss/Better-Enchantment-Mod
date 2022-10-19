# Better-Enchantment-Mod
Removes the restrictions from the *enchantment* command. Beware that Minecrafts limit on this is 127, every number higher than that will be set to 0.

Also this mod adds a *disenchant* command, that lets you remove specific enchantments from your items.

Syntax: /disenchant <target> <enchantment> <level>
                                              ^ optional if you decide to use a level it will be subtracted from the level of your enchanted item.

**Note:** you need at least a permission-level of 2 (simple op)

**Known Issues:** When you play with mods that adds enchantments and two of your mods have an enchantment with the same name but different mod ids, the *disenchant* command will remove both enchantments instead of only one.
