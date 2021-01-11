package ru.sfedu.course_project.enums;

public enum ApiEndpoint {
    createPresentation,
    // createPresentationByTemplate
    getPresentations,
    getPresentationById,
    removePresentationById,
    editPresentationOptions,

    getPresentationSlides,
    createPresentationSlide,
    removePresentationSlideById,
    editPresentationSlideById,
    getSlideById,

    getPresentationComments,
    commentPresentation,
    editPresentationComment,
    removePresentationCommentById,

    addElementInSlide,
    removeSlideElement,
    editSlideElement,
    getSlideElementById,
    getSlideElements,

    rateByMark,
    getPresentationMarks,
    // removePresentationMarkById
}
