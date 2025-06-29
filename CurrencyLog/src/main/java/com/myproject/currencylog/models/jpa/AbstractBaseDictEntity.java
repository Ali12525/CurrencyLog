package com.myproject.currencylog.models.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractBaseDictEntity extends AbstractBaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "num_code")
    private String numCode;

    @Column(name = "char_code")
    private String charCode;

    public String getName() {
        return name;
    }

    public String getNumCode() {
        return numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }
}