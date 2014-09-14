package com.t3coode.togg.services.dtos;

public abstract class IdentifiedBaseDTO extends BaseDTO {
    public static final String CREATED_WITH = "android:com.t3coode.togg";

    private Long id;

    IdentifiedBaseDTO() {
        super();
    }

    IdentifiedBaseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass().isInstance(o)) {
            IdentifiedBaseDTO identifiedEntity = (IdentifiedBaseDTO) o;
            if (identifiedEntity.getId() != null
                    && identifiedEntity.getId().equals(id)) {
                return true;
            }
        }
        return super.equals(o);
    }
}
