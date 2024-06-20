@file:OptIn(ExperimentalTypeInference::class)

package me.lucko.helper.text3

import com.google.common.collect.ListMultimap
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import kotlin.experimental.ExperimentalTypeInference

/**
 * Used to fast build an i18n lore based on [TranslatableComponent].
 */
class I18nLore
private constructor() {
    private val format: ArrayList<String> = ArrayList()
    private val arguments: ListMultimap<String, TranslatableComponent.Builder> = ListMultimapBuilder
        .hashKeys()
        .arrayListValues()
        .build()
    private val sanitizers: HashMap<String, () -> Boolean> = HashMap()

    companion object Factory {
        /**
         * Creates an instance of [I18nLore].
         *
         * @return a new instance of [I18nLore]
         */
        fun create(): I18nLore {
            return I18nLore()
        }
    }

    /**
     * Appends a translation key to the **lore format**.
     *
     * The lore format is substantially a list of string, each of which will be
     * used as a translation key to create [TranslatableComponent].
     *
     * The following is an example lore format in YAML file:
     * ```yaml
     * lore:
     * - menu.enchant.icon.description
     * - menu.enchant.icon.empty
     * - menu.enchant.icon.rarity
     * - menu.enchant.icon.target
     * - menu.enchant.icon.level
     * - menu.enchant.icon.empty
     * - menu.enchant.icon.conflict.title
     * - menu.enchant.icon.conflict.item
     * - menu.enchant.icon.charging.title
     * - menu.enchant.icon.charging.fuel_item
     * - menu.enchant.icon.charging.consume_amount
     * - menu.enchant.icon.charging.recharge_amount
     * - menu.enchant.icon.charging.max_amount
     * - menu.enchant.icon.obtaining.title
     * - menu.enchant.icon.obtaining.enchanting
     * - menu.enchant.icon.obtaining.villager
     * - menu.enchant.icon.obtaining.loot_generation
     * - menu.enchant.icon.obtaining.fishing
     * - menu.enchant.icon.obtaining.mob_spawning
     * - menu.enchant.icon.empty
     * - menu.enchant.icon.preview
     * ```
     *
     * Calling this function is just like appending the [key] to the list.
     *
     * @param key the translation key
     */
    fun append(key: String): I18nLore {
        format.add(key)
        return this
    }

    /**
     * The same as [append] but for multiple ones.
     *
     * @param keys the translation keys
     */
    fun append(keys: Iterable<String>): I18nLore {
        keys.forEach { key -> append(key) }
        return this
    }

    /**
     * Sets the translation arguments for the component specified by [key].
     *
     * Non-Component arguments can be wrapped in TranslationArgument, or
     * represented with a TranslationArgumentLike.
     *
     * @param key the translation key
     * @param args the translation arguments
     */
    @JvmName("argumentsListComponent")
    fun arguments(key: String, args: List<ComponentLike>): I18nLore {
        val list = listOf(Component.translatable().key(key).arguments(args)) // singleton list
        arguments.replaceValues(key, list)
        return this
    }

    @JvmName("argumentsVarargComponent")
    fun arguments(key: String, vararg args: ComponentLike) = arguments(key, args.toList())

    @JvmName("argumentsListComponentGetter")
    @OverloadResolutionByLambdaReturnType // WTF ???
    inline fun arguments(key: String, args: () -> List<ComponentLike>) = arguments(key, args())

    @JvmName("argumentsComponentGetter")
    @OverloadResolutionByLambdaReturnType
    inline fun arguments(key: String, args: () -> ComponentLike) = arguments(key, args())

    ////// MiniMessage String as Arguments //////

    @JvmName("argumentsListString")
    fun arguments(key: String, args: List<String>) = arguments(key, args.map(kotlin.String::mini))

    @JvmName("argumentsVarargString")
    fun arguments(key: String, vararg args: String) = arguments(key, args.toList())

    @JvmName("argumentsListStringGetter")
    @OverloadResolutionByLambdaReturnType
    inline fun arguments(key: String, args: () -> List<String>) = arguments(key, args())

    @JvmName("argumentsStringGetter")
    @OverloadResolutionByLambdaReturnType
    inline fun arguments(key: String, args: () -> String) = arguments(key, args())

    ////// MiniMessage String as Arguments //////

    /**
     * Sets the translation arguments for the component specified by [key].
     *
     * Non-Component arguments can be wrapped in TranslationArgument, or
     * represented with a TranslationArgumentLike.
     *
     * For each item in [items], this function will set the item as the
     * **first** translation argument for the component specified by [key],
     * ultimately constructing a [List]<[TranslatableComponent]> of the same
     * length as [items].
     *
     * For example, if the [items] has **three** [ComponentLike]s, then in the
     * constructed [List]<[TranslatableComponent]>，the same [key] will map to
     * **three** [TranslatableComponent]s. More specifically:
     * - The `{0}` argument of the 1st component will be [items].get(0)
     * - The `{0}` argument of the 2nd component will be [items].get(1)
     * - The `{0}` argument of the 3rd component will be [items].get(2)
     *
     * While the final lore is being built, the **three** components will span
     * the lore, starting at the first occurrence of [key].
     *
     * @param key the translation key
     * @param items
     */
    @JvmName("argumentsManyListComponent")
    fun argumentsMany(key: String, items: List<ComponentLike>): I18nLore {
        val list = items.map { Component.translatable().key(key).arguments(it) }
        arguments.replaceValues(key, list)
        return this
    }

    @JvmName("argumentsManyListComponentGetter")
    @OverloadResolutionByLambdaReturnType
    inline fun argumentsMany(key: String, items: () -> List<ComponentLike>) = argumentsMany(key, items())


    ////// MiniMessage String as Arguments //////

    @JvmName("argumentsManyListString")
    fun argumentsMany(key: String, items: List<String>) = argumentsMany(key, items.map(String::mini))

    @JvmName("argumentsManyListStringGetter")
    @OverloadResolutionByLambdaReturnType
    inline fun argumentsMany(key: String, items: () -> List<String>) = argumentsMany(key, items())

    ////// MiniMessage String as Arguments //////

    /**
     * Register a cleanup for the final lore construction.
     *
     * If [predicate] returns `true`, lines containing the [pattern] will be
     * removed in the final lore.
     *
     * @param pattern the pattern for which string to be removed
     * @param predicate the prerequisite for the cleanup to execute
     */
    fun sanitize(pattern: String, predicate: () -> Boolean): I18nLore {
        sanitizers[pattern] = predicate
        return this
    }

    /**
     * Build the final i18n lore.
     *
     * @return the final i18n lore
     */
    fun build(): List<TranslatableComponent> {
        // copy
        val sanitized = format.toMutableList()

        // sanitize lore
        for ((pattern, predicate) in sanitizers.entries) {
            if (predicate.invoke()) {
                sanitized.removeIf {
                    it.contains(pattern)
                }
            }
        }

        // generate the final lore
        val ret = sanitized
            .flatMap { key ->
                arguments[key].takeIf { it.isNotEmpty() } ?: listOf(Component.translatable().key(key))
            }.map {
                it.build()
            }
        return ret
    }
}
