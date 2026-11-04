open class Player{
    // Бюджет
    var money: Int = 100
        private set
    // Энергия
    var energy: Int = 10
        private set
    // Готовность
    var readiness: Int = 0
        private set
    // Текущий день
    var currentDay: Int = 1

    // Параметр для определения порядка выполнения действий
    var counter: String = ""
        private set
    // Параметр количества попыток для действия "Испытать удачу"
    var attempt: Int = 1
    // Функция, возвращающая значение изменения энергии в зависимости от номера дня
    fun energyCost(): Int = if (currentDay % 2 == 0) 3 else 2
    // Метод для изменения параметра counter
    fun counterChange(value: String){
        counter += value
    }
    // Метод для изменения количества денег
    open fun moneyChange(value: Int){
        // Применяем coerceAtLeast(0) чтобы не ушло ниже нуля
        money = (money + value).coerceAtLeast(0)
    }
    // Метод для изменения количества энергии
    open fun energyChange(value: Int){
        energy = (energy + value).coerceAtLeast(0)
    }
    // Метод для изменения готовности
    open fun readinessChange(value: Int){
        readiness += value
    }

    // Переопределяем метод toString для красивого вывода в консоли
    override fun toString(): String {
        return "💰Бюджет: $money, 🔥Энергия: $energy, 🎯Готовность: $readiness"
    }

}
// Абстрактный класс-родитель для событий
abstract class MartianEvent(
    // Параметр описания события
    val description: String,
) {
    // Метод для применения события
    abstract fun apply(player: Player)
}
// Дочерний класс фиксированных событий
class FixedEvent(
    description: String,
    // Параметры, показывающие, на какое значение менять соответствующие параметры
    val moneyChange: Int,
    val energyChange: Int,
    val readinessChange: Int) : MartianEvent(description) {
    override fun apply(player: Player) {
        println("═".repeat(75))
        println(description)
        println("Последствия:")
        println("💰Бюджет $moneyChange")
        println("🔥Энергия $energyChange")
        println("🎯Готовность $readinessChange")
        player.moneyChange(moneyChange)
        player.energyChange(energyChange)
        player.readinessChange(readinessChange)
    }
}
// Дочерний класс условных событий
class ConditionalEvent(
    description: String,
) : MartianEvent(description) {

    override fun apply(player: Player) {

        // when для того, чтобы решить, что будем выполнять в зависимости от события
        when {
            "буря" in description -> {
                // Если энергия игрока больше или равна 5, то...
                if (player.energy >= 5) {
                    player.energyChange(-3)
                    // Проверка, что применение события удовлетворяет условиям игры
                    eventComplianceWithTheRules(player)
                    // Если всё ок, выводим текст (ресурсы меняются в самой функции actionComplianceWithTheRules())
                    if (flag){
                        println("Вы держите оборону в скафандре!")
                        println("Энергия -5")
                    }
                    // Это выполняем, если энергия меньше 5
                } else {
                    println("Вы теряете сознание, ульи повреждены!")
                    println("Готовность -20")
                    player.readinessChange(-20)
                }
            }

            "датчик" in description -> {
                // Выполняем, если значение бюджета больше или равно 30
                if (player.money >= 30) {
                    println("Вы покупаете премиум-датчики!")
                    println("Готовность +10")
                    println("Бюджет -30")
                    player.readinessChange(10)
                    player.moneyChange(-30)
                }
                // Выполняем, если бюджет меньше 30
                else {
                    println("Приходится использовать старые, а они врут")
                    println("Готовность -5")
                    player.readinessChange(-5)
                }
            }

            "адаптация" in description -> {
                // Выполняем, если готовность больше или равна 60
                if (player.readiness >= 60) {
                    println("Пчёлы привыкли к Марсу и работают быстрее!")
                    println("Готовность +10")
                    player.readinessChange(10)
                }
                // Выполняем, если готовность меньше 60
                else {
                    player.energyChange(-3)
                    // Проверка, что применение события удовлетворяет условиям игры
                    eventComplianceWithTheRules(player)
                    // Если всё ок, выводим текст
                    if (flag) {
                        println("Пчёлы в стрессе от новой среды - пришлось их успокаивать")
                        println("Энергия -3")
                    }
                }
            }

            "грант" in description -> {
                // Выполняем, если бюджет меньше или равен 20
                if (player.money <= 20) {
                    println("Земля видит, что вы на грани, и выделяет помощь!")
                    println("Бюджет +25")
                }
                // Выполняем, если бюджет больше 20
                else {
                    println("Земля считает, что вы справляетесь")
                    println("Эффект 0")
                }
            }
        }
    }
}

// Дата-класс для хранения данных об изменении ресурсов по истечении каждого дня
data class RecordDay(
    val dayNumber: Int,
    val moneyChange: Int,
    val energyChange: Int,
    val readinessChange: Int,
)

// Функция, проверяющая, что будущее применение действия не приведет к завершению игры
// В роли аргументов передаем объект player (игрок) и значения, на которые хотим изменить соответствующие параметры
fun actionComplianceWithTheRules(player: Player,
                           counterChange: String,
                           moneyChange: Int,
                           energyChange: Int,
                           readinessChange: Int) {
    // Локальная переменная для проверки
    val localCounter = player.counter + counterChange
    // Выполняем, если верно одно из условий:
    // 1. В counter есть хотя бы одна цифра, которая повторяется три раза подряд
    // 2. Энергия уменьшится до нуля
    if (localCounter.toSet().any { it.toString().repeat(3) in localCounter } ||
        player.energy + energyChange < 1) {
        println("═".repeat(75))
        println(
            """"!!! ВЫ ПОДТВЕРЖДАЕТЕ СВОЙ ВЫБОР? ПРИ ВЫПОЛНЕНИИ ЭТОГО ДЕЙСТВИЯ ИГРА ЗАКОНЧИТСЯ !!!
                    Введите:
                    ДА
                    НЕТ""".trimIndent()
        )
        println("═".repeat(75))
        // Обращаемся к функии, которая обрабатывает ответ игрока
        confirmChoice(player, counterChange, moneyChange, energyChange, readinessChange)
        }
        // Выполняем, если изменение параметров не влечет за собой завершение игры
        else {
        player.counterChange(counterChange)
        player.moneyChange(moneyChange)
        player.energyChange(energyChange)
        player.readinessChange(readinessChange)
    }
}

// Функция, проверяющая, что после применения события значения ресурсов не противоречат правилам игры
// В роли аргументов передаем объект player (игрок)
fun eventComplianceWithTheRules(player: Player) {
    if (player.energy < 1) {
        end(player)
    }
}

// Функция для обработки ответа игрока при подтверждении выбора действия
fun confirmChoice(player: Player,
                  counterChange: String,
                  moneyChange: Int,
                  energyChange: Int,
                  readinessChange: Int) {
    // Сохраняем ответ игрока в confirmationChoice
    val confirmationChoice = readlnOrNull()?.uppercase()
    // Выполняем, если ответ соответствует "да" или "нет"
    if (confirmationChoice in listOf("ДА", "НЕТ")) {
        // Если ответ "да", то...
        if (confirmationChoice == "ДА") {
            // Применяем изменения
            player.counterChange(counterChange)
            player.moneyChange(moneyChange)
            player.energyChange(energyChange)
            player.readinessChange(readinessChange)
            // Обращаемся к функции завершения игры
            end(player)
        }
        // Если ответ "нет", возвращаемся к выбору действия
        else if (confirmationChoice == "НЕТ") {
            action(player)
        }
    }
    // // Обращаемся к этой же функции в ожидании ответа "да" или "нет"
    else {
        println("""ВВЕДИТЕ:
                   ДА
                   НЕТ
        """.trimIndent())
        confirmChoice(player, counterChange, moneyChange, energyChange, readinessChange)
    }
}

// Функция блока Действие
fun action(player: Player){
    println("═".repeat(75))
    println("• Выберите действие:")
    println("1. 🐝Обслужить ульи (-${player.energyCost()} энергии, +20 готовности)")
    println("2. 💊Заказать витаминно-экологические стимуляторы (-20 денег, +20 готовности)")
    println("3. 💤Отдохнуть (+7 энергии)")
    // Проверяем, что попытки для выбора действия "Испытать удачу" не исчерпаны
    if (player.attempt == 1) println("4. ☘️Испытать удачу (+30 готовности / -30 готовности)")
    println("═".repeat(75))
    // Сохраняем ответ в choice
    val choice: Int? = readlnOrNull()?.toIntOrNull()
    // Определяем, что за действие выбрал игрок и выполняем соответствующий код
    when (choice) {
        1 -> {
            // Проверка, что применение действия удовлетворяет условиям игры
            actionComplianceWithTheRules(player,
                "1",
                0,
                player.energyCost(),
                20)
            // Выполняем, если всё ок
            if (flag) {
                println("Улья обслужены!")
            }
        }
        2 -> {
            actionComplianceWithTheRules(player,
                "2",
                -20,
                0,
                20)
            if (flag) {
                println("ВЭС заказаны!")
            }
        }
        3 -> {
            actionComplianceWithTheRules(player,
                "3",
                0,
                7,
                0,)
            if (flag) {
                println("Вы отдохнули!")
            }
        }
        4 -> {
            // Выполняем, если у игрока есть попытки для использования "Испытать удачу"
            if (player.attempt == 1) {
                // Рандомный выбор значения, на которое изменится готовность
                val luck = listOf(-30, 30).random()
                player.readinessChange(luck)
                player.counterChange("4")
                // Обнуляем количество попыток
                player.attempt = 0
                if (luck > 0) println("Вам невероятно повезло!\n🎯Готовность +30") else println("Очень жаль, конечно, но...\n🎯Готовность -30")
            }
            // Выполняем, если попытки кончились
            else {
                println(""""Вы исчерпали свои попытки на использование действия "Испытать удачу"""")
            }
        }
        // Выполняем, если ответ не соответствует "1", "2", "3" или "4"
        else -> {
            // ${player.attempt + 3} выводит значение 3 или 4 в зависимости от количества попыток
            println("Ошибка ввода. Выберите число 1 - ${player.attempt + 3}")
            // Обращаемся к этой же функции в ожидании ответа "1", "2", "3" или "4"
            action(player)
        }
    }
}

// Функция завершения игры
fun end(player: Player){
    flag = false
    // Выполняем, если игрок выполнил одно действие три раза подряд
    if (player.counter.toSet().any { it.toString().repeat(3) in player.counter }) {
        println("""
        ⛔ НАРУШЕНИЕ ПРОТОКОЛА!
        Вы выполнили одно действие слишком много раз подряд.
        Рутинная работа привела к выгоранию и ошибкам в уходе за пасекой.
        Колонисты не доверяют такому специалисту. Миссия провалена!
    """.trimIndent())
    }
    // Выполняем, если по завершении игры готовность меньше 100
    else if (player.readiness < 100) {
        println("""
        📉 МИССИЯ НЕ ВЫПОЛНЕНА
        Срок экспедиции истёк, а пасека так и не достигла нужного уровня готовности.
        Без опыления марсианская флора не сможет развиваться.
        Колонизация откладывается на неопределённый срок...
    """.trimIndent())
    }
    // Выполняем, если у пользователя в ходе игры кончилась энергия
    else if (player.energy < 1) {
        println("""
        💀 ЭНЕРГИЯ ИСТОЩЕНА
        Вы работали на пределе возможностей, но силы закончились раньше времени.
        Без вашего контроля системы жизнеобеспечения вышли из строя.
        Пасека погибла... 
        Колонизация откладывается на неопределённый срок...
    """.trimIndent())
    }
    // Выполняем, если игрок не нарушил правила, энергия больше нуля и готовность больше или равна 100
    else {
        println("""
        🏆 ПОБЕДА! МАРС ОЖИЛ!
        К моменту прибытия колонистов пасека полностью готова к работе!
        Первые марсианские ульи дают мёд, а растения начинают опыляться.
        Благодаря вашему труду красная планета делает первый шаг к зелёному будущему.
        Вы — герой новой эры освоения космоса!
    """.trimIndent())
    }
}

// Переменная, значение которой равно false, если была вызвана функция end()
var flag = true

fun main() {
    println("""
            
    🚀 2163 год. Развитие технологий привело к началу освоения Марса.
    Одним из первых этапов является развитие флоры на планете.
    А значит, кого нужно вывести? Правильно — пчёл!

    Вам, как учёному-пчеловоду, поручено подготовить их к прибытию колонистов,
    чтобы они могли заниматься последующим развитием экосистемы Марса.

    ${"═".repeat(75)}
    ХОД ИГРЫ:
    1. В начале каждого дня выберите одно действие.
    2. После действия происходит случайное событие (может помочь или навредить).
    3. События бывают фиксированными и условными (зависят от ваших текущих ресурсов).

    ⚠️ ОСОБЕННОСТЬ МАРСА:
    Из-за резких перепадов температур каждый ЧЁТНЫЙ день обслуживание ульев
    требует ПОВЫШЕННЫХ затрат энергии. Планируйте отдых заранее!

    ПРАВИЛА ИГРЫ:
    Начальные ресурсы:
      • Бюджет: 100
      • Энергия: 10
      • Готовность: 0
    
    Доступные действия:
      1. Обслужить ульи
      2. Заказать витаминно-экологические стимуляторы
      3. Отдохнуть
      4. Испытать удачу (доступно только 1 раз за игру!)
    
    ⛔ ИГРА ЗАВЕРШАЕТСЯ, ЕСЛИ:
      1. Вы выполнили одно и то же действие более 2 раз подряд.
      2. Ваша энергия упала до 0.
      3. Прошло 7 дней.

    🎯 ЦЕЛЬ: Достичь значения «Готовность» ≥ 100 к концу 7-го дня.
    ${"═".repeat(75)}
    
""".trimIndent())
    println("Нажмите Enter для начала игры")
    readlnOrNull()

    // Список, хранящий информацию об изменении ресурсов по истечении каждого дня
    val history = mutableListOf<RecordDay>()
    // Создаём объект класса Player (пользователя)
    val player = Player()
    // Цикл выполняющийся при условии, что игрок не дошел до 8 дня и flag == true
    while (player.currentDay < 8 && flag) {
        // Переменные, которые сохраняют все изменения ресурсов в течение дня
        val currentDay = player.currentDay
        var moneyChange = player.money
        var energyChange = player.energy
        var readinessChange = player.readiness

        println("День: $currentDay")
        action(player)

        if (flag) {
            // Список всех возможных событий
            val allEvents = mutableListOf(
                FixedEvent("Микрометеорит пробил защитный купол - срочный ремонт ",
                    -15,
                    -2,
                    0),
                FixedEvent(
                    "Колония решила роиться досрочно! Рой вырвался из улья и кружит в теплице. Пришлось ловить их вручную — это заняло много сил",
                    0,
                    -3,
                    0
                ),
                FixedEvent("Система подачи воды дала сбой",
                    0,
                    0,
                    -10),
                FixedEvent("Вы нашли старую земную банку мёда в подсобке - продали на чёрном рынке станции",
                    +15,
                    0,
                    0),

                ConditionalEvent("Марсианская пылевая буря"),
                ConditionalEvent("Проверка датчиков влажности"),
                ConditionalEvent("Генетическая адаптация"),
                ConditionalEvent("Экстренный грант с Земли"),
            )
            // Переменная которой присваивается рандомное событие
            val event = allEvents.random()
            // Применяем событие
            event.apply(player)
            // Удаляем выпавшее событие из списка, чтобы не повторялось
            allEvents.remove(event)
            // Считаем разницу значений ресурсов в конце и начале дня
            moneyChange = player.money - moneyChange
            energyChange = player.energy - energyChange
            readinessChange = player.readiness - readinessChange
            // Добавляем в историю статистику текущего дня
            history.add(
                RecordDay(
                    dayNumber = currentDay,
                    moneyChange = moneyChange,
                    energyChange = energyChange,
                    readinessChange = readinessChange
                )
            )
            player.currentDay += 1
            println("═".repeat(75))
            println(player)
            println()
        }

        else {
            break
        }
    }
    end(player)
    // Переменные для вывода статистики:
    // День с максимальной прибавкой готовности
    val bestDay = history.maxByOrNull { it.readinessChange}
    // День, когда готовность убавилась сильнее всего
    val worstDay = history.minByOrNull { it.readinessChange}
    // Общие затраты бюджета
    val totalMoney = history.sumOf { it.moneyChange}
    // Общие затраты энергии
    val totalEnergy = history.sumOf {it.energyChange}
    println()
    print("Нажмите Enter, чтобы увидеть итоговую статистику...")
    readlnOrNull()
    println("═".repeat(75))
    println("Статистика за ${history.size} из 7 дней:")
    println("🏆 Лучший день: День ${bestDay?.dayNumber} — готовность +${bestDay?.readinessChange}")
    println("💀 Худший день: День ${worstDay?.dayNumber} — готовность ${worstDay?.readinessChange}")
    println("💸 Общие затраты бюджета: $totalMoney")
    println("⚡ Общие затраты энергии: $totalEnergy")
}