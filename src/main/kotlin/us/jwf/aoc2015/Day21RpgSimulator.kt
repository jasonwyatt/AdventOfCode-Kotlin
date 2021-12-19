package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

class Day21RpgSimulator : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val boss = Character(name = "Boss", damage = 8, defense = 2, hp = 109)
    return Kit.generateKits()
      .sorted()
      .first { kit ->
        val player = Character.buildFrom("Player", kit)
        val winner = Character.fight(player, boss)
        winner.name == "Player"
      }.cost
  }

  override suspend fun executePart2(input: Reader): Int {
    val boss = Character(name = "Boss", damage = 8, defense = 2, hp = 109)
    return Kit.generateKits()
      .sorted()
      .reversed()
      .first { kit ->
        val player = Character.buildFrom("Player", kit)
        val winner = Character.fight(player, boss)
        winner.name == "Boss"
      }
      .cost
  }

  data class Character(val name: String, val damage: Int, val defense: Int, val hp: Int = 100) {
    fun attack(other: Character): Character =
      other.copy(hp = other.hp - maxOf(damage - other.defense, 1))

    companion object {
      fun fight(a: Character, b: Character): Character {
        var attacking = a
        var defending = b
        while (attacking.hp > 0 && defending.hp > 0) {
          val newAttacking = attacking.attack(defending)
          defending = attacking
          attacking = newAttacking
        }
        val winner = if (attacking.hp > 0) attacking else defending
        return winner
      }

      fun buildFrom(name: String, kit: Kit): Character {
        val (damage, defense) = kit.stats
        return Character(name, damage, defense)
      }
    }
  }

  sealed class Item {
    abstract val cost: Int
    abstract val damage: Int
    abstract val defense: Int

    data class Weapon(
      override val cost: Int,
      override val damage: Int
    ) : Item() {
      override val defense: Int = 0
    }

    data class Armor(
      override val cost: Int,
      override val defense: Int
    ) : Item() {
      override val damage: Int = 0
    }

    data class Ring(
      override val cost: Int,
      override val damage: Int,
      override val defense: Int
    ) : Item()

    companion object {
      val WEAPONS = setOf(
        Weapon(8, 4),
        Weapon(10, 5),
        Weapon(25, 6),
        Weapon(40, 7),
        Weapon(74, 8),
      )

      val ARMOR = setOf(
        Armor(13, 1),
        Armor(31, 2),
        Armor(53, 3),
        Armor(75, 4),
        Armor(102, 5),
      )

      val RINGS = setOf(
        Ring(25, 1, 0),
        Ring(50, 2, 0),
        Ring(100, 3, 0),
        Ring(20, 0, 1),
        Ring(40, 0, 2),
        Ring(80, 0, 3),
      )
    }
  }

  data class Kit(
    val weapon: Item.Weapon?,
    val armor: Item.Armor?,
    val ring1: Item.Ring?,
    val ring2: Item.Ring?
  ) : Comparable<Kit> {
    val cost: Int =
      (weapon?.cost ?: 0) + (armor?.cost ?: 0) + (ring1?.cost ?: 0) + (ring2?.cost ?: 0)

    val stats: Pair<Int, Int> =
      ((weapon?.damage ?: 0) + (ring1?.damage ?: 0) + (ring2?.damage ?: 0)) to
        ((armor?.defense ?: 0) + (ring1?.defense ?: 0) + (ring2?.defense ?: 0))

    override fun compareTo(other: Kit): Int = cost - other.cost

    companion object {
      fun generateKits(
        choice: Int = 0,
        soFar: Kit = Kit(null, null, null, null)
      ): Set<Kit> {
        val result = mutableSetOf<Kit>()
        when (choice) {
          0 -> {
            Item.WEAPONS.forEach { result.addAll(generateKits(1, soFar.copy(weapon = it))) }
          }
          1 -> {
            result.addAll(generateKits(2, soFar))
            Item.ARMOR.forEach { result.addAll(generateKits(2, soFar.copy(armor = it))) }
          }
          2 -> {
            result.addAll(generateKits(3, soFar))
            Item.RINGS.forEach { result.addAll(generateKits(3, soFar.copy(ring1 = it))) }
          }
          3 -> {
            result.addAll(generateKits(4, soFar))
            Item.RINGS.forEach {
              if (soFar.ring1 == it) return@forEach
              result.addAll(generateKits(4, soFar.copy(ring2 = it)))
            }
          }
          4 -> result.add(soFar)
          else -> throw IllegalArgumentException()
        }
        return result
      }
    }
  }
}