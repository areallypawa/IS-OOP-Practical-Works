# IS-OOP-Practical-Works

Система управления складом и каталогом товаров на Java. Проект демонстрирует наследование, композицию, глубокое копирование, фабричный метод и обход склада через стандартный Java-итератор.

## Архитектура предметной области

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
    +iterator() Iterator~Product~
}

class Iterable~Product~ {
    <<interface>>
    +iterator() Iterator~Product~
}

class Iterator~Product~ {
    <<interface>>
    +hasNext() boolean
    +next() Product
    +remove() void
}

class Product {
    <<abstract>>
    -long id
    -String name
    -Price price
    -Quantity quantity
    -Weight weight
    -String category
    +changeQuantity(int delta) void
    +setPrice(Price price) void
    +transferTo(Warehouse other) void
    +clone() Product
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

class Weight {
    -double value
    +getValue() double
    +setValue(double value) void
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

Warehouse ..|> Iterable~Product~
Warehouse o-- "0..*" Product : stores
Warehouse --> Iterator~Product~ : provides
Product *-- Price
Product *-- Quantity
Product *-- Weight
Product --> Warehouse : transferTo
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

`Warehouse` реализует `Iterable<Product>` и адаптирует стандартный итератор `ArrayList` через `products.iterator()`. В Java это эквивалент обхода от `begin()` до `end()` в C++.

## Фабричный метод

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

ChronoBatteryFactory ..> ChronoBattery : creates
QuantumCoreFactory ..> QuantumCore : creates
SoulProcessorFactory ..> SoulProcessor : creates
CombatFirmwareFactory ..> CombatFirmware : creates
GameLicenseFactory ..> GameLicense : creates
MedicalAIFactory ..> MedicalAI : creates
NavigationModuleFactory ..> NavigationModule : creates
GuardianRobotFactory ..> GuardianRobot : creates
MedicalRobotFactory ..> MedicalRobot : creates
ScoutDroneFactory ..> ScoutDrone : creates
```

Каждая конкретная фабрика хранит специфическую конфигурацию продукта. Общие параметры товарной позиции (`name`, `price`, `quantity`, `weight`) передаются в `createProduct(...)`, после чего фабрика создаёт соответствующий конкретный `Product`.
