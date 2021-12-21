package us.jwf.aoc2015

import java.io.Reader
import java.util.LinkedList
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 22
 */
class Day22WizardSimulator : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val game = Game(
      player = Wizard(hitPoints = 50, mana = 500),
      boss = Boss(hitPoints = 71, damage = 10)
    )

    val queue = LinkedList<Game>().apply { offer(game) }

    val winningGames = mutableListOf<Game>()
    val losingGames = mutableListOf<Game>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()
      if (next.playerWins()) {
        winningGames.add(next)
      } else if (next.bossWins()) {
        losingGames.add(next)
      } else {
        next.nextTurns().forEach {
          queue.offer(it)
        }
      }
    }

    return winningGames.minOf { it.player.manaSpent }
  }

  override suspend fun executePart2(input: Reader): Int {
    val game = Game(
      player = Wizard(hitPoints = 50, mana = 500),
      boss = Boss(hitPoints = 71, damage = 10)
    )

    val queue = LinkedList<Game>().apply { offer(game) }

    val winningGames = mutableListOf<Game>()
    val losingGames = mutableListOf<Game>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()
      if (next.playerWins()) {
        winningGames.add(next)
      } else if (next.bossWins()) {
        losingGames.add(next)
      } else {
        next.nextTurns(hardMode = true).forEach {
          queue.offer(it)
        }
      }
    }

    return winningGames.minOf { it.player.manaSpent }
  }

  data class Game(
    val player: Wizard = Wizard(),
    val boss: Boss = Boss(),
    val history: List<String> = emptyList(),
    val turn: Int = 0,
  ) : Comparable<Game> {

    fun playerWins(): Boolean =
      player.hitPoints > 0 && boss.hitPoints <= 0
    fun bossWins(): Boolean =
      player.hitPoints <= 0 && boss.hitPoints > 0

    override fun compareTo(other: Game): Int = boss.hitPoints - other.boss.hitPoints

    fun nextTurns(hardMode: Boolean = false): Set<Game> {
      var nextPlayer: Wizard = player
      var nextBoss: Boss = boss

      // Run the effects.
      val effects = player.effects
      effects.forEach {
        val (ePlayer, eBoss) = it.onTurn(nextPlayer, nextBoss)
        nextPlayer = ePlayer
        nextBoss = eBoss
      }

      if (nextBoss.hitPoints <= 0 || nextPlayer.hitPoints <= 0) {
        return setOf(Game(nextPlayer, nextBoss, history, turn + 1))
      }

      val nextTurns = mutableSetOf<Game>()
      if (turn % 2 == 1) {
        // boss's turn
        nextPlayer = nextPlayer.copy(hitPoints = nextPlayer.hitPoints - maxOf(nextBoss.damage - nextPlayer.armor, 1))
        nextTurns.add(
          Game(nextPlayer, nextBoss, history + "Attack", turn + 1)
        )
      } else {
        if (hardMode) {
          nextPlayer = nextPlayer.copy(hitPoints = nextPlayer.hitPoints - 1)
        }
        if (nextPlayer.mana < 53 || nextPlayer.hitPoints <= 0) {
          return setOf()
        }
        // player's turn
        Spell.castable(nextPlayer).forEach { spell ->
          val (spellPlayer, spellBoss) = spell.cast(nextPlayer, nextBoss)
          nextTurns.add(
            Game(spellPlayer, spellBoss, history + spell.toString(), turn + 1)
          )
        }
      }
      return nextTurns
    }
  }

  data class Boss(val hitPoints: Int = 14, val damage: Int = 8)
  data class Wizard(
    val hitPoints: Int = 10,
    val armor: Int = 0,
    val mana: Int = 250,
    val manaSpent: Int = 0,
    val effects: Set<Effect> = emptySet()
  )

  sealed class Spell {
    abstract fun canCast(player: Wizard): Boolean
    abstract fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss>

    object MagicMissile : Spell() {
      override fun canCast(player: Wizard): Boolean = player.mana >= 53

      override fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        //println("Player casts Magic Missile, dealing 4 damage")
        return player.copy(mana = player.mana - 53, manaSpent = player.manaSpent + 53) to
          boss.copy(hitPoints = boss.hitPoints - 4)
      }

      override fun toString(): String = "MagicMissile"
    }

    object Drain : Spell() {
      override fun canCast(player: Wizard): Boolean = player.mana >= 73

      override fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        //println("Player casts Drain, dealing 2 damage, and healing 2 hit points")
        return player.copy(
          mana = player.mana - 73,
          hitPoints = player.hitPoints + 2,
          manaSpent = player.manaSpent + 73
        ) to boss.copy(hitPoints = boss.hitPoints - 2)
      }

      override fun toString(): String = "Drain"
    }

    object Shield : Spell() {
      override fun canCast(player: Wizard): Boolean =
        player.mana >= 113 && player.effects.none { it is Effect.Shield }

      override fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val shieldEffect = Effect.Shield()
        return shieldEffect.onCast(
          player.copy(mana = player.mana - 113, manaSpent = player.manaSpent + 113),
          boss
        )
      }

      override fun toString(): String = "Shield"
    }

    object Poison : Spell() {
      override fun canCast(player: Wizard): Boolean =
        player.mana >= 173 && player.effects.none { it is Effect.Poison }

      override fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val poisonEffect = Effect.Poison()
        return poisonEffect.onCast(
          player.copy(mana = player.mana - 173, manaSpent = player.manaSpent + 173),
          boss
        )
      }

      override fun toString(): String = "Poison"
    }

    object Recharge : Spell() {
      override fun canCast(player: Wizard): Boolean =
        player.mana >= 229 && player.effects.none { it is Effect.Recharge }

      override fun cast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val rechargeEffect = Effect.Recharge()
        return rechargeEffect.onCast(
          player.copy(mana = player.mana - 229, manaSpent = player.manaSpent + 229),
          boss
        )
      }

      override fun toString(): String = "Recharge"
    }

    companion object {
      val ALL_SPELLS = listOf(MagicMissile, Drain, Shield, Poison, Recharge)

      fun castable(player: Wizard): List<Spell> = ALL_SPELLS.filter { it.canCast(player) }
    }
  }

  sealed class Effect {
    abstract val turnsLeft: Int
    abstract fun onCast(player: Wizard, boss: Boss): Pair<Wizard, Boss>
    abstract fun onTurn(player: Wizard, boss: Boss): Pair<Wizard, Boss>

    data class Shield(override val turnsLeft: Int = START_TURNS) : Effect() {
      override fun onCast(player: Wizard, boss: Boss): Pair<Wizard, Boss> =
        player.copy(armor = player.armor + 7, effects = player.effects + this) to boss

      override fun onTurn(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val newMe = Shield(turnsLeft - 1)
        if (newMe.turnsLeft == 0) {
          return player.copy(armor = player.armor - 7, effects = player.effects - this) to boss
        }
        return player.copy(effects = player.effects - this + newMe) to boss
      }

      companion object {
        const val START_TURNS = 6
      }
    }

    data class Poison(override val turnsLeft: Int = START_TURNS) : Effect() {
      override fun onCast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        return player.copy(effects = player.effects + this) to boss
      }

      override fun onTurn(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val newMe = Poison(turnsLeft - 1)
        if (newMe.turnsLeft == 0) {
          return player.copy(effects = player.effects - this) to boss.copy(hitPoints = boss.hitPoints - 3)
        }
        return player.copy(effects = player.effects - this + newMe) to boss.copy(hitPoints = boss.hitPoints - 3)
      }

      companion object {
        const val START_TURNS = 6
      }
    }

    data class Recharge(override val turnsLeft: Int = START_TURNS) : Effect() {
      override fun onCast(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        return player.copy(effects = player.effects + this) to boss
      }

      override fun onTurn(player: Wizard, boss: Boss): Pair<Wizard, Boss> {
        val newMe = Recharge(turnsLeft - 1)
        //println("Recharge returns 101 mana; its timer is now $turnsLeft")
        if (newMe.turnsLeft == 0) {
          //println("Recharge wears off")
          return player.copy(mana = player.mana + 101, effects = player.effects - this) to boss
        }
        return player.copy(mana = player.mana + 101, effects = player.effects - this + newMe) to boss
      }

      companion object {
        const val START_TURNS = 5
      }
    }
  }
}