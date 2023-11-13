##  Задача

1. Создать любой gradle проект.
2. Проект должен быть совместим с java 17.
3. Придерживаться GitFlow: master -> develop -> feature/fix.
4. Разработать библиотеку, которая будет формировать на основе 
   Java класса json и обратно.
5. Использовать рефлексию.
6. Предусмотреть возможную вложенность объектов (рекурсия).
7. Покрыть код unit tests (можно использовать jackson/gson).
8. Использовать lombok. 
 
###   Объект который сериализуется имеет вид :
   TrainDto[trainNumber=9502ПВЭ, trainIndex=1550-287-1500, 
   locomotive=LocomotiveDto[model=2ТЭ10У, locomotiveNumber=0234, typeLocomotive=Грузовой, documents=[Книга машиниста, Справка о мед.осмотре]], 
   trainLineUp=TrainLineUpDto[wagons={
   24506345=WagonDto[wagonNumber=24506345, loadCapacity=63, yearOfConstruction=1999, cargo=CargoDto[invoiceNumber=SD563456JK, cargoName=combine harvester, weight=33.1, transportationCost=2400]], 
   91750059=WagonDto[wagonNumber=91750059, loadCapacity=64, yearOfConstruction=2015, cargo=CargoDto[invoiceNumber=B123456JK, cargoName=sea container, weight=23.5, transportationCost=1200]]}], 
    departureTime=2022-12-31T13:00]
### Полученный объект вида :
{"trainNumber":"9502ПВЭ",
"trainIndex":"1550-287-1500",
"locomotive":{"model":"2ТЭ10У",
"locomotiveNumber":"0234",
"typeLocomotive":"Грузовой",
"documents":["Книга машиниста", "Справка о мед.осмотре"]},
"trainLineUp":{"wagons":{"24506345":{"wagonNumber":24506345,
"loadCapacity":63,
"yearOfConstruction":1999,
"cargo":{"invoiceNumber":"SD563456JK",
"cargoName":"combine harvester",
"weight":33.1,
"transportationCost":2400}},"91750059":{"wagonNumber":91750059,
"loadCapacity":64,
"yearOfConstruction":2015,
"cargo":{"invoiceNumber":"B123456JK",
"cargoName":"sea container",
"weight":23.5,
"transportationCost":1200}}}},
"departureTime":"2022-12-31T13:00"}

### После десериализации :

Train(uuid=null, trainNumber="9502ПВЭ", trainIndex="1550-287-1500", 
locomotive=Locomotive(uuid=null, model="2ТЭ10У", locomotiveNumber="0234", typeLocomotive="Грузовой", documents=[Книга машиниста, Справка о мед.осмотре]), 
trainLineUp=TrainLineUp(wagons={
24506345=={loadCapacity=63, yearOfConstruction=1999, wagonNumber=24506345, cargo={cargoName="combine harvester", invoiceNumber="SD563456JK", weight=33.1, transportationCost=2400}}, 
91750059=={loadCapacity=64, yearOfConstruction=2015, wagonNumber=91750059, cargo={cargoName="sea container", invoiceNumber="B123456JK", weight=23.5, transportationCost=1200}}}), 
departureTime=2022-12-31T13:00)