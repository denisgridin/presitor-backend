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