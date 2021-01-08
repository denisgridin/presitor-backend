package ru.sfedu.course_project.enums;

public enum ApiEndpoint {
    createPresentation,
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
    removePresentationComment,

    addElementInSlide,
    removeSlideElement,
    editSlideElement,
    getSlideElementById,
    getSlideElements,

    rateByMark,
    getPresentationMarks,
}
