package ru.sfedu.course_project.api;

import ru.sfedu.course_project.api.csv.CSVAssessmentMethods;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public interface DataProvider {
    /**
     * <ul>
     *    <li>role - (String) role of current user</li>
     *    <li>name - (String) name of presentation</li>
     *    <li>fillColor - (String) color of presentation background</li>
     *    <li>fontFamily - (String) font family of presentation</li>
     *    <li>templateId - (String) id of presentation, that will be template for new presentation</li>
     * </ul>
     *
     *
     * Create presentation
     * @param arguments arguments for presentation creation
     * @return Result class with Status (success or error) and returnValue (presentationId or error message)
     */
    public Result createPresentation (HashMap arguments);


    /**
     * Get all data source presentations
     * @return Result class with Status (success or error) and returnValue (Presentation data or error message)
     */
    public Result getPresentations ();


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>id - (String) id of presentation</li>
     *    <li>withSlides - (boolean) flag to get slides for presentation</li>
     *    <li>withElements - (boolean) flag to get elements in slides</li>
     *    <li>withComments - (boolean) flag to get comments for presentation</li>
     *    <li>withMarks - (boolean) flag to get assessments for presentation</li>
     * </ul>
     *
     * Get presentation data by id
     * @param arguments arguments to get presentation by id
     * @return Result class with Status (success or error) and returnValue (ArrayList of Presentation or error message)
     * @throws IOException
     */
    public Result getPresentationById (HashMap arguments) throws IOException;



    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>id - (String) id of presentation</li>
     * </ul>
     *
     * Remove presentation from data source by id
     * @param arguments arguments to remove presentation by id
     * @return Result class with Status (success or error) and returnValue (success or error message)
     * @throws IOException
     */
    public Result removePresentationById (HashMap arguments) throws  IOException;



    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>id - (String) id of presentation</li>
     *    <li>name - (String) name of presentation</li>
     *    <li>fillColor - (String) color of presentation background</li>
     *    <li>fontFamily - (String) font family of presentation</li>
     * </ul>
     *
     * Edit presentation data by id
     * @param arguments arguments to edit presentation data
     * @return Result class with Status (success or error) and returnValue (success or error message)
     * @throws IOException
     */
    public Result editPresentationOptions (HashMap arguments) throws IOException;


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>withElements - (boolean) flag to get elements in slides</li>
     * </ul>
     *
     * Get slides for provided presentation
     * @param arguments arguments to get slides for presentation
     * @return Result class with Status (success or error) and returnValue (ArrayList of Slide or error message)
     */
    public Result getPresentationSlides (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>name - (String) name of slide</li>
     *    <li>index - (String) position of slide in presentation</li>
     * </ul>
     *
     * Create slide for provided presentation
     * @param arguments arguments to get slides for presentation
     * @return Result class with Status (success or error) and returnValue (slideId or error message)
     */
    public Result createPresentationSlide (HashMap arguments);


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>id - (String) id of slide</li>
     * </ul>
     *
     * Remove slide in presentation by id
     * @param arguments arguments to remove slide from presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result removePresentationSlideById (HashMap arguments);


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>id - (String) id of slide</li>
     *    <li>name - (String) name of slide</li>
     *    <li>index - (String) position of slide in presentation</li>
     * </ul>
     *
     * Edit slide in presentation by id
     * @param arguments arguments to edit slide from presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result editPresentationSlideById (HashMap arguments);


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>id - (String) id of slide</li>
     * </ul>
     *
     * Get slide in presentation by id
     * @param arguments arguments to get slide from presentation
     * @return Result class with Status (success or error) and returnValue (Slide or error message)
     */
    public Result getSlideById (HashMap arguments);


    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     * </ul>
     *
     * Get comment of presentation
     * @param arguments arguments to get comment of presentation
     * @return Result class with Status (success or error) and returnValue (ArrayList of Comment or error message)
     */
    public Result getPresentationComments (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>text - (String) text of comment</li>
     * </ul>
     *
     * Set comment for presentation
     * @param arguments arguments to set comment for presentation
     * @return Result class with Status (success or error) and returnValue (commentId or error message)
     */
    public Result commentPresentation (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>id - (String) id of comment</li>
     *    <li>text - (String) text of comment</li>
     * </ul>
     *
     * Edit comment of presentation
     * @param arguments arguments to edit comment for presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result editPresentationComment (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>id - (String) id of comment</li>
     * </ul>
     *
     * Remove comment of presentation
     * @param arguments arguments to remove comment for presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result removePresentationCommentById (HashMap arguments);


    /**
     *
     * <strong>Create graphic element (Shape)</strong>
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>elementType - (ElementType) type of element</li>
     *    <li>figure - (Figure) type of element</li>
     *    <li>x - (int) x-coordinate of shape</li>
     *    <li>y - (int) y-coordinate of shape</li>
     *    <li>width - (int) width of shape</li>
     *    <li>height - (int) height of shape</li>
     *    <li>rotation - (int) rotation degrees of shape</li>
     *    <li>fillColor - (String) shape background color</li>
     *    <li>boxShadow - (String) shape shadow style</li>
     *    <li>borderStyle - (String) shape border style</li>
     *    <li>borderWidth - (String) shape border width</li>
     *    <li>text - (String) inner shape text</li>
     * </ul>
     *
     * <strong>Create text element (Content)</strong>
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>elementType - (ElementType) type of element</li>
     *    <li>x - (int) x-coordinate of shape</li>
     *    <li>y - (int) y-coordinate of shape</li>
     *    <li>width - (int) width of shape</li>
     *    <li>height - (int) height of shape</li>
     *    <li>rotation - (int) rotation degrees of shape</li>
     *    <li>fontSize - (String) size of text</li>
     *    <li>fontFamily - (String) font family for text</li>
     *    <li>fontCase - (FontCase) font case for text</li>
     *    <li>letterSpacing - (String) width of space between letters</li>
     *    <li>lineSpacing - (String) width of space between lines</li>
     *    <li>text - (String) inner shape text</li>
     * </ul>
     *
     * Add element on slide in presentation
     * @param arguments arguments to add element in presentation slide
     * @return Result class with Status (success or error) and returnValue (Element child class or error message)
     */
    public Result addElementInSlide (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>id - (String) id of comment</li>
     *    <li>elementType - (ElementType) type of element</li>
     * </ul>
     *
     * Remove comment of presentation
     * @param arguments arguments to remove element from presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result removeSlideElement (HashMap arguments);

    /**
     *
     * <strong>Edit graphic element (Shape)</strong>
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>id - (String) id of shape element</li>
     *    <li>elementType - (ElementType) type of element</li>
     *    <li>figure - (Figure) type of element</li>
     *    <li>x - (int) x-coordinate of shape</li>
     *    <li>y - (int) y-coordinate of shape</li>
     *    <li>width - (int) width of shape</li>
     *    <li>height - (int) height of shape</li>
     *    <li>rotation - (int) rotation degrees of shape</li>
     *    <li>fillColor - (String) shape background color</li>
     *    <li>boxShadow - (String) shape shadow style</li>
     *    <li>borderStyle - (String) shape border style</li>
     *    <li>borderWidth - (String) shape border width</li>
     *    <li>text - (String) inner shape text</li>
     * </ul>
     *
     * <strong>Edit text element (Content)</strong>
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>elementType - (ElementType) type of element</li>
     *    <li>x - (int) x-coordinate of shape</li>
     *    <li>y - (int) y-coordinate of shape</li>
     *    <li>width - (int) width of shape</li>
     *    <li>height - (int) height of shape</li>
     *    <li>rotation - (int) rotation degrees of shape</li>
     *    <li>fontSize - (String) size of text</li>
     *    <li>fontFamily - (String) font family for text</li>
     *    <li>fontCase - (FontCase) font case for text</li>
     *    <li>letterSpacing - (String) width of space between letters</li>
     *    <li>lineSpacing - (String) width of space between lines</li>
     *    <li>text - (String) inner shape text</li>
     * </ul>
     *
     * Add element on slide in presentation
     * @param arguments arguments to add element in presentation slide
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result editSlideElement (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     *    <li>id - (String) id of comment</li>
     *    <li>elementType - (ElementType) type of element</li>
     * </ul>
     *
     * Get element on slide
     * @param arguments arguments to get element on slide
     * @return Result class with Status (success or error) and returnValue (Element or error message)
     */
    public Result getSlideElementById (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>slideId - (String) id of slide</li>
     * </ul>
     *
     * Get all elements on slide
     * @param arguments arguments to get element on slide
     * @return Result class with Status (success or error) and returnValue (Element or error message)
     */
    public Result getSlideElements (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     *    <li>mark - (Mark) assessment of slide</li>
     * </ul>
     *
     * Rate presentation
     * @param arguments arguments to rate presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result rateByMark (HashMap arguments);

    /**
     *
     * <ul>
     *    <li>role - (Role) role of current user</li>
     *    <li>presentationId - (String) id of presentation</li>
     * </ul>
     *
     * Get marks of presentation
     * @param arguments arguments to rate presentation
     * @return Result class with Status (success or error) and returnValue (success or error message)
     */
    public Result getPresentationMarks (HashMap arguments);

    public Result removePresentationMarkById(HashMap arguments);

    public Result getMarkById (HashMap arguments);

    public Result editPresentationMark (HashMap arguments);
}
