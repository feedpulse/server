package de.feedpulse.specification;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchCriteria {
    private String key; //field name
    private String operation; //operation like ":" for equal or "<"/">" for less/greater than
    private Object value; //field value

    // constructor, getters and setters
}
