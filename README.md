# Практические работы по ООП на Java

Учебный Java-проект, объединяющий две предметные области: управление складом и оформление заказов с доставкой. На их примере показаны наследование, композиция и несколько классических паттернов проектирования.

## Навигация

- [Лабораторная 1 — склад](#лабораторная-1--склад)
  - [Возможности](#возможности-лабораторной-1)
  - [Архитектура склада](#архитектура-склада)
  - [Фабрики продуктов](#фабрики-продуктов)
- [Лабораторная 2 — заказы и доставка](#лабораторная-2--заказы-и-доставка)
  - [Возможности](#возможности-лабораторной-2)
  - [Архитектура заказов](#архитектура-заказов)
- [Использованные паттерны](#использованные-паттерны)
- [Аналоги конструкций C++ в Java](#аналоги-конструкций-c-в-java)
- [Отчёты по работам](#отчёты-по-работам)
- [Структура проекта](#структура-проекта)
- [Запуск](#запуск)
- [Проверка сообщений коммитов](#проверка-сообщений-коммитов)

---

## Лабораторная 1 — склад

[К оглавлению](#навигация) · [Перейти к лабораторной 2 →](#лабораторная-2--заказы-и-доставка)

Первая лабораторная описывает склад и иерархию товаров: магические артефакты, цифровые продукты и роботов.

### Возможности лабораторной 1

- склад с ограниченной вместимостью;
- добавление, удаление, поиск и перенос товаров между складами;
- обход товаров через стандартный `Iterator<Product>`;
- создание конкретных продуктов через фабрики;
- глубокое копирование склада с клонированием товаров;
- разделение изменяемых данных товара и общего типа `ProductType`.

### Архитектура склада

```mermaid
classDiagram
direction TB

class Warehouse {
    -List~Product~ products
    -int maxSize
    +Warehouse(int maxSize)
    +Warehouse(Warehouse source)
    +addProduct(Product product) void
    +removeById(long id) boolean
    +removeByIndex(int index) Product
    +findById(long id) Product
    +findByCategory(String category) List~Product~
    +findByPrice(double price) List~Product~
    +transferFrom(Warehouse source) void
    +transferProductTo(Product product, Warehouse target) void
    +iterator() Iterator~Product~
}

class IterableProduct {
    <<interface>>
    +iterator() Iterator~Product~
}

class Product {
    <<abstract>>
    -long id
    -String name
    -Price price
    -Quantity quantity
    -ProductType type
    -Warehouse warehouse
    +getWeight() double
    +getCategory() String
    +changeQuantity(int delta) void
    +setPrice(Price price) void
    +transferTo(Warehouse other) void
    +clone() Product
}

class ProductType {
    -String category
    -double baseWeight
    -double basePrice
    +getType(String category, double baseWeight, double basePrice)$ ProductType
    +getTypeCount()$ int
}

class Price {
    -double value
    +getValue() double
    +setValue(double value) void
}

class Quantity {
    -int value
    +getValue() int
    +setValue(int value) void
}

class MagicalArtifact {
    <<abstract>>
}

class DigitalProduct {
    <<abstract>>
}

class RobotProduct {
    <<abstract>>
}

Warehouse ..|> IterableProduct : Iterable~Product~
Warehouse o-- "0..*" Product : хранит
Product *-- Price
Product *-- Quantity
Product --> ProductType : разделяет общий тип
Product --> Warehouse : принадлежит

Product <|-- MagicalArtifact
Product <|-- DigitalProduct
Product <|-- RobotProduct

MagicalArtifact <|-- ChronoBattery
MagicalArtifact <|-- QuantumCore
MagicalArtifact <|-- SoulProcessor
DigitalProduct <|-- CombatFirmware
DigitalProduct <|-- GameLicense
DigitalProduct <|-- MedicalAI
DigitalProduct <|-- NavigationModule
RobotProduct <|-- GuardianRobot
RobotProduct <|-- MedicalRobot
RobotProduct <|-- ScoutDrone
```

`Product` хранит собственные `Price` и `Quantity`, а категория, базовый вес и базовая цена вынесены в неизменяемый `ProductType`. Одинаковые наборы этих значений получают общий экземпляр типа из внутреннего кэша. Метод `getWeight()` читает `baseWeight`, а `getCategory()` — категорию из `ProductType`; отдельных полей `Weight` и `category` в `Product` нет.

`Warehouse` реализует `Iterable<Product>` и возвращает итератор внутреннего `ArrayList`, поэтому склад можно обходить стандартным циклом `for-each`.

### Фабрики продуктов

```mermaid
classDiagram
direction TB

class ProductFactory {
    <<abstract>>
    +createProduct(String name, double price, int quantity, double weight) Product
}

ProductFactory <|-- ChronoBatteryFactory
ProductFactory <|-- QuantumCoreFactory
ProductFactory <|-- SoulProcessorFactory
ProductFactory <|-- CombatFirmwareFactory
ProductFactory <|-- GameLicenseFactory
ProductFactory <|-- MedicalAIFactory
ProductFactory <|-- NavigationModuleFactory
ProductFactory <|-- GuardianRobotFactory
ProductFactory <|-- MedicalRobotFactory
ProductFactory <|-- ScoutDroneFactory

ChronoBatteryFactory ..> ChronoBattery : создаёт
QuantumCoreFactory ..> QuantumCore : создаёт
SoulProcessorFactory ..> SoulProcessor : создаёт
CombatFirmwareFactory ..> CombatFirmware : создаёт
GameLicenseFactory ..> GameLicense : создаёт
MedicalAIFactory ..> MedicalAI : создаёт
NavigationModuleFactory ..> NavigationModule : создаёт
GuardianRobotFactory ..> GuardianRobot : создаёт
MedicalRobotFactory ..> MedicalRobot : создаёт
ScoutDroneFactory ..> ScoutDrone : создаёт
```

Каждая конкретная фабрика создаёт свой подкласс `Product` и хранит его специфическую конфигурацию. Общие параметры товарной позиции передаются в `createProduct(...)`.

---

## Лабораторная 2 — заказы и доставка

[← Вернуться к лабораторной 1](#лабораторная-1--склад) · [К оглавлению](#навигация)

Вторая лабораторная добавляет покупателей, заказы, варианты доставки, скидки и обработку событий изменения статуса заказа.

### Возможности лабораторной 2

- создание заказа из снимков товарных позиций `OrderItem`;
- проверка и списание остатков при создании заказа;
- ограничение количества активных заказов покупателя;
- допустимые переходы между статусами `PENDING`, `PAID`, `SHIPPED`, `DELIVERED` и `CANCELLED`;
- выбор доставки с проверкой доступности и расчётом стоимости;
- процентная скидка, бесплатная доставка, подарок и начисление кешбэка;
- учёт оборота при оплате и возврат товаров на склад при отмене заказа;
- уведомление подписчиков через события `OrderEvent`;
- единое логирование операций с источниками `ORDER`, `DELIVERY`, `DISCOUNT`, `MANAGER`;
- отдельная Swing-вкладка для покупателей, заказов, доставок, скидок и статусов.

### Архитектура заказов

```mermaid
classDiagram
direction TB

class OrderManager {
    -Map~Long, Order~ orders
    -Set~Long~ customerIds
    -int maxActiveOrdersPerCustomer
    -double turnover
    +createOrder(Customer customer, List~OrderItem~ items) Order
    +findById(long orderId) Order
    +getActiveOrderCount(Customer customer) int
    +onOrderEvent(OrderEvent event) void
}

class Order {
    -long id
    -Customer customer
    -List~OrderItem~ items
    -OrderStatus status
    -Delivery delivery
    -List~Discount~ discounts
    -double totalPrice
    +setDelivery(Delivery delivery) void
    +changeStatus(OrderStatus newStatus) void
    +applyDiscount(Discount discount) void
    +getTotalPrice() double
    +getTotalWeight() double
}

class OrderItem {
    -Product sourceProduct
    -long productId
    -String productName
    -double unitPrice
    -double unitWeight
    -int quantity
    +getTotalPrice() double
    +getTotalWeight() double
}

class Customer {
    -long id
    -String name
    -String region
    -double bonusBalance
    +addBonus(double amount) void
}

class Product {
    <<abstract>>
    -long id
    -String name
    -Price price
    -Quantity quantity
    -ProductType type
    +getPrice() double
    +getWeight() double
    +changeQuantity(int delta) void
}

class ProductType {
    -String category
    -double baseWeight
    -double basePrice
    +getType(String category, double baseWeight, double basePrice)$ ProductType
}

class Delivery {
    <<interface>>
    +getCost(Order order) double
    +getEstimatedTime() String
    +isAvailable(Order order) boolean
}

class CourierDelivery
class PickupDelivery
class PostDelivery

class DeliveryProxy {
    -Delivery realDelivery
    -Map~Long, Double~ costCache
    +getCost(Order order) double
    +getEstimatedTime() String
    +isAvailable(Order order) boolean
    +clearCache() void
}

class Discount {
    <<interface>>
    +apply(Order order) void
}

class PercentDiscount
class FreeDeliveryDiscount
class GiftDiscount {
    -Product giftProduct
}
class CashbackDiscount

class Observer {
    <<interface>>
    +onOrderEvent(OrderEvent event) void
}

class Observable {
    <<interface>>
    +subscribe(Observer observer) void
    +unsubscribe(Observer observer) void
    +notifyObservers(OrderEvent event) void
}

class OrderEvent {
    -long orderId
    -OrderStatus oldStatus
    -OrderStatus newStatus
    -Instant timestamp
}

class LoggingProxy {
    +log(Source source, String message)$ void
    +run(Source source, String operation, Runnable action)$ void
    +call(Source source, String operation, Supplier action)$ T
}

class Source {
    <<enumeration>>
    ORDER
    DELIVERY
    DISCOUNT
    MANAGER
}

class OrderStatus {
    <<enumeration>>
    PENDING
    PAID
    SHIPPED
    DELIVERED
    CANCELLED
}

OrderManager ..|> Observer
Order ..|> Observable
OrderManager o-- "0..*" Order : управляет
OrderManager ..> Customer : ограничивает заказы
Order *-- "1..*" OrderItem
Order --> Customer : оформлен для
OrderItem --> Product : снимок и источник
Product --> ProductType : общий тип
Order --> OrderStatus : текущий статус
Order --> "0..1" Delivery : использует
Order o-- "0..*" Discount : применяет

Delivery <|.. CourierDelivery
Delivery <|.. PickupDelivery
Delivery <|.. PostDelivery
Delivery <|.. DeliveryProxy
DeliveryProxy o-- Delivery : делегирует

Discount <|.. PercentDiscount
Discount <|.. FreeDeliveryDiscount
Discount <|.. GiftDiscount
Discount <|.. CashbackDiscount
GiftDiscount --> Product : подарок

Observable ..> Observer : уведомляет
Observable ..> OrderEvent
Observer ..> OrderEvent
Order ..> OrderEvent : создаёт
OrderEvent --> OrderStatus : старый и новый
LoggingProxy --> Source
Order ..> LoggingProxy : ORDER, DISCOUNT
OrderManager ..> LoggingProxy : MANAGER
DeliveryProxy ..> LoggingProxy : DELIVERY
```

`OrderManager` подписывается на каждый созданный `Order`. При переходе заказа в `PAID` менеджер увеличивает оборот, а при переходе в `CANCELLED` восстанавливает количество исходных товаров. `OrderItem` сохраняет цену, вес и имя на момент создания, но также держит ссылку на исходный `Product` для изменения складского остатка.

Варианты доставки реализуют единый интерфейс `Delivery`: курьер и почта ограничивают доступность по весу, а самовывоз доступен всегда. `DeliveryProxy` оборачивает выбранную реализацию, проверяет данные заказа и кэширует стоимость по идентификатору заказа.

---

## Использованные паттерны

| Паттерн | Реализация в проекте |
|---|---|
| **Iterator** | `Warehouse` реализует `Iterable<Product>` и предоставляет стандартный итератор коллекции товаров. |
| **Factory Method** | Абстрактный `ProductFactory` задаёт метод создания, а конкретные фабрики возвращают соответствующие виды продуктов. |
| **Prototype** | Каждый конкретный `Product` реализует `clone()`, а копирующий конструктор `Warehouse` создаёт глубокую копию товаров. |
| **Flyweight** | `ProductType` хранит общие неизменяемые данные типа и переиспользует экземпляры с одинаковыми категорией, базовым весом и базовой ценой. |
| **Strategy** | Реализации `Delivery` взаимозаменяемо определяют стоимость и доступность доставки; реализации `Discount` инкапсулируют разные способы применения скидки. |
| **Proxy** | `DeliveryProxy` реализует тот же интерфейс, делегирует реальной доставке и добавляет проверки и кэш стоимости. `LoggingProxy` централизует журналирование операций разных подсистем. |
| **Observer** | `Order` выступает как `Observable`, создаёт `OrderEvent` при смене статуса и уведомляет `Observer`; `OrderManager` реагирует на событие. |

---

## Аналоги конструкций C++ в Java

Задание сформулировано с примерами на C++, поэтому операции адаптированы к возможностям Java:

| Конструкция из задания | Java-эквивалент в проекте |
|---|---|
| `order + discount` | `order.applyDiscount(discount)` |
| `delivery << order` | `order.assignTo(delivery)` |
| `ostream << order` | переопределённый `order.toString()` |
| `shared_ptr<T>` | обычная ссылка на объект; временем жизни управляет сборщик мусора |
| `const T&` | ссылка без копирования и неизменяемые представления через `Collections.unmodifiableList(...)` |
| чисто виртуальный метод | метод интерфейса или `abstract`-метод |
| деструктор | автоматическая сборка мусора; для внешних ресурсов применяется `AutoCloseable` |

Java не поддерживает пользовательскую перегрузку операторов, поэтому именованные методы делают те же операции явно и читаемо.

## Отчёты по работам

- [Практическая работа №1 — склад](docs/practical-work-1-summary.md)
- [Практическая работа №2 — заказы, доставка и паттерны](docs/practical-work-2-summary.md)

---

## Структура проекта

```text
oop/
├── .githooks/
│   └── commit-msg          # проверка формата сообщений коммитов
├── docs/                   # дополнительная документация
├── src/
│   ├── customer/           # покупатели
│   ├── delivery/           # стратегии доставки и прокси
│   ├── discount/           # стратегии скидок
│   ├── factory/            # фабрики продуктов
│   ├── inventory/          # склад
│   ├── logging/            # централизованное логирование операций
│   ├── observer/           # наблюдатели и события заказов
│   ├── order/              # заказы и управление ими
│   ├── product/            # товары, типы и атрибуты
│   ├── ui/                 # Swing-интерфейс
│   └── Main.java           # точка входа
├── out/                    # результат компиляции
├── run.ps1                 # сборка и запуск в Windows
└── README.md
```

## Запуск

Для запуска необходим JDK: команды `javac` и `java` должны быть доступны через `PATH`.

В Windows откройте PowerShell в корне проекта и выполните:

```powershell
./run.ps1
```

Скрипт рекурсивно компилирует Java-файлы из `src/` в `out/`, а затем запускает класс `Main` со Swing-интерфейсом.

## Проверка сообщений коммитов

Проект использует хук `.githooks/commit-msg`, проверяющий сообщения в формате [Conventional Commits](https://www.conventionalcommits.org/):

```text
<type>(optional-scope): <description>
```

Допустимые типы: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.

Примеры:

```text
feat: add warehouse search
fix(ui): validate product price
refactor(factory)!: change product factory API
```

После клонирования репозитория включите хук один раз:

```bash
git config core.hooksPath .githooks
```

Заголовок коммита ограничен 72 символами. Merge-, revert-, fixup- и squash-коммиты, созданные Git, разрешены автоматически.
