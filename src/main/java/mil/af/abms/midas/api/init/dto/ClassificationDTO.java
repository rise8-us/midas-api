package mil.af.abms.midas.api.init.dto;

import java.io.Serializable;

import lombok.Getter;

import mil.af.abms.midas.enums.Classification;

@Getter
public class ClassificationDTO implements Serializable {
    ClassificationDTO(String classificationString, String caveat) {
        Classification classification = Classification.valueOf(classificationString.toUpperCase());

        this.caveat = caveat;
        this.name = classification.getName();
        this.backgroundColor = classification.getBackgroundColor();
        this.textColor = classification.getTextColor();
    }
    private final String name;
    private final String backgroundColor;
    private final String textColor;
    private final String caveat;
}
