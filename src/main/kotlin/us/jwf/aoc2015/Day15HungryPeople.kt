package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 15
 */
class Day15HungryPeople : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val ingredients = input.readLines().map(Ingredient.Companion::parse)
    return getMaxScoringRecipe(100, ingredients, mapOf()).score
  }

  override suspend fun executePart2(input: Reader): Long {
    val ingredients = input.readLines().map(Ingredient.Companion::parse)
    return getMaxScoringRecipe2(100, 500, ingredients, mapOf()).score
  }

  fun getMaxScoringRecipe(tsps: Int, remainingIngredients: List<Ingredient>, recipeSoFar: Recipe): Recipe {
    if (tsps == 0) return recipeSoFar
    if (remainingIngredients.size == 1) {
      return recipeSoFar + mapOf(remainingIngredients.first() to tsps)
    }

    var maxScore = -1L
    var maxRecipe: Recipe? = null
    val ingredient = remainingIngredients.first()
    val newRemaining = remainingIngredients.drop(1)
    (0 until (tsps + 1)).forEach {
      val innerBest =
        getMaxScoringRecipe(tsps - it, newRemaining, recipeSoFar + mapOf(ingredient to it))
      val innerBestScore = innerBest.score
      if (innerBestScore > maxScore) {
        maxScore = innerBestScore
        maxRecipe = innerBest
      }
    }
    return maxRecipe!!
  }

  fun getMaxScoringRecipe2(
    tsps: Int,
    calories: Int,
    remainingIngredients: List<Ingredient>,
    recipeSoFar: Recipe
  ): Recipe {
    if (tsps == 0) {
      return if (calories == 0) recipeSoFar else mapOf()
    }
    if (remainingIngredients.size == 1) {
      val first = remainingIngredients.first()
      return if (calories - tsps * first.properties.calories == 0) {
        recipeSoFar + mapOf(first to tsps)
      } else emptyMap()
    }

    var maxScore = -1L
    var maxRecipe: Recipe? = null
    val ingredient = remainingIngredients.first()
    val newRemaining = remainingIngredients.drop(1)
    (0 until (tsps + 1)).forEach {
      val innerBest =
        getMaxScoringRecipe2(
          tsps = tsps - it,
          calories = calories - ingredient.properties.calories * it,
          remainingIngredients = newRemaining,
          recipeSoFar = recipeSoFar + mapOf(ingredient to it)
        )
      val innerBestScore = innerBest.score
      if (innerBestScore > maxScore) {
        maxScore = innerBestScore
        maxRecipe = innerBest
      }
    }
    return maxRecipe!!
  }

  data class Ingredient(
    val name: String,
    val properties: Properties,
  ) {
    companion object {
      val PATTERN = "([a-zA-Z]+): capacity (-?[0-9]+), durability (-?[0-9]+), flavor (-?[0-9]+), texture (-?[0-9]+), calories (-?[0-9]+)".toRegex()

      fun parse(line: String): Ingredient {
        val match = PATTERN.matchEntire(line)!!.groupValues
        return Ingredient(
          name = match[1],
          properties = Properties(
            capacity = match[2].toInt(),
            durability = match[3].toInt(),
            flavor = match[4].toInt(),
            texture = match[5].toInt(),
            calories = match[6].toInt()
          )
        )
      }
    }
  }

  data class Properties(
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
  ) {
    operator fun times(count: Int): Properties {
      return copy(
        capacity = capacity * count,
        durability = durability * count,
        flavor = flavor * count,
        texture = texture * count,
        calories = calories * count,
      )
    }

    operator fun plus(other: Properties): Properties {
      return copy(
        capacity = capacity + other.capacity,
        durability = durability + other.durability,
        flavor = flavor + other.flavor,
        texture = texture + other.texture,
        calories = calories + other.calories,
      )
    }

    val score: Long
      get() {
        return maxOf(0, capacity.toLong()) *
          maxOf(0, durability) *
          maxOf(0, flavor) *
          maxOf(0, texture)
      }
  }

  val Recipe.score: Long
    get() {
      if (isEmpty()) return 0
      return map { (ingredient, count) -> ingredient.properties * count }
        .reduce { acc, properties -> acc + properties }
        .score
    }
}

typealias Recipe = Map<Day15HungryPeople.Ingredient, Int>
