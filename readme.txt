Запуск
java -jar -Dtest=123 -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removePresentationById role=editor id=0150d271-783b-4912-8517-f8c0e87abaaf


1. Создание презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv createPresentation role=editor fillColor=red fontFamily="Comic Sans" name="Моя новая презентация"

2. Получение презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentationById role=editor id=a3cacfb6-ca30-446e-8830-af1b5a95c629

3. Изменение презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv editPresentationOptions role=editor fillColor=green fontFamily="Roboto" name="Моя любимая презентация презентация" id=<id презентации>

4. Удаление презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removePresentationById role=editor id=a3cacfb6-ca30-446e-8830-af1b5a95c629

5. Получение всех презентаций
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentations role=guest



6. Получение слайдов презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentationSlides role=editor presentationId=<presentationId>

7. Создание сдайда презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv createPresentationSlide role=editor presentationId=<presentationId> name=<name>

8. Удаление слайда презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removePresentationSlideById role=editor presentationId=<presentationId> id=<id>

9. Изменение слайда презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv editPresentationSlideById role=editor presentationId=<presentationId> id=<id> name=<name>

10. Получение слайда презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getSlideById role=guest presentationId=<presentationId> id=<id>


11. Получение комментариев презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentationComments role=editor presentationId=<presentationId>

12. Комментрирование презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv commentPresentation role=editor presentationId=<presentationId> text=<text>


13. Изменение комментария презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv editPresentationComment role=editor presentationId=<presentationId> text=<text> id=<id>

14. Удаление комментария презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removePresentationComment role=editor presentationId=<presentationId> id=<id>


15. Добавление графического элемента на слайд
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv addElementInSlide role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b elementType=shape figure=rectangle x=150 y=200 width=200 height=300 rotation=45 fillColor=red text="Текст элемента" boxShadow="1px 2px 4px red" opacity=1 borderStyle=dashed borderWidth=2px

16. Получение элементов слайда
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getSlideElements role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b

17. Изменение графического элемента
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv editSlideElement role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b elementType=shape figure=rectangle x=250 y=250 width=250 height=250 rotation=0 fillColor=yellow text="Измененный текст элемента" boxShadow="0px 0px 0px transparent" opacity=53 borderStyle=inset borderWidth=1px id=ec776269-d138-4e28-b6e5-6ba26d6c860f

18. Создание текстового элемента
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv addElementInSlide role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b elementType=content x=150 y=200 width=200 height=300 rotation=45 text="Текстовый элемента" fontSize=13px fontFamily=Roboto fontCase=uppercase letterSpacing=1rem lineSpacing=2rem

19. Изменение текстового элемента
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv editSlideElement role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=aa966972-04ad-464b-a14e-e19c4e73e8b9 elementType=content x=250 y=250 width=250 height=250 rotation=0 text="Измененный текстовый элемент" fontSize=10px fontFamily="Times new Roman" fontCase=lowercase letterSpacing=0rem lineSpacing=0em id=a09eb96e-a90b-4fc0-848c-54e6bc8defa0

20. Удаление текстового элемента
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removeSlideElement role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b id=4cfb62bf-205f-47b1-9292-da27527ee696 elementType=content

21. Удаление текстового элемента
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv removeSlideElement role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b id=4cfb62bf-205f-47b1-9292-da27527ee696 elementType=shape

22. Получение элемента слайда
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getSlideElementById role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e slideId=70afa35d-2c36-4648-8248-ca53e534ce6b id=4cfb62bf-205f-47b1-9292-da27527ee696 elementType=content


23. Оценивание презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv rateByMark role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e mark=good

24. Получение оценок презентации
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentationMarks role=editor presentationId=a45d013f-8771-4140-8112-9a7c5f68252e


25. Создание презентации на основе шаблона
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv createPresentation role=editor templateId=a45d013f-8771-4140-8112-9a7c5f68252e

26. Получение презентации со всеми опциями
java -jar -DenvironmentFile=./enviroment.properties -Dlog4j2.configurationFile=./log4j2.properties presitor-1.0.jar csv getPresentationById role=editor withComments=true withMarks=true withElements=true withSlides=true id=a45d013f-8771-4140-8112-9a7c5f68252e


///////                       Presentations                        \\\\\\\\\
	Create Presentation
datatype=<datatype>
method=createPresentation
role=<guest, editor>
name=<name>
fillColor=<fillColor>
fontFamily=<fontFamily>

	Get presentations
datatype=<datatype>
method=getPresentations
role=<guest, editor>

	Get presentation by id
datatype=<datatype>
method=getPresentationById
role=<guest, editor>
id=<id>

	Update presentation options
datatype=<datatype>
method=editPresentationOptions
role=<editor>
id=<id>
name=<name>
fillColor=<fillColor>
fontFamily=<fontFamily>

	Remove presentation by id
datatype=<datatype>
method=removePresentationById
role=<editor>
id=84b9be73-2059-491f-894a-4586c36bb782

///////                       Slides                         \\\\\\\\\

	Create presentation slide
datatype=<datatype>
method=createPresentationSlide
role=<editor>
presentationId=<presentationId>
name=<name>
index=<index>

	Get presentation slides
datatype=<datatype>
method=getPresentationSlides
role=<guest, editor>
presentationId=<presentationId>

	Get presentation slide by id
datatype=<datatype>
method=getSlideById
role=<guest, editor>
presentationId=<presentationId>
id=<id>

	Remove presentation slide by id
datatype=<datatype>
method=removePresentationSlideById
role=<editor>
presentationId=<presentationId>
id=<id>


	Edit presentation slide by id
datatype=<datatype>
method=editPresentationSlideById
role=<editor>
presentationId=<presentationId>
id=<id>
name=<name>
index=<index>

///////                    Comments                         \\\\\\\\\\\\

	Comment Presentation
datatype=<datatype>
method=commentPresentation
role=<guest, editor>
text=<text>
presentationId=<presentationId>


	getPresentationComments
datatype=csv
method=getPresentationComments
role=editor
presentationId=1085b983-0f16-44ca-8aff-346a2b86bd12

	editPresentationComment
datatype=csv
method=editPresentationComment
role=editor
id=5bb8caa6-e3b3-4780-8606-19e5dc052b78
presentationId=5ed29ceb-ccd8-4fdc-8c63-f3c60fb27cb0
text="Хорошая презентация"


	removePresentationComment
datatype=csv
method=removePresentationComment
role=editor
id=ba780cb0-1d35-4ccc-b411-59fa15ab42e1
presentationId=8f6314d8-acc3-4d58-98dc-19346081d97b


////////              Elements               \\\\\\\\\\\\\\


	addElementInSlide (Shape)
datatype=csv
method=addElementInSlide
role=editor
presentationId=fc6af4d0-61e9-443b-8aa5-3309690ca480
slideId=7aff8196-8f1b-4bad-95b4-7aeae7e1645d
elementType=shape
name="Мой любимый прямоугольник"
fillColor="green"
text="Мой любимый прямоугольник"
x=100
y=132
width=230
height=412
figure=rectangle
opacity=0.3
borderRadius="3px"
rotation=31


	addElementInSlide (Content)
datatype=csv
method=addElementInSlide
role=editor
presentationId=7aff8196-8f1b-4bad-95b4-7aeae7e1645d
slideId=fc6af4d0-61e9-443b-8aa5-3309690ca480
elementType=content
name="Мой любимый текст"
figure=rectangle


	editSlideElement
datatype=csv
method=editSlideElement
role=editor
presentationId=7aff8196-8f1b-4bad-95b4-7aeae7e1645d
slideId=fc6af4d0-61e9-443b-8aa5-3309690ca480
id=1b1cc523-a2f9-41b2-9397-cc369032482f
elementType=shape
fillColor=red
size=12px
case=upperCase
text="Мой не любимый прямоугольник"
opacity=23
borderColor=#40311d
x=14
y=423
rotation=41
borderStyle=ridge
borderWidth=13