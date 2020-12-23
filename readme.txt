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


	addElementInSlide
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