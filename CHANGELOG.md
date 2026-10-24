# Changelog

## 0.9.0

A stabilization release: new magic items, a safer machine and tool progression, shader pack fixes and a dedicated server startup fix.

### Added
- The Omnitome: holds spell books and casts the selected one, with a spell wheel to choose it and an icon for every spell. Craft it at the blasphemous altar.
- Three staffs: the Incomplete Staff of the Pure Blooded, the Staff of Scarlet Cinder and the Staff of the Golden Sorrow, with their own recipes. A staff casts the spell book held in the other hand, or the one selected in the Omnitome.
- Staff affinity bonus: the Incomplete Staff favors the Blood books and the Scarlet Cinder the Blood Fire books. With up to five nearby targets under Bleeding (Incomplete Staff) or Blood Fire (Scarlet Cinder) a staff gives up to +30% spell damage, -25% Crimson Veil cost and -25% cooldown. A book of another family gets 40% of that. The item tooltip shows it. The Staff of the Golden Sorrow works as a kill streak: each kill you make while holding it adds one charge (up to five) and, after five seconds without a kill, it loses one charge every three seconds.
- The orb of a staff shows its charge: the blood orb pulses faster and its grooves get finer, the golden orb spins faster, grows and gains one orbiting gold shard for each charge, the scarlet cinder burns faster, grows and gathers more blobs, every orb gives a short pulse each time the charge grows and a stronger flash when it is full. Only the player holding the staff sees and hears it: a rising note for each marked target, a sound at full charge, one when it fades a soft ember loop on the Scarlet Cinder while it is full, and a heartbeat on the Incomplete Staff that follows the pulse of its orb. The sounds are CC0 recordings from Freesound, edited.
- Spell books can be used on their own, with a staff, with the Omnitome, or with a staff and the Omnitome.
- Rhnull blood engine: resurrection mechanics and the tether mode (work in progress).
- Bound blood flask.
- Sound subtitles and the missing names for five entities.

### Changed
- Tools and armor now go sanguinite, blasphemite, rhnull, from weakest to strongest. Knockback resistance uses the vanilla scale.
- Every block now has a declared mining tool and tier: blasphemite blocks and ore need a sanguinite tool, and the Rhnull block needs a blasphemite tool. Hardness and drops were corrected.
- Sanguinite tanks, pipes and condensers refuse dangerous fluids (corrupted blood and viscous blasphemy), and a sanguinite pipe that ends up holding one bursts into a corrupted blood block. The rhnull versions take every fluid.
- The Blood bow, arrow and armor are shown as Sanguinite bow, arrow and armor, because they are made of sanguinite. Their ids (`bloodyhell:blood_bow`, `blood_helmet`...) do not change, so recipes, scripts and quests that name them keep working.
- Plants drop like vanilla grass.
- The Offspring of the Unknown flees when it is badly hurt.
- Reworked visuals: the Frenzy overlay (more vanilla, less motion sickness), the blood decal and the blood blob.
- Spell book costs: Blood Sphere costs 10 Crimson Veil and Blood Scratch 5 per cast.
- Dependency ranges are now declared: GeckoLib 4.8 up to 5, Patchouli 1.20.1-85 or newer, PlayerAnimator 1.0.2-rc1 or newer.

### Fixed
- A dedicated server no longer crashes on startup because of a client class loaded by the item code.
- Blood Sphere and Blood Scratch charged Crimson Veil twice per cast.
- A staff cast the sequential spell books twice per cycle and spent the Crimson Veil twice.
- A spell book held on its own could not cast most of its spells.
- Several visual problems with shader packs: the effects drawn through walls, the Omnitome pages, the frenzied trails, the camera bobbing of the blood decal, the Rubidium translucent bug on cutout blocks, and the spell effects not following the shader passes.
- The chalice of the damned on the top block of the portal frame.
- Missing textures, models, blockstates, animation frames and translation entries found by the test world.
- Crashes of the Cyclops and the Twin Daggers Clone with no target.
- The target of the unknown arms and of the sanguine sacrifice is now kept when the world is saved.
- The blood lily drops itself when picked on the water.

### Removed
- The visceral infected vein and the unused infestation decal entity.
- Blood scythe, materialized soul, blood wood chest, jumpy block, eight ball item, the mod paintings, and the dirty blood flower item and particle. Existing copies in saved worlds disappear.
